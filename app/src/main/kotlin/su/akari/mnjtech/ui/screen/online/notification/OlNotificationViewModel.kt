package su.akari.mnjtech.ui.screen.online.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import su.akari.mnjtech.data.model.online.NotificationListPage
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.util.OlStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class OlNotificationViewModel(
    val olRepo: OlRepo
) : ViewModel() {
    val notificationListFlow = OlStateFlow<NotificationListPage>()

    fun markNotify(id: Int, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            runCatching {
                olRepo.markNotify(id)
                getNotificationList()
            }.getOrElse(onError)
        }
    }

    fun getNotificationList() {
        olRepo.getNotificationList().collectAsStateFlow(viewModelScope, notificationListFlow)
    }
}