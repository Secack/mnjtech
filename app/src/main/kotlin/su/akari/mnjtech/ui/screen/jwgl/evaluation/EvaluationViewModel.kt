package su.akari.mnjtech.ui.screen.jwgl.evaluation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import okhttp3.ResponseBody
import su.akari.mnjtech.data.repo.NjtechRepo
import su.akari.mnjtech.util.DataStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class EvaluationViewModel(private val njtechRepo: NjtechRepo) : ViewModel() {
    val evaluationListFlow = DataStateFlow<Map<String, suspend () -> ResponseBody>>()

    fun getEvaluationList() {
        njtechRepo.getEvaluationList()
            .collectAsStateFlow(viewModelScope, evaluationListFlow)
    }
}