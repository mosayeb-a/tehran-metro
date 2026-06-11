package com.ma.tehro.feature.feedback

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.ui.Action
import com.ma.tehro.common.ui.ActionType
import com.ma.tehro.common.ui.UiMessage
import com.ma.tehro.common.ui.UiMessageManager
import com.ma.tehro.domain.feedback.repository.FeedbackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class FeedbackState(
    val isLoading: Boolean = false,
    val isSubmissionSent: Boolean = false,
)

class FeedbackViewModel(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FeedbackState())
    val state = _state.asStateFlow()

    fun send(message: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isSubmissionSent = false) }

            try {
                feedbackRepository.send(message.trim())
                _state.update { it.copy(isLoading = false, isSubmissionSent = true) }
                UiMessageManager.sendEvent(
                    UiMessage(
                        message = ".درخواست با موفقیت ارسال شد",
                        action = Action(
                            name = "بستن",
                            action = {}
                        )
                    )
                )
            } catch (e: Throwable) {
                _state.update { it.copy(isLoading = false) }
                UiMessageManager.sendEvent(
                    UiMessage(
                        message = ".درخواست ارسال نشد، یه مشکلی رخ داده",
                        action = Action(
                            name = "تلاش دوباره",
                            type = ActionType.RETRY,
                            action = { send(message) }
                        )
                    )
                )
            }
        }
    }
}