package com.ma.tehro.feature.feedback

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
import androidx.compose.ui.platform.LocalUriHandler
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.EmptyStatesFaces
import com.ma.tehro.common.ui.Message
import com.ma.tehro.common.ui.MessageAction
import com.ma.tehro.feature.feedback.components.MessageInput

@Composable
fun Feedback(
    onSendMessage: (message: String) -> Unit,
    viewState: FeedbackState,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(viewState.isSubmissionSent) {
        if (viewState.isSubmissionSent) {
            messageText = ""
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                        onSendMessage(messageText)
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
                                message = "پیشنهادت با موفقیت ثبت شد. از همراهیت سپاسگزاریم.",
                                faces = EmptyStatesFaces.happy
                            )
                        }

                        else -> {
                            Message(
                                message = "برای بهبود برنامه، پیشنهاداتت رو ارسال کن. درصورت داشتن گیت‌هاب، ارسال از اونجا توصیه می‌شه.",                                faces = EmptyStatesFaces.suggestion,
                                messageStyle = MaterialTheme.typography.bodyMedium,
                                action = MessageAction(
                                    name = "ارسال از طریق گیت‌هاب",
                                    action = {
                                        uriHandler.openUri("https://github.com/mosayeb-a/tehran-metro/issues/new")
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}