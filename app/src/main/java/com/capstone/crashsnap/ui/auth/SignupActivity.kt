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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.log

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
            startActivity(Intent(this, LoginActivity::class.java))
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
                            Log.d(TAG, "onCreate suk gagal: $message")
                            if (!result.data.error) {
                                Log.d(TAG, "onCreate suk: $message")
                                showToast(message)
                                finish()
                            } else {
                                Log.d(TAG, "onCreate err: $message , ${result.data.error}")
                                showToast(message)
                            }
                        }

                        is NetResult.Error -> {
                            showLoading(false)
                            Log.d(TAG, "onCreate err failed net:  ${result.error}")
                            showToast(result.error)
                        }


                    }
                }
            }
    }

    private fun validSignup() {
        binding.edName.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                binding.edName.error = getString(R.string.msg_required)
            } else {
                binding.edName.error = null
            }
        }

        binding.edEmail.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                binding.edEmail.error = getString(R.string.msg_required)
            } else {
                binding.edEmail.error = null
            }
        }

        binding.edPw.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                binding.edPw.error = getString(R.string.msg_required)
            } else {
                binding.edPw.error = null
            }
        }

        binding.btnSignup.setOnClickListener {
            val name = binding.edName.text.toString()
            val email = binding.edEmail.text.toString()
            val password = binding.edPw.text.toString()

            var valid = true

            if (name.isEmpty()) {
                binding.edName.error = getString(R.string.msg_required)
                valid = false
            }

            if (email.isEmpty()) {
                binding.edEmail.error = getString(R.string.msg_required)
                valid = false
            }

            if (password.isEmpty()) {
                binding.edPw.error = getString(R.string.msg_required)
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