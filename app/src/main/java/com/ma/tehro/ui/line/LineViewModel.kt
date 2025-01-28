package com.ma.tehro.ui.line

import androidx.lifecycle.ViewModel
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.LineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// todo: make single viewmodel for whole app for simplification
@HiltViewModel
class LineViewModel @Inject constructor(
    private val repository: LineRepository
) : ViewModel() {

    fun getLines(): List<Int> =
        repository.getLines()

    fun getOrderedStationsInLineByPosition(
        line: Int,
    ): List<Station> =
        repository.getOrderedStationsByLine(line)
}