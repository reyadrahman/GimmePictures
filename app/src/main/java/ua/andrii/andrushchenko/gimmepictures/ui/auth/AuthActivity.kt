package ua.andrii.andrushchenko.gimmepictures.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository.Companion.unsplashAuthCallback
import ua.andrii.andrushchenko.gimmepictures.databinding.ActivityAuthBinding
import ua.andrii.andrushchenko.gimmepictures.util.CustomTabsHelper
import ua.andrii.andrushchenko.gimmepictures.util.Result

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            openUnsplashLoginTab()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            if (uri.authority.equals(unsplashAuthCallback)) {
                uri.getQueryParameter("code")?.let { code ->
                    viewModel.getAccessToken(code).observe(this) {
                        when (it) {
                            is Result.Loading -> {

                            }
                            is Result.Success -> {
                                Toast.makeText(this,
                                    getString(R.string.login_successful),
                                    Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is Result.Error, Result.NetworkError -> {
                                Toast.makeText(this,
                                    getString(R.string.login_failed),
                                    Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openUnsplashLoginTab() = openCustomTab(viewModel.loginUrl)

    private fun openCustomTab(url: String) {
        CustomTabsHelper.openCustomTab(this, Uri.parse(url))
    }

}