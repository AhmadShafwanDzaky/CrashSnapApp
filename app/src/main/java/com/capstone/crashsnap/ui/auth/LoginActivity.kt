package com.capstone.crashsnap.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.capstone.crashsnap.R
import com.capstone.crashsnap.ViewModelFactory
import com.capstone.crashsnap.data.NetResult
import com.capstone.crashsnap.databinding.ActivityLoginBinding
import com.capstone.crashsnap.ui.main.MainActivity
import com.capstone.crashsnap.ui.main.MainActivity.Companion.EXTRA_EMAIL
import com.capstone.crashsnap.ui.main.MainActivity.Companion.EXTRA_NAME
import com.capstone.crashsnap.ui.main.MainActivity.Companion.EXTRA_TOKEN

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                val moveToMain = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(moveToMain)
                finish()
            } else {
                binding = ActivityLoginBinding.inflate(layoutInflater)
                setContentView(binding.root)
                setupView()
            }
        }
    }

    private fun setupView() {
        validLogin()

        binding.tvSignUp.setOnClickListener {
            val moveToSignup = Intent(this, SignupActivity::class.java)
            startActivity(moveToSignup)
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password).observe(this@LoginActivity) { result ->
            if (result != null) {
                when (result) {
                    is NetResult.Loading -> {
                        showLoading(true)
                    }
                    is NetResult.Success -> {
                        showLoading(false)
                        val data = result.data.loginResult
                        viewModel.saveSession(data.token, data.displayName, email )
                        if (!result.data.error) {
                            val moveToMain = Intent(this, MainActivity::class.java)
                            viewModel.saveSession(data.token, data.displayName, email )
                            moveToMain.putExtra(EXTRA_TOKEN, data.token)
                            moveToMain.putExtra(EXTRA_NAME, data.displayName)
                            moveToMain.putExtra(EXTRA_EMAIL, email)
                            startActivity(moveToMain)
                            finish()
                        } else {
                            showToast(result.data.message)

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


    private fun validLogin() {

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

        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPw.text.toString()

            var valid = true

            if (email.isEmpty()) {
                binding.edtextlayoutEmail.error = getString(R.string.msg_required)
                valid = false
            }

            if (password.isEmpty()) {
                binding.edtextlayoutPw.error = getString(R.string.msg_required)
                valid = false
            }

            if (valid) {
                login(email, password)
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
        const val TAG = "login act"
    }
}