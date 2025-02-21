package com.ma.tehro.ui.submit_info

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubmitInfoState(
    val isLoading: Boolean = false
)

@HiltViewModel
class SubmitInfoViewModel @Inject constructor(
    private val stationCorrectionRepo: DataCorrectionRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<SubmitInfoState> = MutableStateFlow(SubmitInfoState())
    val state: StateFlow<SubmitInfoState> = _state.asStateFlow()

    fun submitStationCorrection(station: Station) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                stationCorrectionRepo.submitStationCorrection(station)
                _state.update { it.copy(isLoading = false) }
                UiMessageManager.sendEvent(
                    UiMessage(
                        message = createBilingualMessage(
                            fa = "اطلاعات ایستگاه با موفقیت ارسال شد",
                            en = "Station info sent successfully"
                        ),
                        action = Action(
                            name = "Dismiss",
                            action = {}
                        )
                    )
                )
            } catch (e: Throwable) {
                _state.update { it.copy(isLoading = false) }
                UiMessageManager.sendEvent(
                    UiMessage(
                        message = createBilingualMessage(
                            fa = "ارسال اطلاعات ایستگاه ناموفق بود",
                            en = "Station info failed to send"
                        ),
                        action = Action(
                            name = "Retry",
                            action = { submitStationCorrection(station) }
                        )
                    )
                )
            }
        }
    }
}