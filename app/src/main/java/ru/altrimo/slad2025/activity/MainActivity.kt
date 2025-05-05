package ru.altrimo.slad2025.activity

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.databinding.ActivityMainBinding
import ru.altrimo.slad2025.fragment.base.BaseFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        mNavController = navHostFragment.navController
        mNavController.setGraph(R.navigation.nav_graph)
        window?.navigationBarColor = resources.getColor(R.color.divider)
    }


    fun showProgress(isVisible: Boolean) {
        binding.progress.isVisible = isVisible
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val currentFragment = mNavController.currentDestination?.id?.let {
            supportFragmentManager.findFragmentById(it)
        }
        if (currentFragment is BaseFragment && currentFragment.isVisible){
            currentFragment.handleKeyDown(keyCode)
        }
        return super.onKeyDown(keyCode, event)
    }

}
