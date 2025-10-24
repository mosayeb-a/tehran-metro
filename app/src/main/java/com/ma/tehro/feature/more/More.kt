package com.ma.tehro.feature.more

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ma.tehro.R
import com.ma.tehro.common.ui.theme.AppThemes
import com.ma.tehro.feature.more.components.AboutItem
import com.ma.tehro.feature.more.components.AppThemeItem

@Composable
fun More(
    modifier: Modifier = Modifier,
    viewModel: PreferencesViewModel,
) {
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = innerPadding,
        ) {
            item { Spacer(Modifier.height(28.dp)) }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    Spacer(Modifier.height(36.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = .28f))
                }
            }

            item { Spacer(Modifier.height(14.dp)) }

            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "APP THEME",
                        color = MaterialTheme.colorScheme.onBackground.copy(9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "نمای برنامه",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W500
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                LazyRow {
                    items(AppThemes, key = { it.name }) { theme ->
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
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ABOUT",
                        color = MaterialTheme.colorScheme.onBackground.copy(9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "درباره",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W500
                    )
                }
            }

            item { Spacer(Modifier.height(6.dp)) }

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