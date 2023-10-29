package com.jhy.project.schoollibrary.component.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhy.project.schoollibrary.R

@Composable
fun WorkSandButtonMedium(
    modifier: Modifier = Modifier,
    text: String,
    alpha: Float = 1f,
    textSize: TextUnit = 14.sp,
    onClick: () -> Unit
) {
    OutlinedButton(modifier = modifier.alpha(alpha),
        border = BorderStroke(1.dp, colorResource(id = R.color.textColor)),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        onClick = { onClick.invoke() }) {
        WorkSandTextMedium(text = text, size = textSize)
    }
}

@Composable
fun FilterButton(modifier: Modifier, text: String, selected: Boolean, action: () -> Unit) {
    if (selected) {
        SelectedButton(modifier = modifier, text = text)
    } else {
        UnSelectedButton(modifier = modifier, text = text) {
            action.invoke()
        }
    }
}

@Composable
fun SelectedButton(modifier: Modifier = Modifier, text: String, onClick: (() -> Unit)? = null) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        onClick = { onClick?.invoke() },
        colors = ButtonDefaults.buttonColors(
            contentColor = colorResource(id = R.color.white), backgroundColor = colorResource(
                id = R.color.primary_color
            )
        )
    ) {
        WorkSandTextMedium(
            text = text, color = colorResource(id = R.color.white)
        )
    }
}

@Composable
fun UnSelectedButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Int = R.color.textColor,
    background: Int = R.color.neutral_40,
    action: () -> Unit
) {
    OutlinedButton(
        modifier = modifier, shape = RoundedCornerShape(50.dp), onClick = {
            action.invoke()
        }, border = BorderStroke(
            1.dp, colorResource(id = R.color.primary_color)
        ), colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = colorResource(id = background)
        )
    ) {
        WorkSandTextMedium(text = text, color = colorResource(id = textColor))
    }
}

@Composable
fun PrimaryButton(modifier: Modifier = Modifier.fillMaxWidth(), text: String, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            contentColor = AppColor.neutral40, backgroundColor = AppColor.blueSoft
        )
    ) {
        WorkSandTextMedium(
            text = text, color = AppColor.neutral40
        )
    }
}