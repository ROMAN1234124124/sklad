package ru.altrimo.slad2025.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.databinding.AuthFragmentBinding
import ru.altrimo.slad2025.fragment.base.ViewBindingFragment
import ru.altrimo.slad2025.viewmodel.AuthViewModel

@AndroidEntryPoint
class AuthFragment : ViewBindingFragment<AuthFragmentBinding>() {

    override val inflaterDelegate by inflaterDelegate()
    private val viewModel: AuthViewModel by viewModels()

    override fun onInflationComplete() {
        setupObserve()
        binding.actionLogin.setOnClickListener {
            val login = binding.txtLogin.text
            val password = binding.txtPassword.text
            if (login.isNullOrBlank() || password.isNullOrBlank()) {
                showError(getString(R.string.empty_error))
            } else {
                viewModel.auth(login.toString(), password.toString())
            }
        }
    }

    private fun setupObserve() {
        viewModel.viewResult.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_auth_to_doc_list)
        }

        viewModel.viewShowError.observe(viewLifecycleOwner) {
            showError(it)
        }

        viewModel.viewShowLoading.observe(viewLifecycleOwner) {
            showProgress(it)
            binding.actionLogin.isEnabled = !it
        }
    }


}