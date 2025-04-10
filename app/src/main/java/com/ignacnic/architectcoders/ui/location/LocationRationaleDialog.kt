package com.ignacnic.architectcoders.ui.location

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ignacnic.architectcoders.ui.theme.ArchitectCodersTheme

@Composable
fun LocationRationaleDialog(
    onDismiss: () -> Unit = {},
    onAccept: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Precise location needed")
        },
        text = {
            Text(text = "In order to get your location, you must grant the app access to your precise location")
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
        LocationRationaleDialog()
    }
}
