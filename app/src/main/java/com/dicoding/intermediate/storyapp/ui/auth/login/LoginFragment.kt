package com.dicoding.intermediate.storyapp.ui.auth.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dicoding.intermediate.storyapp.R
import com.dicoding.intermediate.storyapp.data.DataStoreViewModel
import com.dicoding.intermediate.storyapp.data.remote.Result
import com.dicoding.intermediate.storyapp.databinding.FragmentLoginBinding
import com.dicoding.intermediate.storyapp.ui.auth.AuthViewModel
import com.dicoding.intermediate.storyapp.ui.main.MainActivity
import com.dicoding.intermediate.storyapp.ui.main.MainActivity.Companion.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private var loginJob: Job = Job()

    private val viewModels: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAction()
    }

    private fun setUpAction() {
        binding?.apply {
            toRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            btnLogin.setOnClickListener {
                handleLogin()
            }
        }
    }

    private fun handleLogin() {
        binding?.apply {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    edtEmail.error = getString(R.string.edt_email_empty_message)
                }
                password.isEmpty() -> {
                    edtPassword.error = getString(R.string.edt_email_empty_message)
                }
                else -> {
                    setLoadingState(true)

                    lifecycleScope.launchWhenResumed {
                        if (loginJob.isActive) loginJob.cancel()
                        loginJob = launch {
                            viewModels.loginUser(email, password).observe(requireActivity()) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        setLoadingState(true)
                                    }
                                    is Result.Success -> {
                                        dataStoreViewModel.saveAuthToken(result.data.loginResult.token)
                                        Intent(requireContext(), MainActivity::class.java).also { intent ->
                                            intent.putExtra(EXTRA_TOKEN, result.data.loginResult.token)
                                            startActivity(intent)
                                        }
                                        Toast.makeText(requireActivity(), "Login success", Toast.LENGTH_SHORT).show()
                                        setLoadingState(false)
                                        requireActivity().finish()
                                    }
                                    is Result.Error -> {
                                        Toast.makeText(requireActivity(), "Login gagal", Toast.LENGTH_SHORT).show()
                                        setLoadingState(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(state: Boolean) {
        binding?.apply {
            edtEmailLayout.isEnabled = !state
            edtPasswordLayout.isEnabled = !state

            if (state) {
                ObjectAnimator.ofFloat(binding?.progressBar, View.ALPHA, 1f).start()
            } else {
                ObjectAnimator.ofFloat(binding?.progressBar, View.ALPHA, 0f).start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}