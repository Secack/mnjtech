package su.akari.mnjtech.ui.screen.online.collect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import su.akari.mnjtech.data.model.online.CollectionListPage
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.util.OlStateFlow
import su.akari.mnjtech.util.collectAsStateFlow
import kotlinx.coroutines.launch

class OlCollectionViewModel(
    val olRepo: OlRepo
) : ViewModel() {
    val collectionListFlow = OlStateFlow<CollectionListPage>()

    fun getCollectionList() {
        olRepo.getCollectionList().collectAsStateFlow(viewModelScope, collectionListFlow)
    }

    fun delCollection(videoId: Int) {
        viewModelScope.launch {
            olRepo.delCollection(videoId)
            getCollectionList()
        }
    }
}