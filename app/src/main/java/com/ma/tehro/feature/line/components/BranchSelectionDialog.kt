package com.ma.tehro.feature.line.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.LineEndpoints
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.ui.BilingualText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchSelectionDialog(
    line: Int,
    onDismiss: () -> Unit,
    onSelect: (useBranch: Boolean) -> Unit
) {
    val mainInteractionSource = remember { MutableInteractionSource() }
    val branchInteractionSource = remember { MutableInteractionSource() }
    val indication: Indication = LocalIndication.current

    val mainEnEndpoints = remember(line) { LineEndpoints.getEn(line, false) }
    val mainFaEndpoints = remember(line) { LineEndpoints.getFa(line, false) }

    val branchEnEndpoints = remember(line) { LineEndpoints.getEn(line, true) }
    val branchFaEndpoints = remember(line) { LineEndpoints.getFa(line, true) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BilingualText(
                    fa = "انتخاب مسیر",
                    en = "SELECT PATH",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLine = 2,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                PathSelectionItem(
                    faEndpoints = mainFaEndpoints,
                    enEndpoints = mainEnEndpoints,
                    backgroundColor = getLineColorByNumber(line).copy(alpha = .9f),
                    interactionSource = mainInteractionSource,
                    indication = indication,
                    onClick = {
                        onSelect(false)
                        onDismiss()
                    }
                )

                Spacer(Modifier.height(16.dp))

                PathSelectionItem(
                    faEndpoints = branchFaEndpoints,
                    enEndpoints = branchEnEndpoints,
                    backgroundColor = getLineColorByNumber(line),
                    interactionSource = branchInteractionSource,
                    indication = indication,
                    onClick = {
                        onSelect(true)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun PathSelectionItem(
    faEndpoints: Pair<String, String>?,
    enEndpoints: Pair<String, String>?,
    backgroundColor: Color,
    interactionSource: MutableInteractionSource,
    indication: Indication,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(36.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = indication
            ) {
                onClick()
            }
            .padding(16.dp)
    ) {
        BilingualText(
            modifier = Modifier
                .fillMaxWidth(),
            fa = "${faEndpoints?.first} / ${faEndpoints?.second}",
            en = "${enEndpoints?.first?.uppercase()} / ${enEndpoints?.second?.uppercase()}",
            style = MaterialTheme.typography.bodyMedium,
            maxLine = 2,
            textAlign = TextAlign.Center,
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "See stations by line",
            tint = Color.White,
            modifier = Modifier
                .padding(end = 2.dp)
                .size(16.dp)
                .align(Alignment.CenterEnd)
        )
    }
}