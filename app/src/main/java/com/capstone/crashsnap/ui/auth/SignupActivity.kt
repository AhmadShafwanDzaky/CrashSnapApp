package com.capstone.crashsnap.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.capstone.crashsnap.R
import com.capstone.crashsnap.ViewModelFactory
import com.capstone.crashsnap.data.NetResult
import com.capstone.crashsnap.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        validSignup()

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }

    }

    private fun signup(name: String, email: String, password: String) {
        viewModel.signup(name, email, password)
            .observe(this@SignupActivity) { result ->
                if (result != null) {
                    when (result) {
                        is NetResult.Loading -> {
                            showLoading(true)
                        }

                        is NetResult.Success -> {
                            showLoading(false)
                            val message = result.data.message
                            if (!result.data.error) {
                                showToast(message)
                                finish()
                            } else {
                                showToast(message)
                            }
                        }

                        is NetResult.Error -> {
                            showLoading(false)
                            showToast(result.error)
                        }


                    }
                }
            }
    }

    private fun validSignup() {
        binding.edName.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                binding.edtextlayoutName.error = getString(R.string.msg_required)
            } else {
                binding.edtextlayoutName.error = null
            }
        }

        binding.edEmail.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                binding.edtextlayoutEmail.error = getString(R.string.msg_required)
            } else {
                binding.edtextlayoutEmail.error = null
            }
        }

        binding.edPw.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                binding.edtextlayoutPw.error = getString(R.string.msg_required)
            } else {
                binding.edtextlayoutPw.error = null
            }
        }

        binding.btnSignup.setOnClickListener {
            val name = binding.edName.text.toString()
            val email = binding.edEmail.text.toString()
            val password = binding.edPw.text.toString()

            var valid = true

            if (name.isEmpty()) {
                binding.edtextlayoutName.error = getString(R.string.msg_required)
                valid = false
            }

            if (email.isEmpty()) {
                binding.edtextlayoutEmail.error = getString(R.string.msg_required)
                valid = false
            }

            if (password.isEmpty()) {
                binding.edtextlayoutPw.error = getString(R.string.msg_required)
                valid = false
            }

            if (valid) {
                signup(name, email, password)
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE

        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "Signupact"
    }
}