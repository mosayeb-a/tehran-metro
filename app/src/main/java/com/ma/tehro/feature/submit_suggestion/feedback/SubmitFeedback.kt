package com.ma.tehro.feature.submit_suggestion.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.EmptyStatesFaces
import com.ma.tehro.common.ui.Message
import com.ma.tehro.feature.submit_suggestion.SubmitInfoState
import com.ma.tehro.feature.submit_suggestion.feedback.components.MessageInput

@Composable
fun SubmitFeedback(
    onSendMessageClicked: (message: String) -> Unit,
    viewState: SubmitInfoState,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }

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
                onBackClick = onBack
            )
        },
        bottomBar = {
            MessageInput(
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding(),
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessageClicked(messageText)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingValues.calculateTopPadding()),
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
    }
}