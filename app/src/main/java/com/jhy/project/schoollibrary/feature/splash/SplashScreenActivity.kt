package com.jhy.project.schoollibrary.feature.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingActivity
import com.jhy.project.schoollibrary.databinding.ActivitySplashBinding
import com.jhy.project.schoollibrary.extension.onAnimationEnd
import com.jhy.project.schoollibrary.extension.setAnim
import com.jhy.project.schoollibrary.feature.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseViewBindingActivity<ActivitySplashBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val anim = binding.root.setAnim(R.anim.fade_in)
        binding.splashIcon.animation = anim
        anim.onAnimationEnd {
            postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                if (intent.hasExtra("uri")) {
                    intent.putExtra("uri", intent.getStringExtra("uri"))
                }
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }, 2_00L)
        }
    }

    override val bindingInflater: (LayoutInflater) -> ActivitySplashBinding
        get() = ActivitySplashBinding::inflate
}