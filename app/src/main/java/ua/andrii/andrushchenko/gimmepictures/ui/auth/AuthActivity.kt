package ua.andrii.andrushchenko.gimmepictures.ui.auth

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.*
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepositoryImpl.Companion.unsplashAuthCallback
import ua.andrii.andrushchenko.gimmepictures.databinding.ActivityAuthBinding
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.CustomTabsHelper
import ua.andrii.andrushchenko.gimmepictures.util.toast

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null
    private var shouldUnbindCustomTabService = false

    private val customTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(
            name: ComponentName,
            client: CustomTabsClient
        ) {
            customTabsClient = client
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            customTabsClient = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupCustomTabs()

        with(binding) {
            toolbar.setNavigationOnClickListener { finish() }

            val animationDrawable = coordinatorLayout.background as AnimationDrawable
            animationDrawable.setEnterFadeDuration(2000)
            animationDrawable.setExitFadeDuration(4000)
            animationDrawable.start()

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
                                binding.layoutProgress.visibility = View.VISIBLE
                            }
                            is BackendResult.Success -> {
                                binding.layoutProgress.visibility = View.GONE
                                toast(R.string.login_successful)
                                setResult(RESULT_OK)
                                finish()
                            }
                            is BackendResult.Error -> {
                                binding.layoutProgress.visibility = View.GONE
                                toast(R.string.login)
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

    private fun setupCustomTabs() {
        CustomTabsHelper.getPackageNameToUse(this)?.let { customTabsPackageName ->
            if (CustomTabsClient.bindCustomTabsService(
                    this,
                    customTabsPackageName,
                    customTabsServiceConnection
                )
            ) {
                shouldUnbindCustomTabService = true
                customTabsClient?.warmup(0)
                customTabsSession = customTabsClient?.newSession(CustomTabsCallback())?.apply {
                    mayLaunchUrl(
                        Uri.parse(viewModel.loginUrl),
                        null,
                        mutableListOf(
                            Bundle().apply {
                                putParcelable(
                                    CustomTabsService.KEY_URL,
                                    Uri.parse(getString(R.string.unsplash_join_url))
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (shouldUnbindCustomTabService) {
            unbindService(customTabsServiceConnection)
            shouldUnbindCustomTabService = false
        }
        customTabsClient = null
        customTabsSession = null
    }
}
