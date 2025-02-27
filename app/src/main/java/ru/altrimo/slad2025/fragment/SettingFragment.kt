package ru.altrimo.slad2025.fragment

import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.BuildConfig
import ru.altrimo.slad2025.R
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
            showDialog(getString(R.string.confirm_message_save_settings)) {
                preferences.apiServer = binding.txtServer.editableText.toString()
            }
        }
    }

}