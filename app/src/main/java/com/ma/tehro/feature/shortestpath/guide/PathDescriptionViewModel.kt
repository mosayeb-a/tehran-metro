package com.ma.tehro.feature.shortestpath.guide

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ma.tehro.common.ui.PathDescriptionScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class PathDescriptionState(
    val steps: List<String> = emptyList(),
    val lastLine: Int = 0
)

@HiltViewModel
class PathDescriptionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PathDescriptionState())
    val uiState = _uiState.asStateFlow()

    init {
        val routeArgs = savedStateHandle.toRoute<PathDescriptionScreen>()
        viewModelScope.launch {
            val (steps, lastLine) = processSteps(routeArgs.path)
            _uiState.update { it.copy(steps = steps, lastLine = lastLine) }
        }
    }

    private fun processSteps(path: List<String>): Pair<List<String>, Int> {
        val newSteps = mutableListOf<String>()
        var firstStation: String? = null
        var lastLine = 0

        val lastTitle = path.lastOrNull { it.startsWith("t: ") }
        if (lastTitle != null) {
            lastLine = lastTitle.removePrefix("t: ")
                .substringAfter("خط ")
                .substringBefore(":")
                .trim()
                .toIntOrNull() ?: 0
        }

        path.forEachIndexed { index, step ->
            when {
                step.startsWith("f: ") && firstStation == null -> {
                    firstStation = step.removePrefix("f: ").trim()
                    val firstTitleText = path.first { it.startsWith("t: ") }
                        .removePrefix("t: ")
                        .substringAfterLast(": ")
                        .trim()
                    newSteps.add("> وارد ایستگاه $firstStation شوید و $firstTitleText سوار قطار شوید.")
                }
                step.startsWith("l: ") -> {
                    val currentLastStation = step.removePrefix("l: ").trim()
                    val nextStep = path.getOrNull(index + 1)
                    if (nextStep?.startsWith("t: ") == true) {
                        val title = nextStep.removePrefix("t: ").trim()
                        val changeTitle = title.substringAfter(":").trim()
                        newSteps.add("<> در ایستگاه $currentLastStation از قطار پیاده شوید و $changeTitle خط عوض کنید.")
                    }
                }
            }
        }

        val finalLastStation = path.last { it.startsWith("l: ") }.removePrefix("l: ").trim()
        newSteps.add("< در ایستگاه $finalLastStation از قطار پیاده شوید.")
        newSteps.add("* شما به مقصد رسیدید.")

        return Pair(newSteps, lastLine)
    }
}