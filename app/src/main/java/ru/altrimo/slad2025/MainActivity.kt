package ru.altrimo.slad2025

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.databinding.ActivityMainBinding


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
    }


    fun showError(message: String) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(R.string.alertDialogErrorTitle)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.OK) { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    fun showProgress(isVisible: Boolean) {
        binding.progress.isVisible = isVisible
    }

}