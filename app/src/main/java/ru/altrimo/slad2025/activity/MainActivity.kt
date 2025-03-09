package ru.altrimo.slad2025.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.KeyEventSimulator
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.atol.Barcode
import ru.altrimo.slad2025.atol.BarcodeReceiver
import ru.altrimo.slad2025.databinding.ActivityMainBinding


const val KEYCODE_SCANNER = 141

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var mNavController: NavController


    private val barcodeReceiver = object : BarcodeReceiver() {
        override fun onBarcodeReceive(context: Context, barcode: Barcode) {
            Log.d("ASFDASFASF", "Received barcode: ${barcode.barcode}, Type: ${barcode.type}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        mNavController = navHostFragment.navController
        mNavController.setGraph(R.navigation.nav_graph)
        window?.navigationBarColor = resources.getColor(R.color.divider)
        binding.toolbar.setNavigationOnClickListener {
            KeyEventSimulator.simulateKeyPress(it, KEYCODE_SCANNER)
        }
        lifecycle.addObserver(barcodeReceiver.registerLifecycleEventObserver(this))

    }

    fun showProgress(isVisible: Boolean) {
        binding.progress.isVisible = isVisible
    }

}