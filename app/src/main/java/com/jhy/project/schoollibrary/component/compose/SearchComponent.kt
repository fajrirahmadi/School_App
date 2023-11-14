package com.jhy.project.schoollibrary.component.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jhy.project.schoollibrary.R

const val cariSiswaOrAlumni = "Cari siswa/alumni disini..."
const val cariBukuDisini = "Cari buku disini..."

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    val controller = LocalSoftwareKeyboardController.current
    Box(
        modifier = modifier.border(BorderStroke(1.dp, AppColor.neutral40), RoundedCornerShape(50))
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
        ) {
            Image(
                modifier = Modifier.size(22.dp),
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "ic_search",
                colorFilter = ColorFilter.tint(AppColor.neutral40)
            )
            HorizontalSpace(width = 16.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(AppColor.white)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        onTextChange(newText)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        onSearch(text)
                        controller?.hide()
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black)
                )
                if (text.isEmpty()) {
                    WorkSandTextNormal(
                        text = placeholder,
                        color = AppColor.neutral40
                    )
                }
            }
            HorizontalSpace(width = 16.dp)
            if (text.isNotEmpty()) {
                IconButton(
                    onClick = { onTextChange("") }, modifier = Modifier.background(AppColor.white)
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_clear),
                        contentDescription = "ic_clear",
                        colorFilter = ColorFilter.tint(AppColor.neutral40)
                    )
                }
            }
        }
    }
}