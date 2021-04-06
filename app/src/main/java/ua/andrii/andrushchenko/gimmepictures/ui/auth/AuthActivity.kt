package ua.andrii.andrushchenko.gimmepictures.ui.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository.Companion.unsplashAuthCallback
import ua.andrii.andrushchenko.gimmepictures.databinding.ActivityAuthBinding
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.customtabs.CustomTabsHelper
import ua.andrii.andrushchenko.gimmepictures.util.loadBlurredImage

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        with(binding) {
            toolbar.setNavigationOnClickListener { finish() }
            viewModel.backgroundPhoto.observe(this@AuthActivity) { photo ->
                bgImage.loadBlurredImage(
                    url = photo.urls.small,
                    placeholderColorDrawable = ColorDrawable(Color.parseColor(photo.color))
                )
            }

            btnLogin.setOnClickListener {
                openUnsplashLoginTab()
            }

            btnJoin.setOnClickListener {
                openUnsplashJoinTab()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            if (uri.authority.equals(unsplashAuthCallback)) {
                uri.getQueryParameter("code")?.let { code ->
                    viewModel.getAccessToken(code).observe(this) {
                        when (it) {
                            is BackendResult.Loading -> {
                                binding.authProgress.visibility = View.VISIBLE
                            }
                            is BackendResult.Success -> {
                                binding.authProgress.visibility = View.GONE
                                Toast.makeText(this,
                                    getString(R.string.login_successful),
                                    Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK)
                                finish()
                            }
                            is BackendResult.Error -> {
                                binding.authProgress.visibility = View.GONE
                                Toast.makeText(this,
                                    getString(R.string.login_failed),
                                    Toast.LENGTH_SHORT).show()
                                setResult(RESULT_CANCELED)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openUnsplashJoinTab() = openCustomTab(getString(R.string.unsplash_join_url))

    private fun openUnsplashLoginTab() = openCustomTab(viewModel.loginUrl)

    private fun openCustomTab(url: String) {
        CustomTabsHelper.openCustomTab(this, Uri.parse(url))
    }
}
