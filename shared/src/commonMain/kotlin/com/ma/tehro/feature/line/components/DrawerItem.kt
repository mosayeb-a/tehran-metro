package com.ma.tehro.feature.line.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.data.BilingualName
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DrawerItem(
    label: BilingualName,
    onClick: () -> Unit,
    icon: DrawableResource? = null,
    imageVector: ImageVector? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            BilingualText(
                fa = label.fa,
                en = label.en,
                style = MaterialTheme.typography.bodyMedium,
                maxLine = 2,
                textAlign = TextAlign.End
            )

            Spacer(Modifier.width(16.dp))

            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = label.fa,
                    tint = Color.White
                )
            } else if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = label.fa,
                    tint = Color.White
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(.15f))
    }
}