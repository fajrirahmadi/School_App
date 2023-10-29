package com.jhy.project.schoollibrary.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.component.compose.ImageComponent
import com.jhy.project.schoollibrary.component.compose.loadImage
import com.jhy.project.schoollibrary.databinding.BottomsheetZoomImageBinding

class ZoomImageBottomSheet(private val url: String) :
    BaseViewBindingBottomSheet<BottomsheetZoomImageBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
        binding.composeView.setContent {
            var zoom by remember {
                mutableStateOf(1f)
            }
            var offset by remember {
                mutableStateOf(Offset.Zero)
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                loadImage(
                    context = requireContext(), url = url, defaultImage = R.drawable.ic_logo_smp
                ).value?.let {
                    ImageComponent(modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(200.dp, 500.dp)
                        .clipToBounds()
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, panChange, zoomChange, _ ->
                                offset = offset.calculateNewOffset(
                                    centroid,
                                    panChange,
                                    zoom,
                                    zoomChange,
                                    size
                                )
                                zoom = maxOf(1f, zoom * zoomChange)
                            }
                        }
                        .graphicsLayer {
                            translationX = -offset.x * zoom
                            translationY = -offset.y * zoom
                            scaleX = zoom; scaleY = zoom
                            transformOrigin = TransformOrigin(0f, 0f)
                        }, frame = it
                    )
                }
            }
        }
    }

    private fun Offset.calculateNewOffset(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        gestureZoom: Float,
        size: IntSize
    ): Offset {
        val newScale = maxOf(1f, zoom * gestureZoom)
        val newOffset = (this + centroid / zoom) -
                (centroid / newScale + pan / zoom)
        return Offset(
            newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
            newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
        )
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetZoomImageBinding
        get() = BottomsheetZoomImageBinding::inflate

    override fun isAlwaysFull(): Boolean {
        return true
    }

    override fun isDraggable(): Boolean {
        return false
    }
}
