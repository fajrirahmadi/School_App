package com.jhy.project.schoollibrary.feature.school.article

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.ZoomImageBottomSheet
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.ImageComponent
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.component.compose.loadImage
import com.jhy.project.schoollibrary.extension.capitalizeWord
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.extension.toDateFormat

class ArticleFragment : BaseComposeFragment() {

    private val args by navArgs<ArticleFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            MaterialTheme {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        loadImage(
                            context = requireContext(),
                            url = args.article.imageUrl,
                            defaultImage = R.drawable.ic_logo_smp
                        ).value?.let { image ->
                            ImageComponent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clickable {
                                        showBottomSheet(
                                            ZoomImageBottomSheet(args.article.imageUrl)
                                        )
                                    },
                                frame = image,
                                scale = ContentScale.Crop
                            )
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WorkSandTextMedium(text = args.article.title, size = 16.sp)
                            VerticalSpace(height = 8.dp)
                            WorkSandTextNormal(text = "Dibuat oleh: ${args.article.author.capitalizeWord()}")
                            WorkSandTextNormal(
                                text = "Dipublish tanggal ${
                                    args.article.postDate.toDateFormat(
                                        "dd MMMM yyyy, HH:mm"
                                    )
                                }"
                            )
                            Divider(
                                color = AppColor.grey
                            )
                            VerticalSpace(height = 8.dp)
                            WorkSandTextNormal(
                                text = args.article.content,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Left
                            )
                        }
                    }
                }
            }
        }
    }
}