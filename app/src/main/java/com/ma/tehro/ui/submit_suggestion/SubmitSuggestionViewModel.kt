package com.ma.tehro.ui.submit_suggestion

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.messenger.Action
import com.ma.tehro.common.messenger.UiMessage
import com.ma.tehro.common.messenger.UiMessageManager
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.DataCorrectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SubmitInfoState(
    val isLoading: Boolean = false,
    val isSubmissionSent: Boolean = false,
)

@HiltViewModel
class SubmitSuggestionViewModel @Inject constructor(
    private val repo: DataCorrectionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SubmitInfoState())
    val state = _state.asStateFlow()

    fun submitStationCorrection(station: Station) {
        viewModelScope.launch {
            submitWithMessage(
                submitAction = { repo.submitStationCorrection(station) },
                successMessage = "Station info sent successfully",
                errorMessage = "Station info failed to send"
            )
        }
    }

    fun sendSimpleFeedback(message: String) {
        viewModelScope.launch {
            submitWithMessage(
                submitAction = { repo.submitFeedback(message.trim()) },
                successMessage = "Feedback sent successfully",
                errorMessage = "Failed to send feedback"
            )
        }
    }

    private suspend fun submitWithMessage(
        submitAction: suspend () -> Unit,
        successMessage: String,
        errorMessage: String
    ) {
        try {
            _state.update { it.copy(isLoading = true, isSubmissionSent = false) }
            submitAction()
            _state.update { it.copy(isLoading = false, isSubmissionSent = true) }
            UiMessageManager.sendEvent(
                UiMessage(
                    message = createBilingualMessage(
                        fa = ".درخواست با موفقیت ارسال شد",
                        en = "Request sent successfully."
                    ),
                    action = Action(
                        name = createBilingualMessage(fa = "بستن", en = "Dismiss"),
                        action = {}
                    )
                )
            )
        } catch (e: Throwable) {
            _state.update { it.copy(isLoading = false) }
            UiMessageManager.sendEvent(
                UiMessage(
                    message = createBilingualMessage(
                        fa = ".درخواست ارسال نشد،یه مشکلی رخ داده",
                        en = "Message not sent due to an issue."
                    ),
                    action = Action(
                        name = createBilingualMessage(fa = "تلاش دوباره", en = "Retry"),
                        action = {
                            viewModelScope.launch {
                                submitWithMessage(
                                    submitAction = submitAction,
                                    successMessage = successMessage,
                                    errorMessage = errorMessage
                                )
                            }
                        }
                    )
                )
            )
        }
    }
}