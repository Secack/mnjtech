package su.akari.mnjtech.ui.screen.jwgl.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import su.akari.mnjtech.data.model.jwgl.RoomListPage
import su.akari.mnjtech.data.model.jwgl.SectionList
import su.akari.mnjtech.data.repo.NjtechRepo
import su.akari.mnjtech.util.DataStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class FreeRoomViewModel(private val njtechRepo: NjtechRepo) : ViewModel() {
    val sectionListFlow = DataStateFlow<SectionList>()
    val roomListFlow = DataStateFlow<RoomListPage>()

    fun getSectionList(campus: Int, year: Int, term: Int) {
        njtechRepo.getSectionList(campus, year, term)
            .collectAsStateFlow(viewModelScope, sectionListFlow)
    }

    fun getRoomList(
        campus: Int,
        year: Int,
        term: Int,
        week: Int,
        dayOfWeek: Int,
        building: String,
        classes: IntRange
    ) {
        njtechRepo.getRoomList(
            campus = campus,
            year = year,
            term = term,
            week = week,
            dayOfWeek = dayOfWeek,
            building = building,
            classes = classes
        ).collectAsStateFlow(viewModelScope, roomListFlow)
    }
}