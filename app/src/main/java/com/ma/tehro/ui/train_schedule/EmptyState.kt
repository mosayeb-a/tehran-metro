package com.ma.tehro.ui.train_schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.BilingualText

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    faMessage: String,
    enMessage: String,
    faces: List<String>
) {
    val selectedFace = remember { faces.random() }

    Box(
        modifier = modifier
            .padding(bottom = 42.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = selectedFace,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 46.sp),
                modifier = Modifier,
            )
            Spacer(Modifier.height(18.dp))
            BilingualText(
                fa = faMessage,
                en = enMessage,
                spaceBetween = 4.dp,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

object EmptyStatesFaces {
    val happy = listOf(
        "(◠‿◠)",
        "ʘ‿ʘ",
        "(◕‿◕)",
        "(*^▽^*)",
        "(◠‿◠✿)",
        "٩(◕‿◕｡)۶",
        "(｡♥‿♥｡)",
        "(◕‿◕✿)",
        "( ﾟ▽ﾟ)/"
    )
    val suggestion = listOf(
        "(・_・ヾ",
        "(｡･ω･｡)",
        "(◕ᴗ◕✿)",
        "('ω')",
        "(´･ω･`)?",
        "(◠‿◕)",
        "(。・_・。)",
        "(・∀・)",
        "(◕‿◕)"
    )
    val sad = listOf(
        "(˘･_･˘)",
        "(╥﹏╥)",
        "(｡•́︿•̀｡)",
        "(っ˘̩╭╮˘̩)っ",
        "( ˘･з･)",
        "(◕︵◕)",
        "ಥ_ಥ",
        "(｡╯︵╰｡)",
        "(-̩̩̩-̩̩̩-̩̩̩-̩̩̩-̩̩̩___-̩̩̩-̩̩̩-̩̩̩-̩̩̩-̩̩̩)",
        "(;´༎ຶД༎ຶ`)"
    )
}