package com.ma.tehro.domain.repo

import com.ma.tehro.data.Station

interface LineRepository {
    fun getOrderedStationsByLine(line: Int, useBranch: Boolean): List<Station>
    val getLines :List<Int>
}