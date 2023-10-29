package com.jhy.project.schoollibrary.feature.library.book

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentBookReadPdfBinding
import com.mindev.mindev_pdfviewer.MindevPDFViewer
import com.mindev.mindev_pdfviewer.PdfScope

class BookReadPdfFragment : BaseViewBindingFragment<FragmentBookReadPdfBinding>() {

    private val args by navArgs<BookReadPdfFragmentArgs>()

    private val statusListener = object : MindevPDFViewer.MindevViewerStatusListener {
        override fun onStartDownload() {
            showLoading(true)
        }

        @SuppressLint("SetTextI18n")
        override fun onPageChanged(position: Int, total: Int) {
            binding.pageCounterTv.text = "${position + 1} / $total"
        }

        override fun onProgressDownload(currentStatus: Int) {
            showLoading(true)
        }

        override fun onSuccessDownLoad(path: String) {
            showLoading(false)
            binding.pageCounterTv.isVisible = true
            binding.pdf.fileInit(path)
        }

        override fun onFail(error: Throwable) {
            showInfoDialog(requireContext(), error.toString())
        }

        override fun unsupportedDevice() {
            showInfoDialog(requireContext(), "Device not support")
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pdf.initializePDFDownloader(args.url, statusListener)
        lifecycle.addObserver(PdfScope())
    }

    override fun onDestroyView() {
        binding.pdf.pdfRendererCore?.clear()
        super.onDestroyView()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBookReadPdfBinding
        get() = FragmentBookReadPdfBinding::inflate
}