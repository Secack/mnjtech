package su.akari.mnjtech.ui.screen.online.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import su.akari.mnjtech.data.model.online.*
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.util.OlStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class OlIndexViewModel(
    private val olRepo: OlRepo
) : ViewModel() {
    var carouselListFlow = OlStateFlow<VideoListPage>()
    val weeklyHottestFlow = OlStateFlow<WeeklyHottest>()
    val latestVideoFlow = OlStateFlow<LatestVideo>()
    val userDetailFlow = OlStateFlow<User>()
    val latestAnnouncementFlow = OlStateFlow<Announcement>()
    val profileCountFlow = OlStateFlow<ProfileCount>()
    var notifyCount by mutableStateOf(0)

    fun init() {
        viewModelScope.also { scope ->
            with(olRepo) {
                getCarouselList().collectAsStateFlow(scope, carouselListFlow)
                getWeeklyHottest().collectAsStateFlow(scope, weeklyHottestFlow)
                getLatestVideo().collectAsStateFlow(scope, latestVideoFlow)
                getUserDetail().collectAsStateFlow(scope, userDetailFlow)
                getLatestAnnouncement().collectAsStateFlow(scope, latestAnnouncementFlow)
            }
        }
    }

    fun getProfileCount() {
        olRepo.getProfileCount().collectAsStateFlow(viewModelScope, profileCountFlow)
    }
}
