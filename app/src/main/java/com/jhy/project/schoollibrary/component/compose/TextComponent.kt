package com.jhy.project.schoollibrary.component.compose

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.jhy.project.schoollibrary.R

val workSansFont = FontFamily(
    Font(R.font.work_sans_regular, FontWeight.Normal),
    Font(R.font.work_sans_medium, FontWeight.Medium),
    Font(R.font.work_sans_semibold, FontWeight.SemiBold),
    Font(R.font.work_sans_bold, FontWeight.Bold),
)

@Composable
fun WorkSandTextMedium(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppColor.neutral40,
    size: TextUnit = 14.sp,
    align: TextAlign = TextAlign.Center
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontFamily = workSansFont,
        fontWeight = FontWeight.Medium,
        fontSize = size,
        textAlign = align
    )
}

@Composable
fun WorkSandTextSemiBold(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppColor.neutral40,
    size: TextUnit = 14.sp,
    align: TextAlign = TextAlign.Center
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontFamily = workSansFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = size,
        textAlign = align
    )
}

@Composable
fun WorkSandTextBold(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppColor.neutral40,
    size: TextUnit = 14.sp,
    align: TextAlign = TextAlign.Center
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontFamily = workSansFont,
        fontWeight = FontWeight.Bold,
        fontSize = size,
        textAlign = align
    )
}

@Composable
fun WorkSandTextNormal(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppColor.neutral40,
    size: TextUnit = 14.sp,
    textAlign: TextAlign = TextAlign.Left
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontFamily = workSansFont,
        fontWeight = FontWeight.Normal,
        fontSize = size,
        textAlign = textAlign,
        lineHeight = 24.sp
    )
}

