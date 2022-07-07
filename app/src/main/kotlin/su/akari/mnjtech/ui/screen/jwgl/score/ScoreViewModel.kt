package su.akari.mnjtech.ui.screen.jwgl.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import su.akari.mnjtech.data.model.jwgl.ScoreListPage
import su.akari.mnjtech.data.repo.NjtechRepo
import su.akari.mnjtech.util.DataStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class ScoreViewModel(private val njtechRepo: NjtechRepo) : ViewModel() {
    val scoreListFlow = DataStateFlow<ScoreListPage>()

    fun getScoreList() {
        njtechRepo.getScoreList(null, null).collectAsStateFlow(viewModelScope, scoreListFlow)
    }

}