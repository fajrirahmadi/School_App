package com.jhy.project.schoollibrary.feature

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingActivity
import com.jhy.project.schoollibrary.databinding.ActivityMainBinding
import com.jhy.project.schoollibrary.extension.slideToBottom
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController

    var isTopLevelMenu: Boolean = true

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        navController = findNavController(R.id.fragmentNavHost)
        navController.setGraph(R.navigation.nav_main)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            binding.mainToolbarArea.isVisible =
                !binding.toolbar.title.isNullOrEmpty() && destination.id != R.id.menuFragment

            binding.toolbar.setNavigationOnClickListener {
                blockButton()
                onBackPressed()
            }
            binding.toolbar.setNavigationIcon(R.drawable.ic_back)
            if (destination.id != R.id.menuFragment) {
                if (!binding.toolbar.title.isNullOrEmpty()) {
                    binding.titleTextView.text = binding.toolbar.title?.toString()
                }
            }
        }

        Firebase.dynamicLinks.getDynamicLink(intent).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.link?.let {
                    postDelayed({
                        navController.navigate(it)
                    }, 2_00L)
                }
            }
        }
    }

    fun setToolbarTitle(title: String) {
        binding.titleTextView.text = title
    }
}