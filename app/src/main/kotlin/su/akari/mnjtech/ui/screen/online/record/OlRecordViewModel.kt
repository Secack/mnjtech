package su.akari.mnjtech.ui.screen.online.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import su.akari.mnjtech.data.model.online.RecordListPage
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.util.OlStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class OlRecordViewModel(
    val olRepo: OlRepo
) : ViewModel() {
    val recordListFlow = OlStateFlow<RecordListPage>()

    fun getRecordList() {
        olRepo.getRecordList().collectAsStateFlow(viewModelScope, recordListFlow)
    }

    fun deleteRecord(videoId: Int, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            runCatching {
                olRepo.deleteRecord(videoId)
                getRecordList()
            }.getOrElse(onError)
        }
    }
}