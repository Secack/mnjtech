package su.akari.mnjtech.ui.component

import android.graphics.Typeface
import android.os.Build.VERSION.SDK_INT
import android.text.Html
import android.text.style.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.core.text.getSpans
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import su.akari.mnjtech.util.Base64ImageFetcher

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    imageFillWidth: Boolean = false,
) {
    BoxWithConstraints {
        val inlineContent = remember {
            mutableStateMapOf<String, InlineTextContent>()
        }
        val annotatedString = if (SDK_INT < 24) {
            Html.fromHtml(text)
        } else {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        }.run {
            buildAnnotatedString {
                append(this@run.toString())
                getSpans<URLSpan>().forEach { urlSpan ->
                    val start = getSpanStart(urlSpan)
                    val end = getSpanEnd(urlSpan)
                    addStyle(
                        SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        ), start, end
                    )
                    addStringAnnotation("url", urlSpan.url, start, end)
                }
                getSpans<ForegroundColorSpan>().forEach { colorSpan ->
                    val start = getSpanStart(colorSpan)
                    val end = getSpanEnd(colorSpan)
                    addStyle(SpanStyle(color = Color(colorSpan.foregroundColor)), start, end)
                }
                getSpans<StyleSpan>().forEach { styleSpan ->
                    val start = getSpanStart(styleSpan)
                    val end = getSpanEnd(styleSpan)
                    when (styleSpan.style) {
                        Typeface.BOLD -> addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            start,
                            end
                        )
                        Typeface.ITALIC -> addStyle(
                            SpanStyle(fontStyle = FontStyle.Italic),
                            start,
                            end
                        )
                        Typeface.BOLD_ITALIC -> addStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            ), start, end
                        )
                    }
                }
                getSpans<UnderlineSpan>().forEach { underlineSpan ->
                    val start = getSpanStart(underlineSpan)
                    val end = getSpanEnd(underlineSpan)
                    addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                }
                getSpans<StrikethroughSpan>().forEach { strikethroughSpan ->
                    val start = getSpanStart(strikethroughSpan)
                    val end = getSpanEnd(strikethroughSpan)
                    addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
                }
                getSpans<ImageSpan>().forEach { imageSpan ->
                    val start = getSpanStart(imageSpan)
                    val end = getSpanEnd(imageSpan)
                    val url = imageSpan.source!!
                    addStringAnnotation(
                        tag = "androidx.compose.foundation.text.inlineContent",
                        annotation = url,
                        start = start,
                        end = end
                    )
                    val context = LocalContext.current
                    val density = LocalDensity.current
                    context.imageLoader.enqueue(
                        ImageRequest.Builder(context).run {
                            data(url)
                            if (url.startsWith("data")) {
                                fetcherFactory(Base64ImageFetcher.Factory)
                            }
                            target { drawable ->
                                inlineContent[url] = InlineTextContent(
                                    with(density) {
                                        val width = drawable.intrinsicWidth
                                        val times = if (imageFillWidth)
                                            maxWidth.toPx().div(width)
                                        else 1f
                                        Placeholder(
                                            width = width.times(times).toSp(),
                                            height = drawable.intrinsicHeight.times(times)
                                                .toSp(),
                                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                                        )
                                    }
                                ) {
                                    Image(
                                        modifier = Modifier.fillMaxSize(),
                                        painter = rememberAsyncImagePainter(drawable),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            build()
                        }
                    )
                }
            }
        }
        val uriHandler = LocalUriHandler.current
        val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
        SelectionContainer {
            Text(
                modifier = modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { pos ->
                            layoutResult.value?.let { layoutResult ->
                                val position = layoutResult.getOffsetForPosition(pos)
                                annotatedString.getStringAnnotations(position, position)
                                    .firstOrNull()
                                    ?.takeIf { it.tag == "url" }?.let {
                                        uriHandler.openUri(it.item)
                                    }
                            }
                        }
                    )
                },
                text = annotatedString,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                inlineContent = inlineContent,
                onTextLayout = {
                    layoutResult.value = it
                    onTextLayout(it)
                },
                style = style
            )
        }
    }
}