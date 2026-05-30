package com.ma.tehro.data.line

import com.ma.tehro.domain.common.BilingualName

data class BranchConfig(
    val branchPoint: BilingualName,
    val branch: List<String>
)