package su.akari.mnjtech

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.room.Room
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import su.akari.mnjtech.data.api.*
import su.akari.mnjtech.data.api.service.*
import su.akari.mnjtech.data.dao.AppDataBase
import su.akari.mnjtech.data.repo.NjtechRepo
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.data.repo.PreferenceRepo
import su.akari.mnjtech.ui.activity.CrashActivity
import su.akari.mnjtech.ui.activity.MainViewModel
import su.akari.mnjtech.ui.screen.index.IndexViewModel
import su.akari.mnjtech.ui.screen.jwgl.classroom.FreeRoomViewModel
import su.akari.mnjtech.ui.screen.jwgl.evaluation.EvaluationViewModel
import su.akari.mnjtech.ui.screen.jwgl.score.ScoreViewModel
import su.akari.mnjtech.ui.screen.login.LoginViewModel
import su.akari.mnjtech.ui.screen.login.LoginWebViewModel
import su.akari.mnjtech.ui.screen.online.announcement.OlAnnouncementViewModel
import su.akari.mnjtech.ui.screen.online.collect.OlCollectionViewModel
import su.akari.mnjtech.ui.screen.online.comment.OlCommentViewModel
import su.akari.mnjtech.ui.screen.online.detail.OlDetailViewModel
import su.akari.mnjtech.ui.screen.online.download.OlDownloadViewModel
import su.akari.mnjtech.ui.screen.online.index.OlIndexViewModel
import su.akari.mnjtech.ui.screen.online.notification.OlNotificationViewModel
import su.akari.mnjtech.ui.screen.online.record.OlRecordViewModel
import su.akari.mnjtech.ui.screen.online.search.OlSearchViewModel
import su.akari.mnjtech.ui.screen.setting.SettingViewModel
import su.akari.mnjtech.util.network.NjtechCookieJar
import su.akari.mnjtech.util.network.UserAgentInterceptor
import xcrash.ICrashCallback
import xcrash.XCrash
import java.io.File
import java.net.Inet4Address
import java.util.concurrent.TimeUnit

lateinit var RES: Resources
lateinit var PR: PreferenceRepo

class MNjtechApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // fuck TabRow
        Class.forName("androidx.compose.material3.TabRowKt")
            .getDeclaredField("ScrollableTabRowMinimumTabWidth").apply {
                isAccessible = true
                set(null, 0.0f)
            }

        startKoin {
            androidContext(this@MNjtechApp)
            modules(appModule)
        }

        PR = get()
        RES = resources
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        val handler = ICrashCallback { logPath, _ ->
            val file = File(logPath)
            startActivity(Intent(this, CrashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("stackTrace", file.readLines().joinToString("\n"))
            })
            file.deleteOnExit()
        }
        XCrash.init(
            this,
            XCrash.InitParameters().setAppVersion(BuildConfig.VERSION_NAME)
                .setLogDir(getExternalFilesDir("crash")?.path).setNativeCallback(handler)
                .setAnrCallback(handler).setJavaCallback(handler)
        )
    }
}

val appModule = module {
    single {
        Room.databaseBuilder(
            get(), AppDataBase::class.java, "mnjtech_db"
        ).build()
    }
    singleOf(::PreferenceRepo)

    single {
        OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            //.eventListenerFactory(LoggingEventListener.Factory())
            .addInterceptor(UserAgentInterceptor).addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }).dns { hostname -> // disable ipv6
                Dns.SYSTEM.lookup(hostname).filter { Inet4Address::class.java.isInstance(it) }
            }.cookieJar(NjtechCookieJar).build()
    }
    factory {
        Retrofit.Builder().client(get()).addConverterFactory(GsonConverterFactory.create())
    }

    single {
        get<Retrofit.Builder>().baseUrl(URL_I_NJTECH).build().create(NjtechService::class.java)
    }
    single {
        get<Retrofit.Builder>().baseUrl(URL_JWGL).build().create(JwglService::class.java)
    }
    singleOf(::NjtechParser)
    singleOf(::NjtechApiImpl) { bind<NjtechApi>() }
    singleOf(::NjtechRepo)

    single {
        get<Retrofit.Builder>().baseUrl(URL_ONLINE_API).build().create(OlService::class.java)
    }
    singleOf(::OlParser)
    singleOf(::OlApiImpl) { bind<OlApi>() }
    singleOf(::OlRepo)

    singleOf(::MainViewModel)
    viewModelOf(::IndexViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::LoginWebViewModel)
    viewModelOf(::SettingViewModel)
    viewModelOf(::EvaluationViewModel)
    viewModelOf(::ScoreViewModel)
    viewModelOf(::FreeRoomViewModel)

    viewModelOf(::OlIndexViewModel)
    viewModelOf(::OlDetailViewModel)
    viewModelOf(::OlSearchViewModel)
    viewModelOf(::OlAnnouncementViewModel)
    viewModelOf(::OlNotificationViewModel)
    viewModelOf(::OlCollectionViewModel)
    viewModelOf(::OlRecordViewModel)
    viewModelOf(::OlCommentViewModel)
    viewModelOf(::OlDownloadViewModel)
}