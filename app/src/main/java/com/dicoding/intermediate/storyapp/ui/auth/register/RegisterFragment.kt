package com.dicoding.intermediate.storyapp.ui.auth.register

import android.animation.ObjectAnimator
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
import com.dicoding.intermediate.storyapp.data.remote.Result
import com.dicoding.intermediate.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.intermediate.storyapp.ui.ViewModelFactory
import com.dicoding.intermediate.storyapp.ui.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private var registerJob: Job = Job()

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
//        val viewModel: AuthViewModel by viewModels {
//            factory
//        }

        setUpAction()

    }

    private fun setUpAction() {
        binding?.apply {
            toLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            btnRegister.setOnClickListener { handleRegister() }
        }
    }

    private fun handleRegister() {
        binding?.apply {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            when {
                name.isEmpty() -> {
                    edtName.error = getString(R.string.edt_email_empty_message)
                }
                password.isEmpty() -> {
                    edtPassword.error = getString(R.string.edt_email_empty_message)
                }
                else -> {
                    setLoadingState(true)

                    lifecycleScope.launchWhenResumed {
                        if (registerJob.isActive) registerJob.cancel()
                        registerJob = launch {
                            viewModel.registerUser(name, email, password).observe(requireActivity()) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        setLoadingState(true)
                                    }
                                    is Result.Success -> {
                                        Toast.makeText(
                                            requireActivity(),
                                            getString(R.string.registration_success),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        setLoadingState(false)
                                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                    }
                                    is Result.Error -> {
                                        Snackbar.make(
                                            root,
                                            getString(R.string.registration_error_message),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        setLoadingState(false)
                                    }
                                }
        //                        result.onSuccess {
        //                            Toast.makeText(
        //                                requireActivity(),
        //                                getString(R.string.registration_success),
        //                                Toast.LENGTH_SHORT
        //                            ).show()
        //                            setLoadingState(false)
        //                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        //                        }
        //                        result.onFailure {
        //                            Snackbar.make(
        //                                root,
        //                                getString(R.string.registration_error_message),
        //                                Snackbar.LENGTH_SHORT
        //                            ).show()
        //                            setLoadingState(false)
        //                        }
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
            edtNameLayout.isEnabled = !state
            edtPasswordLayout.isEnabled = !state

            if (state) {
                ObjectAnimator.ofFloat(binding!!.progressBar, View.ALPHA, 1f).start()
            } else {
                ObjectAnimator.ofFloat(binding!!.progressBar, View.ALPHA, 0f).start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}