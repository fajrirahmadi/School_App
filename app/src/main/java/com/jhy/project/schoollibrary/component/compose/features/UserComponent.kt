package com.jhy.project.schoollibrary.component.compose.features

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.component.compose.WorkSandTextSemiBold
import com.jhy.project.schoollibrary.component.compose.shimmerEffect
import com.jhy.project.schoollibrary.extension.capitalizeWord
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.model.toKelasText
import com.jhy.project.schoollibrary.model.wanita

@Composable
fun UserComponent(
    modifier: Modifier = Modifier,
    user: User,
    onClick: (User) -> Unit
) {
    val background = if (wanita.equals(
            user.gender,
            true
        )
    ) AppColor.pinkSoft else AppColor.blueSoft
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .clickable {
                onClick(user)
            },
        backgroundColor = background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkSandTextMedium(
                text = "Nama: ${user.name ?: ""}",
                color = AppColor.neutral40
            )
            WorkSandTextNormal(
                text = user.kelas?.toKelasText()
                    ?.capitalizeWord() ?: "",
                color = AppColor.neutral40,
                textAlign = TextAlign.Start
            )
            WorkSandTextNormal(
                text = if (user.role == siswa) "NIS: ${user.no_id}"
                else if (user.no_id.isNullOrEmpty()) "NIK: ${user.nik}"
                else "NIP: ${user.no_id}"
            )
        }
    }
}

@Composable
fun UserShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
    )
}

@Composable
fun InformationSection(
    title: String = "",
    userInformation: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = AppColor.white,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WorkSandTextSemiBold(
                text = title,
                size = 16.sp
            )
            userInformation.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    WorkSandTextNormal(
                        modifier = Modifier.weight(1f),
                        text = it.first,
                        textAlign = TextAlign.Left
                    )
                    WorkSandTextNormal(text = ":")
                    WorkSandTextMedium(
                        modifier = Modifier.weight(1f),
                        text = it.second,
                        align = TextAlign.Left
                    )
                }
            }
        }
    }
}