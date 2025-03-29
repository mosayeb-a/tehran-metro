package com.ma.tehro.feature.line

import androidx.lifecycle.ViewModel
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.LineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LineViewModel @Inject constructor(
    private val repository: LineRepository
) : ViewModel() {

    fun getLines(): List<Int> =
        repository.getLines()

    fun getOrderedStationsInLineByPosition(
        line: Int,
        useAlternateBranch: Boolean
    ): List<Station> =
        repository.getOrderedStationsByLine(line, useAlternateBranch)
}