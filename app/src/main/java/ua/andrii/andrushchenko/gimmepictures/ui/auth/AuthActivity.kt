package ua.andrii.andrushchenko.gimmepictures.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.SupportRSBlurTransformation
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

        viewModel.backgroundPhoto.observe(this) { photo ->
            Glide.with(this)
                .load(photo.urls.small)
                .transition(DrawableTransitionOptions.withCrossFade(350))
                .apply(RequestOptions.bitmapTransform(SupportRSBlurTransformation()))
                .into(binding.bgImage)
                .clearOnDetach()
        }

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
                                binding.authProgress.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.authProgress.visibility = View.GONE
                                Toast.makeText(this,
                                    getString(R.string.login_successful),
                                    Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is Result.Error, Result.NetworkError -> {
                                binding.authProgress.visibility = View.GONE
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
