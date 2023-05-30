package su.akari.mnjtech.ui.component.player

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.OrientationEventListener
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.FitScreen
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PictureInPicture
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import su.akari.mnjtech.ui.component.ButtonStyle
import su.akari.mnjtech.ui.component.ButtonX
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.component.LongPressAnim
import su.akari.mnjtech.ui.component.MeasureTextWidth
import su.akari.mnjtech.ui.component.NoThumbSlider
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.state.PipModeListener
import su.akari.mnjtech.util.Log
import su.akari.mnjtech.util.noRippleClickable
import su.akari.mnjtech.util.prettyDuration
import su.akari.mnjtech.util.rememberState
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerController(
    playerState: PlayerState, navigationIcon: @Composable () -> Unit = {}
) {
    val activity = LocalActivity.current
    val view = LocalView.current

    var showController by playerState.showController
    var fullScreen by playerState.fullScreen

    LaunchedEffect(Unit) {
        playerState.showController()
        while (true) {
            delay(1.seconds)
            if (System.currentTimeMillis() - playerState.showControllerTime.value >= 4000) {
                showController = false
            }
        }
    }

    var detailPickerExpanded by rememberState(false)

    val hideDetailPicker = { detailPickerExpanded = false }

    LaunchedEffect(detailPickerExpanded) {
        val index = playerState.index.value - 1
        if (index > 0 && detailPickerExpanded) {
            playerState.episodeGridStates.second.scrollToItem(index)
        }
    }

    BackHandler(
        enabled = fullScreen
    ) {
        if (detailPickerExpanded) {
            hideDetailPicker()
        } else {
            playerState.exitFullScreen(activity)
        }
    }

    PipModeListener {
        if (it.isInPictureInPictureMode) {
            if (fullScreen) {
                playerState.exitFullScreen(activity)
            }
            showController = false
            fullScreen = true
        } else {
            fullScreen = false
            playerState.showController()
        }
    }
    var position by rememberState(0L)
    var longPressing by rememberState(false)
    var hSliding by rememberState(false)
    var hDragState by rememberState(0f)
    var targetPos by rememberState(0L)
    var hProgress by rememberState(0f)
    var brightnessMode by rememberState(false)
    var vSlidingBrightness by rememberState(false)
    var vSlidingVolume by rememberState(false)
    var targetBrightness by rememberState(0f)
    var targetVolume by rememberState(0f)
    var statusBarPadding: Modifier by rememberState(Modifier)
    var statusBarHeight by playerState.statusBarHeight

    with(WindowInsets.systemBars.asPaddingValues()) {
        calculateTopPadding().takeIf {
            it > statusBarHeight
        }?.let {
            statusBarHeight = it
        }
    }

    DisposableEffect(Unit) {
        val listener = object : OrientationEventListener(activity) {
            override fun onOrientationChanged(orientation: Int) {
                if (fullScreen) {
                    when (orientation) {
                        in 65..115 -> statusBarPadding = Modifier.padding(end = statusBarHeight)
                        in 245..295 -> statusBarPadding = Modifier.padding(start = statusBarHeight)
                        else -> {}
                    }
                } else statusBarPadding = Modifier
            }
        }.apply {
            enable()
        }
        onDispose {
            listener.disable()
        }
    }

    val videoDuration by playerState.videoDuration
    val positionState by playerState.observeVideoPositionState()
    LaunchedEffect(positionState, videoDuration) {
        if (videoDuration > 0) {
            (positionState.toFloat() / videoDuration.toFloat()).coerceIn(0.0f..1.0f)
        } else {
            0f
        }.let {
            if (!hSliding) hProgress = it
            position = (videoDuration * it).roundToLong()
        }
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    var width by rememberState(0f)

    LaunchedEffect(fullScreen) {
        val screenWidth = configuration.screenWidthDp.toFloat() * density.density
        val screenHeight = configuration.screenHeightDp.toFloat() * density.density
        width = if (fullScreen) max(screenWidth, screenHeight) else min(screenWidth, screenHeight)
    }

    Centered(
        modifier = Modifier.fillMaxSize()
    ) {
        Crossfade(
            targetState = detailPickerExpanded
        ) { expanded ->
            if (expanded) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier
                        .fillMaxSize()
                        .weight(2f)
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            }, indication = null
                        ) {
                            hideDetailPicker()
                        })
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .noRippleClickable {},
                        color = Color.Black.copy(alpha = 0.8f)
                    ) {
                        val episodes by playerState.episodes
                        LazyVerticalGrid(
                            state = playerState.episodeGridStates.second,
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(span = {
                                GridItemSpan(maxCurrentLineSpan)
                            }) {
                                Text(
                                    text = "选集 (${episodes.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(episodes) { episode ->
                                val isCurrent = playerState.index.value == episode.index
                                ButtonX(modifier = Modifier.fillMaxSize(),
                                    style = if (isCurrent) ButtonStyle.Filled else ButtonStyle.Outlined,
                                    onClick = {
                                        hideDetailPicker()
                                        playerState.setCurrentEpisode(episode.index)
                                    }) {
                                    Text(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        text = episode.index.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeGesturesPadding()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    playerState.togglePlay()
                                },
                                onTap = {
                                    playerState.toggleController()
                                },
                                onLongPress = {
                                    if (playerState.isPlaying.value) {
                                        longPressing = true
                                        showController = false
                                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                        playerState.setPlaySpeed(2.0f, false)
                                    }
                                },
                                onPress = {
                                    awaitRelease()
                                    if (longPressing) {
                                        longPressing = false
                                        playerState.setPlaySpeed(playerState.playSpeed.value)
                                    }
                                })
                        }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    hSliding = true
                                    hDragState = 0f
                                },
                                onDragEnd = {
                                    if (hSliding) playerState.player.seekTo(targetPos)
                                    hSliding = false
                                    hDragState = 0f
                                },
                                onDragCancel = {
                                    hSliding = false
                                    hDragState = 0f
                                },
                                onHorizontalDrag = { _, amount ->
                                    playerState.showController()
                                    hDragState += amount * 10
                                    targetPos =
                                        (playerState.player.currentPosition + hDragState.roundToLong() * 10).coerceIn(
                                            0, playerState.player.duration
                                        )
                                    hProgress = targetPos.toFloat() / videoDuration
                                })
                        }
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    brightnessMode = it.x / width < 0.5
                                    if (brightnessMode) {
                                        vSlidingBrightness = true
                                        targetBrightness = playerState.getBrightness()
                                    } else {
                                        vSlidingVolume = true
                                        targetVolume = playerState.getVolume()
                                    }
                                },
                                onDragEnd = {
                                    vSlidingBrightness = false
                                    vSlidingVolume = false
                                },
                                onDragCancel = {
                                    vSlidingBrightness = false
                                    vSlidingVolume = false
                                },
                                onVerticalDrag = { _, amount ->
                                    Log.e(amount)
                                    if (brightnessMode) {
                                        targetBrightness -= amount * playerState.maxBrightness / 1000
                                        targetBrightness =
                                            targetBrightness.coerceIn(0f, playerState.maxBrightness)
                                        playerState.setBrightness(targetBrightness)
                                    } else {
                                        targetVolume -= amount / playerState.maxVolume * 20
                                        targetVolume =
                                            targetVolume.coerceIn(0f, playerState.maxVolume)
                                        playerState.setVolume(targetVolume)
                                    }
                                })
                        },
                    content = {})
            }
        }
    }
    if (showController) {
        Controller(playerState = playerState,
            modifier = statusBarPadding,
            navigationIcon = navigationIcon,
            progress = hProgress,
            position = position,
            onSliding = { sli, pro ->
                hSliding = sli
                pro?.let {
                    hProgress = it
                    targetPos = (videoDuration * it).roundToLong()
                } ?: run {
                    playerState.player.seekTo(targetPos)
                }
            },
            onPickerClick = {
                detailPickerExpanded = !detailPickerExpanded
                playerState.toggleController()
            })
    }

    if (longPressing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp), color = Color.Black.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LongPressAnim()
                    Text(
                        text = "倍速播放中", style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    if (hSliding) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                    append(targetPos.prettyDuration())
                }
                append(" / ")
                append(playerState.player.duration.prettyDuration())
            }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
        )
    }

    if (vSlidingBrightness || vSlidingVolume) {
        Surface(
            shape = RoundedCornerShape(8.dp), color = Color.Black.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier
                    .width(200.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (vSlidingBrightness) {
                    Icon(imageVector = Icons.Outlined.LightMode, contentDescription = null)
                    NoThumbSlider(
                        value = targetBrightness,
                        onValueChange = {},
                        valueRange = 0f..playerState.maxBrightness,
                    )
                } else {
                    if (targetVolume == 0f) {
                        Icon(imageVector = Icons.Outlined.VolumeOff, contentDescription = null)
                    } else {
                        Icon(imageVector = Icons.Outlined.VolumeUp, contentDescription = null)
                    }
                    NoThumbSlider(
                        value = targetVolume,
                        onValueChange = {},
                        valueRange = 0f..playerState.maxVolume
                    )
                }
            }
        }
    }
}


