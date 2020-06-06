package pl.perski.eattogether.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import pl.perski.eattogether.R
import pl.perski.eattogether.model.AccountModel
import pl.perski.eattogether.utils.*
import pl.perski.eattogether.viewModel.LoginViewModel
//todo add field validation
class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    private val compositeDisposable = CompositeDisposable()
    lateinit var sharedPrefHelper: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        toolbar.title = getString(R.string.title_activity_login)
        setSupportActionBar(toolbar)
        sharedPrefHelper = SharedPrefHelper(this)
        bindUIData()
        bindUIGestures()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


    private fun bindUIData() {
        viewModel.tokenHeader.subscribe(this, ::goToApplication)
        viewModel.progress.subscribe(this, ::updateProgress)
        viewModel.errors.subscribe(this, ::showErrorMessage)
    }

    private fun goToApplication(tokenHeader: String) {
        //todo change to startActivityForResult() to dismiss problem with do not finish login activity after register
        sharedPrefHelper.token = tokenHeader
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun bindUIGestures() {
        val disposableLogin = btnSignUp.clicks().observeOnMainThread().subscribe {
            viewModel.signIn(
                AccountModel(
                    email = etEmail.text.toString(),
                    password = etPlaceName.text.toString()
                )
            )
        }

        val disposableSignUp = btnGoToSignUp.clicks().observeOnMainThread().subscribe {
            goToSignUp()
        }
        compositeDisposable.addAll(disposableLogin, disposableSignUp)
    }

    private fun goToSignUp() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra(RegisterActivity.ACTIVITY_MODE_NAME, RegisterActivity.REGISTER_CODE)
        startActivity(intent)
    }


    private fun showHeader(header: String) {
        tvAppName.text = header
    }

    private fun updateProgress(isDownloading: Boolean) {
        progressBarRegister.show(isDownloading)
    }

    private fun showErrorMessage(error: ErrorMessage) {
        Snackbar.make(
            rootLoginLayout,
            error.getMessage(),
            Snackbar.LENGTH_SHORT
        ).show()
    }

}
