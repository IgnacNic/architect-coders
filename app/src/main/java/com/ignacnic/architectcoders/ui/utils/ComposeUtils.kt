package com.ignacnic.architectcoders.ui.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberFineLocationPermissionsState(
    onPermissionResult: (Map<String, Boolean>) -> Unit
) = rememberMultiplePermissionsState(
    permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ),
    onPermissionsResult = onPermissionResult,
)

@Composable
fun rememberDocumentTreeActivityLauncher(onActivityResult: (Uri) -> Unit) =
    rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { result ->
        result?.let {
            onActivityResult(result)
        }
    }

@Composable
fun rememberDocumentPickerActivityLauncher(onActivityResult: (Uri) -> Unit) =
    rememberLauncherForActivityResult(
        contract = CreateGpxFromUri(),
    ) { result ->
        result?.let(onActivityResult)
    }

private class CreateGpxFromUri : ActivityResultContract<Uri?, Uri?>() {
    override fun createIntent(context: Context, input: Uri?) = Intent(Intent.ACTION_CREATE_DOCUMENT)
        .setType("application/gpx")
        .putExtra(DocumentsContract.EXTRA_INITIAL_URI, input)

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
    }
}
