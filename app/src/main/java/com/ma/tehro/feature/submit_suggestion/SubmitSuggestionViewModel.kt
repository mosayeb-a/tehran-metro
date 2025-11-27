package com.ma.tehro.feature.submit_suggestion

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.ui.Action
import com.ma.tehro.common.ui.UiMessage
import com.ma.tehro.common.ui.UiMessageManager
import com.ma.tehro.data.Station
import com.ma.tehro.domain.repo.DataCorrectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class SubmitInfoState(
    val isLoading: Boolean = false,
    val isSubmissionSent: Boolean = false,
)

class SubmitSuggestionViewModel(
    private val dataCorrectionRepository: DataCorrectionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SubmitInfoState())
    val state = _state.asStateFlow()

    fun submitStationCorrection(station: Station) {
        viewModelScope.launch {
            submitWithMessage(submitAction = { dataCorrectionRepository.submitStationCorrection(station) })
        }
    }

    fun sendSimpleFeedback(message: String) {
        viewModelScope.launch {
            submitWithMessage(
                submitAction = { dataCorrectionRepository.submitFeedback(message.trim()) },
            )
        }
    }

    private suspend fun submitWithMessage(
        submitAction: suspend () -> Unit,
    ) {
        try {
            _state.update { it.copy(isLoading = true, isSubmissionSent = false) }
            submitAction()
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
        } catch (_: Throwable) {
            _state.update { it.copy(isLoading = false) }
            UiMessageManager.sendEvent(
                UiMessage(
                    message = ".درخواست ارسال نشد، یه مشکلی رخ داده",
                    action = Action(
                        name = "تلاش دوباره",
                        action = {
                            viewModelScope.launch {
                                submitWithMessage(
                                    submitAction = submitAction,
                                )
                            }
                        }
                    )
                )
            )
        }
    }
}