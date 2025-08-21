package com.ignacnic.architectcoders.common.uiresources.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ignacnic.architectcoders.common.uiresources.R
import com.ignacnic.architectcoders.common.uiresources.theme.ArchitectCodersTheme

@Composable
fun RationaleDialog(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onDismiss: () -> Unit = {},
    onAccept: () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            description?.let {
                Text(text = it)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onAccept,
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Preview
@Composable
private fun LocationRationalePreview() {
    ArchitectCodersTheme {
        RationaleDialog(
            title = stringResource(R.string.location_rationale_title),
            description = stringResource(R.string.location_rationale_description),
        )
    }
}
