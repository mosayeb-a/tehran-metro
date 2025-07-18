package com.ma.tehro.feature.submit_suggestion.station.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.input.ImeAction

@Composable
fun CorrectionEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (value: String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                )
            },
            trailingIcon = trailingIcon,
            modifier = modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                focusedLabelColor = Color.White,
                focusedLeadingIconColor = Color.White,
                focusedTrailingIconColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                selectionColors = TextSelectionColors(
                    handleColor = Color.White,
                    backgroundColor = Color.White.copy(alpha = 0.3f)
                )
            ),
            maxLines = maxLines,
            shape = MaterialTheme.shapes.medium,
            singleLine = maxLines == 1,
            keyboardOptions = keyboardOptions.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
        )
    }
}