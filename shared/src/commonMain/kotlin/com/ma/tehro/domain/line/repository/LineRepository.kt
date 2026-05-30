package com.ma.tehro.domain.line.repository

import com.ma.tehro.domain.line.Station

interface LineRepository {
    fun getOrderedStationsByLine(line: Int, useBranch: Boolean): List<Station>
    val getLines :List<Int>
}