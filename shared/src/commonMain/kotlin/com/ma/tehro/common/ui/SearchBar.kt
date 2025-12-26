package com.ma.tehro.common.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.isFarsi
import kotlinx.coroutines.launch

@Composable
fun TehroSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    color : Color = MaterialTheme.colorScheme.secondary
) {
    val isRtl = remember(value) { isFarsi(value) }
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.onSecondary,
        backgroundColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
        LocalTextSelectionColors provides customTextSelectionColors
    ) {
        val focusRequester = remember { FocusRequester() }
        val keyboardController =
            LocalSoftwareKeyboardController.current
        val coroutineScope = rememberCoroutineScope()

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(24.dp)
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = value.isNotBlank(),
                    enter = fadeIn(
                        animationSpec = tween(
                            200
                        )
                    ),
                    exit = fadeOut(
                        animationSpec = tween(
                            200
                        )
                    )
                ) {
                    IconButton(
                        onClick = {
                            onValueChange("")
                            coroutineScope.launch {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }
                        },
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSecondary,
                textDirection = TextDirection.Content,
                textAlign = if (isRtl) TextAlign.Right else TextAlign.Start,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = color,
                focusedContainerColor = color,
                focusedBorderColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = .75f),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
            ),
            shape = MaterialTheme.shapes.extraLarge,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = if (isRtl) TextAlign.Right else TextAlign.Start,
                        textDirection = TextDirection.Content,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            )
        )
    }
}
