package com.ma.tehro.common.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ma.tehro.common.ui.theme.LightGray

@Composable
fun TehroHorizontalDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(modifier = modifier, color = LightGray)
}