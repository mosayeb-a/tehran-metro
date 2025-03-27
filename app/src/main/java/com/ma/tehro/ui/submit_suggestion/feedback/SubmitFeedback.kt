package com.ma.tehro.ui.submit_suggestion.feedback

import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.isFarsi
import com.ma.tehro.ui.submit_suggestion.SubmitInfoState
import com.ma.tehro.ui.train_schedule.EmptyState
import com.ma.tehro.ui.train_schedule.EmptyStatesFaces
import kotlinx.coroutines.launch

@Composable
fun SubmitFeedback(
    onSendMessageClicked: (message: String) -> Unit,
    viewState: SubmitInfoState,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(imeState.value) {
        if (imeState.value) {
            scope.launch {
                lazyListState.animateScrollToItem(
                    index = 1,
                    scrollOffset = 0,
                )
            }
        }
    }
    LaunchedEffect(viewState.isSubmissionSent) {
        if (viewState.isSubmissionSent) {
            messageText = ""
        }
    }
    LaunchedEffect(messageText) {
        if (messageText.count { it == '\n' } > 0) {
            scope.launch {
                lazyListState.animateScrollToItem(
                    index = 1,
                    scrollOffset = 0
                )
            }
        }
    }


    Scaffold(
        topBar = {
            Appbar(
                title = createBilingualMessage(fa = "ارسال پیشنهاد", en = "submit suggestion"),
                handleBack = true,
                onBackClick = onBack
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                state = lazyListState,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                item("state") {
                    Box(
                        modifier = Modifier
                            .fillParentMaxHeight(0.85f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            viewState.isLoading -> {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                            }

                            viewState.isSubmissionSent -> {
                                EmptyState(
                                    faMessage = "پیشنهادت با موفقیت ثبت شد. از همراهیت سپاسگزاریم",
                                    enMessage = "Your suggestion has been recorded successfully. Thanks for your support!",
                                    faces = EmptyStatesFaces.happy
                                )
                            }

                            else -> {
                                EmptyState(
                                    faMessage = ".برای بهتر شدن برنامه، نظرات و پیشنهاداتت رو ارسال کن",
                                    enMessage = "Share your ideas to make the app better!",
                                    faces = EmptyStatesFaces.suggestion
                                )
                            }
                        }
                    }
                }

                item("input") {
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
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(24.dp)),
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(
                    painter = painterResource(R.drawable.send),
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }

}