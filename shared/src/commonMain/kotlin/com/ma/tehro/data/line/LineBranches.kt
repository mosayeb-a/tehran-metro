package com.ma.tehro.data.line

import com.ma.tehro.domain.common.BilingualName

data class LineBranches(
    val main: Pair<BilingualName, BilingualName>,
    val branch: Pair<BilingualName, BilingualName>? = null
)