@Composable
private fun Controller(
    modifier: Modifier,
    navigationIcon: @Composable () -> Unit,
    playerState: PlayerState,
    progress: Float,
    position: Long,
    onSliding: (sliding: Boolean, progress: Float?) -> Unit,
    onPickerClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current

    var speedExpanded by rememberState(false)

    Centered {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                navigationIcon()

                Text(
                    text = playerState.title.value,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    IconButton(onClick = {
                        playerState.enterPIP(activity)
                    }) {
                        Icon(Icons.Outlined.PictureInPicture, null)
                    }
                }

                IconButton(onClick = {
                    playerState.showController()
                    playerState.changeFitMode()
                }) {
                    Icon(
                        imageVector = when (playerState.fitMode.value) {
                            PlayerState.FitMode.FIT_VIDEO -> Icons.Outlined.AspectRatio
                            PlayerState.FitMode.FIT_SCREEN -> Icons.Outlined.FitScreen
                        }, contentDescription = null
                    )
                }
            }

            Centered(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (playerState.isLoading.value && !playerState.isPlaying.value) {
                    CircularProgressIndicator(
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            val playButton = remember {
                movableContentOf {
                    IconButton(onClick = {
                        playerState.showController()
                        playerState.togglePlay()
                    }) {
                        if (playerState.isPlaying.value) {
                            Icon(Icons.Outlined.Pause, null)
                        } else {
                            Icon(Icons.Outlined.PlayArrow, null)
                        }
                    }
                }
            }

            val fullScreen = remember {
                movableContentOf {
                    IconButton(onClick = {
                        playerState.showController()
                        playerState.player.play()
                        if (playerState.videoSize.value != VideoSize.UNKNOWN) {
                            playerState.toggleFullScreen(activity)
                        }
                    }) {
                        Icon(
                            imageVector = if (playerState.fullScreen.value) Icons.Outlined.FullscreenExit else Icons.Outlined.Fullscreen,
                            contentDescription = null
                        )
                    }
                }
            }

            val duration = playerState.videoDuration.value.prettyDuration()

            if (playerState.fullScreen.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MeasureTextWidth(duration) {
                        Text(
                            modifier = Modifier.width(it),
                            text = position.prettyDuration(),
                            textAlign = TextAlign.Right
                        )
                    }

                    Slider(modifier = Modifier
                        .weight(1f)
                        .height(4.dp),
                        value = progress,
                        onValueChange = {
                            playerState.showController()
                            onSliding(true, it)
                        },
                        onValueChangeFinished = {
                            onSliding(false, null)
                        })

                    Text(text = duration)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    playButton()

                    IconButton(
                        onClick = {
                            scope.launch {
                                playerState.showController()
                                playerState.skipNext(activity)
                            }
                        }, enabled = playerState.index.value < playerState.episodes.value.size
                    ) {
                        Icon(Icons.Outlined.SkipNext, null)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = onPickerClick, colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "选集")
                    }

                    TextButton(
                        onClick = {
                            playerState.showController()
                            speedExpanded = !speedExpanded
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        val speedMap = mapOf(
                            "2.0X" to 2.0f,
                            "1.5X" to 1.5f,
                            "1.25X" to 1.25f,
                            "1.0X" to 1.0f,
                            "0.75X" to 0.75f,
                            "0.5X" to 0.5f,
                        )
                        Text(
                            text = if (playerState.playSpeed.value == 1.0f) "倍速"
                            else speedMap.entries.find {
                                it.value == playerState.playSpeed.value
                            }!!.key
                        )
                        DropdownMenu(expanded = speedExpanded, onDismissRequest = {
                            speedExpanded = false
                        }) {
                            speedMap.forEach { entry ->
                                DropdownMenuItem(text = {
                                    Text(entry.key)
                                }, onClick = {
                                    speedExpanded = false
                                    playerState.setPlaySpeed(entry.value)
                                })
                            }
                        }
                    }

                    fullScreen()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    playButton()

                    Slider(modifier = Modifier.weight(1f), value = progress, onValueChange = {
                        playerState.showController()
                        onSliding(true, it)
                    }, onValueChangeFinished = {
                        onSliding(false, null)
                    })

                    MeasureTextWidth("$duration / $duration") {
                        Text(
                            modifier = Modifier.width(it),
                            text = position.prettyDuration() + " / " + duration,
                            textAlign = TextAlign.Center
                        )
                    }

                    fullScreen()
                }
            }

            LinearProgressIndicator(
                progress = playerState.observeBufferPct().value / 100.0f,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
            )
        }
    }

}