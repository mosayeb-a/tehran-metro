package com.ma.tehro.feature.submit_suggestion.feedback

import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.EmptyStatesFaces
import com.ma.tehro.common.Message
import com.ma.tehro.common.isFarsi
import com.ma.tehro.feature.submit_suggestion.SubmitInfoState

@Composable
fun SubmitFeedback(
    onSendMessageClicked: (message: String) -> Unit,
    viewState: SubmitInfoState,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(viewState.isSubmissionSent) {
        if (viewState.isSubmissionSent) {
            messageText = ""
        }
    }

    Scaffold(
        topBar = {
            Appbar(
                fa = "ارسال پیشنهاد",
                en = "submit suggestion",
                handleBack = true,
                onBackClick = onBack
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(
                    bottom = if (imeState.value) 0.dp else paddingValues.calculateBottomPadding()
                )
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = paddingValues.calculateTopPadding()),
                state = lazyListState,
                verticalArrangement = Arrangement.Center
            ) {
                item("state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillParentMaxHeight(0.85f),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            viewState.isLoading -> {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                            }

                            viewState.isSubmissionSent -> {
                                Message(
                                    faMessage = "پیشنهادت با موفقیت ثبت شد. از همراهیت سپاسگزاریم.",
                                    faces = EmptyStatesFaces.happy
                                )
                            }

                            else -> {
                                Message(
                                    faMessage = "برای بهتر شدن برنامه، نظرات و پیشنهادتت رو ارسال کن.",
                                    faces = EmptyStatesFaces.suggestion
                                )
                            }
                        }
                    }
                }
            }


            MessageInput(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessageClicked(messageText)
                    }
                }
            )
        }
    }
}

@Composable
fun rememberImeState(): State<Boolean> {
    val imeState = remember {
        mutableStateOf(false)
    }

    val view = LocalView.current
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            imeState.value = isKeyboardOpen
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return imeState
}

@Composable
private fun MessageInput(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.Transparent),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                textStyle = LocalTextStyle.current.copy(
                    textDirection = if (isFarsi(messageText)) TextDirection.Rtl else TextDirection.Ltr,
                    textAlign = if (isFarsi(messageText)) TextAlign.Right else TextAlign.Left,
                    color = Color.White
                ),
                placeholder = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "...پیشنهادت رو بفرست",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        textAlign = TextAlign.End
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectionColors = TextSelectionColors(
                        handleColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = 0.4f
                        )
                    )
                )
            )

            FloatingActionButton(
                onClick = onSendClick,
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}