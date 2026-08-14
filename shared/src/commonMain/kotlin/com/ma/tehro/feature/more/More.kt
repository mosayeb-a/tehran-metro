package com.ma.tehro.feature.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ma.tehro.common.ShareManager
import com.ma.tehro.common.rememberShareManager
import com.ma.tehro.common.ui.TehroIcon
import com.ma.tehro.common.ui.theme.Themes
import com.ma.tehro.feature.more.components.AboutItem
import com.ma.tehro.feature.more.components.AppThemeItem

@Composable
fun More(
    modifier: Modifier = Modifier,
    viewModel: PreferencesViewModel,
    onBack: () -> Unit
) {
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val shareManager = rememberShareManager()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = innerPadding,
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .padding(horizontal = 16.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f))
                                .size(38.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            TehroIcon(
                                modifier = Modifier.padding(top = 46.dp),
                                size = 64.dp,
                                cornerRadius = 22.dp,
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = .28f))
                }
            }

            item { Spacer(Modifier.height(8.dp)) }

            item {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    text = "نمای برنامه",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.End
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                LazyRow {
                    items(Themes, key = { it.name }) { theme ->
                        AppThemeItem(
                            title = theme.name,
                            colorScheme = theme.colorScheme,
                            amoledBlack = false,
                            darkTheme = 2,
                            selected = theme.name == currentTheme?.name,
                            onClick = { viewModel.setTheme(theme) }
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(26.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = .28f))
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    text = "بیشتر",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W500,
                    textAlign = TextAlign.End
                )
            }
            
            item { Spacer(Modifier.height(8.dp)) }

            item {
                AboutItem(
                    icon = Icons.Rounded.Share,
                    title = "اشتراک‌گذاری برنامه",
                    description = "این برنامه رو با بقیه به اشتراک بذار",
                    onClick = {
                        val shareText = """
                            دریافت نرم‌افزار مترو تهران

                            کافه بازار:
                            http://cafebazaar.ir/app/?id=com.ma.tehro

                            مایکت:
                            https://myket.ir/app/com.ma.tehro

                            گیت هاب:
                            https://github.com/mosayeb-a/tehran-metro/releases/
                        """.trimIndent()
                        shareManager.shareText(shareText, "اشتراک‌گذاری برنامه مترو تهران")
                    }
                )
            }

            item {
                AboutItem(
                    icon = Icons.Rounded.BugReport,
                    title = "گزارش اشکال",
                    description = "گزارش باگ یا درخواست قابلیت (نیازمند حساب گیت‌هاب)",
                    onClick = { uriHandler.openUri("https://github.com/mosayeb-a/tehran-metro/issues/new") }
                )
            }
            item {
                AboutItem(
                    icon = Icons.Rounded.Source,
                    title = "سورس‌کد پروژه",
                    description = "مشاهده کد منبع روی گیت‌هاب",
                    onClick = { uriHandler.openUri("https://github.com/mosayeb-a/tehran-metro") }
                )
            }
            item {
                AboutItem(
                    icon = Icons.Rounded.Coffee,
                    title = "حمایت از پروژه",
                    description = "این برنامه به‌صورت شخصی، مستقل، رایگان و آزاد (متن‌باز) توسعه یافته و همواره رایگان باقی خواهد ماند. اگر برایتان مفید بوده، می‌توانید با خرید یک قهوه از آن حمایت کنید.",
                    onClick = { uriHandler.openUri("https://www.coffeebede.com/tehran_metro") }
                )
            }
            item { Spacer(Modifier.height(56.dp)) }
        }
    }
}