package com.ma.tehro.feature.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.BuildConfig
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.BilingualText
import com.ma.tehro.common.toFarsiNumber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun About(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,

    ) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        modifier = modifier,
        topBar = {
            Appbar(
                fa = "درباره",
                en = "ABOUT",
                handleBack = true,
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            item("icon") {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "app icon",
                    modifier = Modifier
                        .size(78.dp)
                        .border(
                            width = 6.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    contentScale = ContentScale.FillBounds
                )
            }

            item { Spacer(Modifier.height(4.dp)) }

            item("app_name") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BilingualText(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        fa = "تهران مترو",
                        en = "TEHRAN METRO",
                        enAlpha = .7f,
                        spaceBetween = (-10).dp,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item("version") {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = .8f))
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    BilingualText(
                        fa = "نگارش ${BuildConfig.VERSION_NAME.toFarsiNumber()}",
                        en = "VERSION ${BuildConfig.VERSION_NAME}",
                        enSize = 8.sp,
                        enAlpha = .7f,
                        spaceBetween = (-1).dp,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item("sections") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    verticalAlignment = Alignment.Top
                ) {
                    AboutSection(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = "گزارش اشکال",
                        description = "گزارش باگ یا درخواست قابلیت (نیازمند حساب گیت‌هاب)",
                        onClick = { uriHandler.openUri("https://github.com/mosayeb-a/tehran-metro/issues/new") }
                    )
                    Spacer(Modifier.width(8.dp))
                    AboutSection(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        title = "سورس‌کد پروژه",
                        description = "مشاهده کد منبع روی گیت‌هاب",
                        onClick = { uriHandler.openUri("https://github.com/mosayeb-a/tehran-metro") }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(18.dp)) }

            item("donation") {
                AboutSection(
                    modifier = Modifier.fillMaxWidth(),
                    title = "حمایت از پروژه",
                    description = "این برنامه به‌صورت شخصی، مستقل، رایگان و آزاد (متن‌باز) توسعه یافته و همواره رایگان باقی خواهد ماند. اگر برایتان مفید بوده، می‌توانید با خرید یک قهوه از آن حمایت کنید.",
                    onClick = { uriHandler.openUri("https://www.coffeebede.com/tehran_metro") }
                )
            }
        }
    }
}


@Composable
fun AboutSection(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clickable { onClick() }
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
