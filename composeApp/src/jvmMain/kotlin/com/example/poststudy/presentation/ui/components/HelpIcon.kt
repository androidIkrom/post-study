package com.example.poststudy.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HelpIcon(
    title: String,
    helpText: String,
    modifier: Modifier = Modifier,
    tint: Color = Color(0xFF64748B)
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showDialog = true },
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = "Yordam",
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
    }

    if (showDialog) {
        PostStudyDialog(
            onDismissRequest = { showDialog = false },
            title = title,
            text = helpText,
            confirmText = "Tushunarli",
            onConfirm = { showDialog = false }
        )
    }
}
