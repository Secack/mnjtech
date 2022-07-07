package su.akari.mnjtech.ui.local

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import su.akari.mnjtech.data.model.Network
import su.akari.mnjtech.data.model.profile.Profile
import su.akari.mnjtech.ui.activity.MainActivity

val LocalActivity = staticCompositionLocalOf<MainActivity> { error("Not init yet") }

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("Not init yet")
}

val LocalSelfData = compositionLocalOf { Profile.GUEST }

val LocalNetworkState = compositionLocalOf { Network.OFFLINE }

val LocalDarkMode = compositionLocalOf { false }