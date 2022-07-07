package su.akari.mnjtech.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.task.DownloadTask
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import su.akari.mnjtech.R
import su.akari.mnjtech.data.dao.AppDataBase
import su.akari.mnjtech.data.model.online.DownloadEntry
import su.akari.mnjtech.data.model.online.DownloadedVideo
import su.akari.mnjtech.ui.activity.MainActivity
import su.akari.mnjtech.ui.navigation.DestinationDeepLink
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.util.FileSize
import su.akari.mnjtech.util.fromJson
import su.akari.mnjtech.util.toJson
import java.io.File

class VideoDownloadService : Service() {
    private val database: AppDataBase by inject()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val dNotification =
        NotificationCompat.Builder(this, Destinations.OlDownload).setSmallIcon(R.drawable.download)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

    private val fNotification =
        NotificationCompat.Builder(this, Destinations.OlDownload).setSmallIcon(R.drawable.download)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()

        Aria.download(this).register()

        Aria.get(this).downloadConfig.apply {
            isConvertSpeed = true
            threadNum = 1
            maxTaskNum = 1
            isUseBlock = false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(
                Destinations.OlDownload, "南工在线缓存", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setSound(null, null)
            })
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val entry = intent.getParcelableExtra<DownloadEntry>("entry")!!

        fun download() {
            Aria.download(this).load(entry.path).setFilePath(
                File(
                    "${getExternalFilesDir(Environment.DIRECTORY_MOVIES)}/${entry.videoTopic.id}".also {
                        File(it).run {
                            if (exists().not()) mkdir()
                        }
                    }, "${entry.videoTopic.name} - ${entry.name}"
                ).path
            ).setExtendField(
                entry.toJson()
            ).ignoreFilePathOccupy().ignoreCheckPermissions().create()
        }

        Aria.download(this).getFirstDownloadEntity(entry.path)?.let {
            if (it.isComplete.not()) {
                if (File(it.filePath).canWrite()) {
                    Aria.download(this).load(it.id).ignoreCheckPermissions().resume()
                } else {
                    download()
                }
            } else {
                scope.launch {
                    database.downloadedVideoDao().getVideoById(entry.id) ?: download()
                }
            }
        } ?: download()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        Aria.download(this).unRegister()
    }

    @Download.onTaskStart
    fun onStart(task: DownloadTask) {
        startForeground(1, dNotification.build().apply {
            flags = NotificationCompat.FLAG_ONGOING_EVENT.or(NotificationCompat.FLAG_NO_CLEAR)
        })
    }

    @Download.onTaskRunning
    fun onRunning(task: DownloadTask) {
        val entry = task.extendField.fromJson<DownloadEntry>()
        val cur = FileSize(task.currentProgress)
        val max = FileSize(task.fileSize)
        dNotification.setContentTitle("${entry.videoTopic.name} - ${entry.name}")
            .setContentText("$cur / $max").setProgress(100, task.percent, false).setContentIntent(
                PendingIntent.getActivity(
                    this, 0, Intent(this, MainActivity::class.java).apply {
                        data = Uri.parse(DestinationDeepLink.DownloadPattern)
                    }, PendingIntent.FLAG_IMMUTABLE
                )
            ).setWhen(System.currentTimeMillis()).build().apply {
                notificationManager.notify(1, this)
            }
    }

    @Download.onTaskComplete
    fun onComplete(task: DownloadTask) {
        val entry = task.extendField.fromJson<DownloadEntry>()
        scope.apply {
            launch {
                database.videoTopicDao().insertTopic(entry.videoTopic)
            }
            launch {
                database.downloadedVideoDao().insertVideo(
                    DownloadedVideo(
                        id = entry.id,
                        topicId = entry.videoTopic.id,
                        index = entry.index,
                        name = entry.name,
                        filePath = task.filePath,
                        fileSize = task.fileSize,
                    )
                )
            }
        }
        notificationManager.notify(
            2,
            fNotification.setContentText("${entry.videoTopic.name} - ${entry.name} 已完成")
                .setContentIntent(
                    PendingIntent.getActivity(
                        this, 0, Intent(this, MainActivity::class.java).apply {
                            data = Uri.parse(DestinationDeepLink.DownloadPattern)
                        }, PendingIntent.FLAG_IMMUTABLE
                    )
                ).setWhen(System.currentTimeMillis()).build()
        )

        if (Aria.download(this).dRunningTask == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                stopForeground(true)
            }
            notificationManager.cancel(1)
        }
    }

    @Download.onTaskStop
    fun onStop(task: DownloadTask) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        notificationManager.cancel(1)
    }

    override fun onBind(intent: Intent?): IBinder = DownloadBinder()

    inner class DownloadBinder : Binder() {
        fun getDownloadingTasks(): List<DownloadEntity> =
            Aria.download(this).dRunningTask ?: emptyList()
    }
}
