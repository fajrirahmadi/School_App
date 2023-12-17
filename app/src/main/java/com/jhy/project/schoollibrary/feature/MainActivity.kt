package com.jhy.project.schoollibrary.feature

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingActivity
import com.jhy.project.schoollibrary.databinding.ActivityMainBinding
import com.jhy.project.schoollibrary.extension.slideFromBottom
import com.jhy.project.schoollibrary.extension.slideFromTop
import com.jhy.project.schoollibrary.extension.slideToBottom
import com.jhy.project.schoollibrary.extension.slideToTop
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentNavHost) as NavHostFragment
        navHostFragment.navController
    }

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setSupportActionBar(binding.toolbar)
        navController.setGraph(R.navigation.nav_main)
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemReselectedListener { false }
        binding.toolbar.setupWithNavController(navController)

        val bottomMenus = binding.bottomNav.menu.children
        navController.addOnDestinationChangedListener { _, destination, _ ->

            val isTopLevelMenu = bottomMenus.any {
                it.itemId == destination.id
            }

            binding.toolbar.setNavigationOnClickListener {
                blockButton()
                onBackPressed()
            }
            binding.toolbar.setNavigationIcon(R.drawable.ic_back)

            if (!isTopLevelMenu) {
                binding.bottomNav.slideToBottom()
                binding.mainToolbarArea.slideFromTop()
                if (!binding.toolbar.title.isNullOrEmpty()) {
                    binding.titleTextView.text = binding.toolbar.title.toString()
                }
            } else {
                binding.mainToolbarArea.slideToTop()
                binding.bottomNav.slideFromBottom()
            }
            binding.bottomNav.isVisible = isTopLevelMenu
            binding.mainToolbarArea.isVisible = !isTopLevelMenu
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