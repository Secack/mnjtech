package su.akari.mnjtech.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.util.animateRotate
import kotlinx.coroutines.launch

@Composable
fun Md3TopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = WindowInsets.statusBars.asPaddingValues(),
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    appBarStyle: AppBarStyle = AppBarStyle.Small,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val colors = when (appBarStyle) {
        AppBarStyle.Small -> TopAppBarDefaults.smallTopAppBarColors()
        AppBarStyle.Medium -> TopAppBarDefaults.mediumTopAppBarColors()
        AppBarStyle.Large -> TopAppBarDefaults.largeTopAppBarColors()
        AppBarStyle.CenterAligned -> TopAppBarDefaults.centerAlignedTopAppBarColors()
    }
    val scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    val appBarContainerColor by colors.containerColor(scrollFraction)

    Surface(
        modifier = modifier,
        color = appBarContainerColor
    ) {
        when (appBarStyle) {
            AppBarStyle.Small -> {
                SmallTopAppBar(
                    modifier = Modifier.padding(contentPadding),
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }
            AppBarStyle.Medium -> {
                MediumTopAppBar(
                    modifier = Modifier.padding(contentPadding),
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }
            AppBarStyle.Large -> {
                LargeTopAppBar(
                    modifier = Modifier.padding(contentPadding),
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }
            AppBarStyle.CenterAligned -> {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(contentPadding),
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }
        }
    }
}

@Composable
fun Md3BottomNavigation(
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        tonalElevation = 3.dp
    ) {
        CompositionLocalProvider(
            LocalAbsoluteTonalElevation provides LocalAbsoluteTonalElevation.current - 3.dp
        ) {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding()
            ) {
                content()
            }
        }
    }
}

@Composable
fun MenuIcon(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    IconButton(
        onClick = {
            scope.launch {
                drawerState.open()
            }
        }
    ) {
        Icon(
            modifier = Modifier.animateRotate(if (drawerState.isOpen) 90f else 0f),
            imageVector = Icons.Outlined.Menu,
            contentDescription = null
        )
    }
}

@Composable
fun BackIcon() {
    val navController = LocalNavController.current
    IconButton(
        onClick = {
            navController.popBackStack()
        }
    ) {
        Icon(Icons.Outlined.ArrowBack, null)
    }
}

enum class AppBarStyle {
    Small,
    Medium,
    Large,
    CenterAligned
}