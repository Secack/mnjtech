package su.akari.mnjtech.ui.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.*
import su.akari.mnjtech.R
import su.akari.mnjtech.util.noRippleClickable

@Composable
fun LoadingAnim() {
    Centered(
        modifier = Modifier.fillMaxSize()
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.fading_cubes_loader)
        )
        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth(0.33f)
                .aspectRatio(1f),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
    }
}

@Composable
fun ErrorAnim(text: String, onClick: () -> Unit) {
    Centered(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable(onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.error)
            )
            LottieAnimation(
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .aspectRatio(1f),
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
            Text(
                text = "点击以重试",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LongPressAnim() {
    val context = LocalContext.current
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.long_press_forward
        )
    )
    val bitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.long_press)
    }
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(LottieProperty.IMAGE, bitmap),
    )
    LottieAnimation(
        modifier = Modifier.size(30.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever,
        dynamicProperties = dynamicProperties
    )
}