package com.jhy.project.schoollibrary.auth

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingActivity
import com.jhy.project.schoollibrary.databinding.ActivityAuthenticationBinding
import com.jhy.project.schoollibrary.extension.string
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseViewBindingActivity<ActivityAuthenticationBinding>() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        navController = findNavController(R.id.fragmentAuthNavHost)
        navController.setGraph(R.navigation.nav_auth)
        binding.authToolbar.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.authToolbar.apply {
                setNavigationIcon(R.drawable.ic_back)
                setNavigationOnClickListener {
                    blockButton()
                    onBackPressed()
                }
                binding.authTitleTextView.text = title?.toString()
            }
            binding.contailer.isVisible = binding.authTitleTextView.string.isNotEmpty()
        }
    }

    override val bindingInflater: (LayoutInflater) -> ActivityAuthenticationBinding
        get() = ActivityAuthenticationBinding::inflate

}