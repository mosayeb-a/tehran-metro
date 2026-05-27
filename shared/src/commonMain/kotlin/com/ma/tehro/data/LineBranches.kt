package com.ma.tehro.data

import com.ma.tehro.domain.BilingualName

data class LineBranches(
    val main: Pair<BilingualName, BilingualName>,
    val branch: Pair<BilingualName, BilingualName>? = null
)