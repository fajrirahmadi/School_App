package com.jhy.project.schoollibrary.model.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterArticleBinding
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.model.Article
import com.mikepenz.fastadapter.binding.AbstractBindingItem

data class ArticleAdapter(
    val article: Article
) :
    AbstractBindingItem<AdapterArticleBinding>() {

    override val type: Int
        get() = R.id.article_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterArticleBinding {
        return AdapterArticleBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: AdapterArticleBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.apply {
            leftIv.setImage(article.imageUrl, R.drawable.placeholder_book)
            leftIv.clipToOutline = true
            titleTv.text = article.title
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                contentTv.text = Html.fromHtml(article.content, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                contentTv.text = Html.fromHtml(article.content)
            }
        }
    }

    override fun unbindView(binding: AdapterArticleBinding) {
        super.unbindView(binding)
        binding.leftIv.setImageResource(0)
    }

}