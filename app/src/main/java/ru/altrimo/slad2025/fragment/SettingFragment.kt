package ru.altrimo.slad2025.fragment

import android.content.Intent
import android.util.Patterns
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.activity.MainActivity
import ru.altrimo.slad2025.data.Preferences
import ru.altrimo.slad2025.databinding.FragmentSettingBinding
import ru.altrimo.slad2025.fragment.base.ViewBindingFragment
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : ViewBindingFragment<FragmentSettingBinding>() {

    override val inflaterDelegate by inflaterDelegate()

    @Inject
    lateinit var preferences: Preferences


    override fun onInflationComplete() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.txtServer.setText(preferences.apiServer)
        binding.actionSave.setOnClickListener {
            val baseUrl = binding.txtServer.editableText.toString()
            if (isValidUrl(baseUrl)) {
                showConfirmationDialog(getString(R.string.confirm_message_save_settings)) {
                    preferences.apiServer = baseUrl
                    restartApp()
                }
            } else {
                showError(getString(R.string.setting_error_url))
            }
        }
    }

    private fun isValidUrl(baseUrl: String): Boolean {
        return baseUrl.isNotBlank()
                && Patterns.WEB_URL.matcher(baseUrl).matches()
                && baseUrl.last().toString() == "/"
    }

    private fun restartApp() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }

}