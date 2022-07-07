package su.akari.mnjtech.ui.screen.online.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import su.akari.mnjtech.data.model.online.CommentListPage
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.util.OlStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class OlCommentViewModel(val olRepo: OlRepo) : ViewModel() {
    val commentListFlow = OlStateFlow<CommentListPage>()

    fun getCommentList() {
        olRepo.getCommentList().collectAsStateFlow(viewModelScope, commentListFlow)
    }
}