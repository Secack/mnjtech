package su.akari.mnjtech.ui.component.player

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.util.Rational
import android.view.View
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.delay
import su.akari.mnjtech.util.findActivity
import su.akari.mnjtech.util.maxBrightness
import java.lang.ref.WeakReference
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

data class Episode(
    val index: Int,
    val id: Int,
    val name: String,
    val mediaItem: MediaItem
)

class PlayerState(
    val context: Context,
    builder: (Context) -> ExoPlayer
) : Player.Listener {
    val player = builder(context).also {
        it.addListener(this)
    }

    var surfaceView: WeakReference<View>? = null

    lateinit var episodeListState: LazyListState
    lateinit var episodeGridStates: Pair<LazyGridState, LazyGridState>

    val title = mutableStateOf("")

    val index = mutableStateOf(-1)

    val episodes = mutableStateOf(emptyList<Episode>())

    val isPlaying = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val videoSize = mutableStateOf(VideoSize.UNKNOWN)
    val playbackState = mutableStateOf(Player.STATE_IDLE)
    val videoDuration = mutableStateOf(0L)
    val playSpeed = mutableStateOf(1.0f)

    val showController = mutableStateOf(true)
    val showControllerTime = mutableStateOf(0L)
    val fullScreen = mutableStateOf(false)
    val fitMode = mutableStateOf(FitMode.FIT_VIDEO)

    val statusBarHeight = mutableStateOf(0.dp)

    private val audioManager: AudioManager = context.getSystemService(AudioManager::class.java)

    private val window = context.findActivity().window

    enum class FitMode {
        FIT_VIDEO, FIT_SCREEN
    }

    fun changeFitMode() {
        surfaceView?.get()?.let { view ->
            val playerView = view as StyledPlayerView
            this.fitMode.value = when (this.fitMode.value) {
                FitMode.FIT_VIDEO -> FitMode.FIT_SCREEN
                FitMode.FIT_SCREEN -> FitMode.FIT_VIDEO
            }
            when (fitMode.value) {
                FitMode.FIT_VIDEO -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
                FitMode.FIT_SCREEN -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            }
        }
    }

    fun initEpisodes(episodes: List<Episode>) {
        this.episodes.value = episodes
    }

    fun skipNext(activity: Activity) {
        if (index.value >= episodes.value.size) {
            exitFullScreen(activity)
        } else {
            setCurrentEpisode(index.value + 1, 0)
        }
    }

    fun setCurrentEpisode(index: Int, position: Long = 0, autoPlay: Boolean = true) {
        val target = episodes.value.find { it.index == index } ?: episodes.value[0]
        this.index.value = target.index
        this.title.value = target.name
        player.setMediaItem(target.mediaItem)
        player.prepare()
        player.seekTo(position)
        if (autoPlay)
            player.play()
        else
            player.pause()
    }

    fun enterPIP(activity: Activity) {
        if (videoSize.value == VideoSize.UNKNOWN) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fullScreen.value = true
            activity.enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(
                        Rational(
                            videoSize.value.width,
                            videoSize.value.height
                        ).coerceAtMost(
                            Rational(239, 100)
                        ).coerceAtLeast(
                            Rational(100, 239)
                        )
                    )
                    .build()
            )
        }
    }

    fun togglePlay() {
        if (playbackState.value == Player.STATE_IDLE) {
            player.prepare()
        }

        if (isPlaying.value) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun setPlaySpeed(playSpeed: Float, affectGlobal: Boolean = true) {
        if (affectGlobal)
            this.playSpeed.value = playSpeed
        player.setPlaybackSpeed(playSpeed)
    }

    fun showController() {
        showController.value = true
        showControllerTime.value = System.currentTimeMillis()
    }

    fun toggleController() {
        if (!showController.value) {
            showControllerTime.value = System.currentTimeMillis()
            showController.value = true
        } else {
            showController.value = false
        }
    }

    fun toggleFullScreen(activity: Activity) {
        if (fullScreen.value) {
            exitFullScreen(activity)
        } else {
            enterFullScreen(activity)
        }
    }

    fun enterFullScreen(activity: Activity) {
        fullScreen.value = true
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun exitFullScreen(activity: Activity) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
        }
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        fullScreen.value = false
    }

    val maxVolume =
        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()

    fun getVolume() =
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()

    fun setVolume(volume: Float) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume.roundToInt(), 0)
    }

    val maxBrightness = context.maxBrightness

    private val originBrightness
        get() = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        ).toFloat()

    fun getBrightness() =
        window.attributes.screenBrightness.takeIf { it != -1f }?.times(maxBrightness)
            ?: originBrightness

    fun setBrightness(brightness: Float) {
        window.attributes = window.attributes.apply {
            screenBrightness = brightness / maxBrightness
        }
    }

    fun resumeBrightness() {
        window.attributes = window.attributes.apply {
            screenBrightness = -1f
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        this.isPlaying.value = isPlaying
        surfaceView?.get()?.keepScreenOn = isPlaying
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        this.isLoading.value = isLoading
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        this.videoSize.value = videoSize
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_READY) {
            this.videoDuration.value = player.duration
        }
        this.playbackState.value = playbackState
    }
}

@Composable
fun PlayerState.observeVideoPositionState() = produceState(
    initialValue = player.currentPosition
) {
    while (true) {
        value = player.currentPosition
        delay(0.5.seconds)
    }
}

@Composable
fun PlayerState.observeBufferPct() = produceState(
    initialValue = player.bufferedPercentage
) {
    while (true) {
        value = player.bufferedPercentage
        delay(0.5.seconds)
    }
}
