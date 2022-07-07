package su.akari.mnjtech.ui.screen.index

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import su.akari.mnjtech.data.model.profile.Profile
import su.akari.mnjtech.ui.component.LoadingAnim
import su.akari.mnjtech.ui.component.Md3BottomNavigation
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.screen.index.page.CurriculumPage
import su.akari.mnjtech.ui.screen.index.page.FuncPage
import su.akari.mnjtech.ui.screen.index.page.HomePage
import su.akari.mnjtech.ui.screen.index.page.SelfPage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel

@Composable
fun IndexScreen() {
    val viewModel by viewModel<IndexViewModel>()
    val activity = LocalActivity.current
    val mainViewModel = activity.viewModel
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val screenType = calculateWindowSizeClass(activity)

    Crossfade(targetState = mainViewModel.userDataFetched && mainViewModel.userData != Profile.GUEST) { loaded ->
        if (loaded) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (screenType.widthSizeClass != WindowWidthSizeClass.Compact) {
                    //TODO: adjust to orientation
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    count = 4,
                    userScrollEnabled = false
                ) { page ->
                    when (page) {
                        0 -> HomePage()
                        1 -> CurriculumPage(viewModel)
                        2 -> FuncPage()
                        3 -> SelfPage(viewModel)
                    }
                }
                BottomBar(currentPage = pagerState.currentPage, scrollToPage = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(it)
                    }
                })
            }
        } else {
            LoadingAnim()
        }
    }
}

@Composable
private fun BottomBar(currentPage: Int, scrollToPage: (Int) -> Unit) {
    Md3BottomNavigation {
        NavigationBarItem(selected = currentPage == 0, onClick = {
            scrollToPage(0)
        }, icon = {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = null)
        }, label = {
            Text(text = "首页")
        }, alwaysShowLabel = false
        )
        NavigationBarItem(selected = currentPage == 1, onClick = {
            scrollToPage(1)
        }, icon = {
            Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null)
        }, label = {
            Text(text = "课表")
        }, alwaysShowLabel = false
        )
        NavigationBarItem(selected = currentPage == 2, onClick = {
            scrollToPage(2)
        }, icon = {
            Icon(imageVector = Icons.Outlined.Category, contentDescription = null)
        }, label = {
            Text(text = "功能")
        }, alwaysShowLabel = false
        )

        NavigationBarItem(selected = currentPage == 3, onClick = {
            scrollToPage(3)
        }, icon = {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = null)
        }, label = {
            Text(text = "我的")
        }, alwaysShowLabel = false
        )
    }
}