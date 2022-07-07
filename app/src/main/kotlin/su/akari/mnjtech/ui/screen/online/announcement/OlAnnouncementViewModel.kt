package su.akari.mnjtech.ui.screen.online.announcement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import su.akari.mnjtech.data.model.online.AnnouncementListPage
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.util.OlStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class OlAnnouncementViewModel(private val olRepo: OlRepo) : ViewModel() {
    val announcementListFlow = OlStateFlow<AnnouncementListPage>()

    init {
        getAnnouncementList()
    }

    fun getAnnouncementList() {
        olRepo.getAnnouncementList().collectAsStateFlow(viewModelScope, announcementListFlow)
    }
}