package com.jhy.project.schoollibrary.component.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    text: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChange: (String) -> Unit,
    onDone: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .border(BorderStroke(1.dp, AppColor.neutral40), RoundedCornerShape(10))
            .background(AppColor.white)
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                onTextChange(newText)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType
            ),
            keyboardActions = KeyboardActions(onDone = {
                onDone?.invoke()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp),
            textStyle = TextStyle(color = Color.Black)
        )
        if (text.isEmpty()) {
            WorkSandTextNormal(
                modifier = Modifier
                    .padding(16.dp, 8.dp)
                    .fillMaxWidth(),
                text = placeholder,
                color = AppColor.neutral40,
                textAlign = TextAlign.Start
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTextWithTitle(
    modifier: Modifier = Modifier,
    title: String,
    placeholder: String = "",
    text: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChange: (String) -> Unit,
    onDone: (() -> Unit)? = null
) {
    val controller = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WorkSandTextMedium(text = title)
        Box(
            modifier = modifier
                .border(
                    BorderStroke(1.dp, AppColor.neutral40),
                    RoundedCornerShape(10)
                )
                .background(AppColor.white)
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newText ->
                    onTextChange(newText)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = keyboardType
                ),
                keyboardActions = KeyboardActions(onDone = {
                    onDone?.invoke() ?: controller?.hide()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp),
                textStyle = TextStyle(color = Color.Black)
            )
            if (text.isEmpty()) {
                WorkSandTextNormal(
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .fillMaxWidth(),
                    text = placeholder,
                    color = AppColor.neutral40,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun MultipleLineEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hintText: String = "",
    maxLines: Int = Int.MAX_VALUE
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        maxLines = maxLines,
        decorationBox = { innerTextField ->
            Box(
                modifier = modifier.border(
                    BorderStroke(1.dp, AppColor.neutral40),
                    RoundedCornerShape(10)
                ).background(AppColor.white)
            ) {
                if (value.isEmpty()) {
                    WorkSandTextNormal(
                        text = hintText,
                        color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                    )
                }
                innerTextField()
            }
        }
    )
}