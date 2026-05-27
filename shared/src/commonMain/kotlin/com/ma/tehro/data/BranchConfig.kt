package com.ma.tehro.data

import com.ma.tehro.domain.BilingualName

data class BranchConfig(
    val branchPoint: BilingualName,
    val branch: List<String>
)