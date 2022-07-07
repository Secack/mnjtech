package su.akari.mnjtech.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import soup.compose.material.motion.materialSharedAxisZIn
import soup.compose.material.motion.materialSharedAxisZOut
import su.akari.mnjtech.data.model.profile.Profile
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.screen.index.IndexScreen
import su.akari.mnjtech.ui.screen.jwgl.classroom.FreeRoomScreen
import su.akari.mnjtech.ui.screen.jwgl.evaluation.EvaluationDialog
import su.akari.mnjtech.ui.screen.jwgl.score.ScoreScreen
import su.akari.mnjtech.ui.screen.login.LoginScreen
import su.akari.mnjtech.ui.screen.login.LoginWebScreen
import su.akari.mnjtech.ui.screen.online.announcement.OlAnnouncementScreen
import su.akari.mnjtech.ui.screen.online.collect.OlCollectionScreen
import su.akari.mnjtech.ui.screen.online.comment.OlCommentScreen
import su.akari.mnjtech.ui.screen.online.detail.OlDetailScreen
import su.akari.mnjtech.ui.screen.online.download.OlDownloadScreen
import su.akari.mnjtech.ui.screen.online.index.OlIndexScreen
import su.akari.mnjtech.ui.screen.online.message.OlMessageScreen
import su.akari.mnjtech.ui.screen.online.notification.OlNotificationScreen
import su.akari.mnjtech.ui.screen.online.record.OlRecordScreen
import su.akari.mnjtech.ui.screen.online.search.OlSearchScreen
import su.akari.mnjtech.ui.screen.setting.SettingScreen

@Composable
fun NavGraph() {
    val navController = LocalNavController.current
    val density = LocalDensity.current
    val viewModel = LocalActivity.current.viewModel

    AnimatedNavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = Destinations.Index,
        enterTransition = {
            materialSharedAxisZIn().transition(true, density)
        },
        exitTransition = {
            materialSharedAxisZOut().transition(true, density)
        },
        popEnterTransition = {
            materialSharedAxisZIn().transition(false, density)
        },
        popExitTransition = {
            materialSharedAxisZOut().transition(false, density)
        }
    ) {
        composable(Destinations.Index) {
            LaunchedEffect(viewModel.userData, viewModel.userDataFetched) {
                if (viewModel.userDataFetched && viewModel.userData == Profile.GUEST) {
                    navController.navigate(Destinations.Login) {
                        popUpTo(0)
                    }
                }
            }
            IndexScreen()
        }

        composable(
            route = "${Destinations.Login}?${DestinationArgs.AutoLogin}={${DestinationArgs.AutoLogin}}",
            arguments = listOf(
                navArgument(DestinationArgs.AutoLogin) {
                    defaultValue = -1
                    type = NavType.IntType
                }
            )
        ) {
            LoginScreen()
        }

        composable(Destinations.LoginWeb) {
            LoginWebScreen()
        }

        composable(Destinations.Setting) {
            SettingScreen()
        }

        dialog(Destinations.Evaluation) {
            EvaluationDialog()
        }

        composable(Destinations.Score) {
            ScoreScreen()
        }

        composable(Destinations.FreeRoom) {
            FreeRoomScreen()
        }

        composable(Destinations.OlIndex) {
            OlIndexScreen()
        }

        composable("${Destinations.OlDetail}/{${DestinationArgs.VideoId}}" +
                "?${DestinationArgs.Index}={${DestinationArgs.Index}}" +
                "&${DestinationArgs.Time}={${DestinationArgs.Time}}" +
                "&${DestinationArgs.CacheMode}={${DestinationArgs.CacheMode}}",
            arguments = listOf(
                navArgument(DestinationArgs.VideoId) {
                    type = NavType.IntType
                },
                navArgument(DestinationArgs.Index) {
                    defaultValue = 1
                    type = NavType.IntType
                },
                navArgument(DestinationArgs.Time) {
                    defaultValue = -1L
                    type = NavType.LongType
                },
                navArgument(DestinationArgs.CacheMode) {
                    defaultValue = false
                    type = NavType.BoolType
                }
            )
        ) {
            OlDetailScreen()
        }

        composable("${Destinations.OlSearch}?${DestinationArgs.Category}={${DestinationArgs.Category}}",
            arguments = listOf(
                navArgument(DestinationArgs.Category) {
                    nullable = true
                    type = NavType.StringType
                }
            )
        ) {
            OlSearchScreen()
        }

        composable(Destinations.OlAnnouncement) {
            OlAnnouncementScreen()
        }

        composable(Destinations.OlNotification) {
            OlNotificationScreen()
        }

        composable(Destinations.OlCollection) {
            OlCollectionScreen()
        }

        composable(Destinations.OlRecord) {
            OlRecordScreen()
        }

        composable(Destinations.OlComment) {
            OlCommentScreen()
        }

        composable(Destinations.OlMessage) {
            OlMessageScreen()
        }

        composable(
            route = Destinations.OlDownload,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DestinationDeepLink.DownloadPattern
                }
            )
        ) {
            OlDownloadScreen()
        }
    }
}