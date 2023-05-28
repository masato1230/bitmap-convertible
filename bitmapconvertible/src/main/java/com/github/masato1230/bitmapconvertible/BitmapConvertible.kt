package com.github.masato1230.bitmapconvertible

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap

@Composable
fun BitmapConvertible(
    state: BitmapConvertibleState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AndroidView(
        modifier = modifier,
        factory = {
            val view = ComposeView(it).apply {
                setContent(content)
            }
            state.setComposeView(view)
            return@AndroidView view
        },
    )
}

@Stable
class BitmapConvertibleState {
    private lateinit var _composeView: ComposeView
    fun setComposeView(view: ComposeView) {
        _composeView = view
    }

    fun getBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        return _composeView.drawToBitmap(config = config)
    }
}

@Composable
fun rememberBitmapConvertibleState() = remember { BitmapConvertibleState() }