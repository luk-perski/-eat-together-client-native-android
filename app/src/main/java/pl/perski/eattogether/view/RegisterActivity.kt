package pl.perski.eattogether.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import com.location.aravind.getlocation.GeoLocator
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.content_register.*
import pl.perski.eattogether.R
import pl.perski.eattogether.model.AccountModel
import pl.perski.eattogether.model.AddAccountModel
import pl.perski.eattogether.model.UserModel
import pl.perski.eattogether.utils.*
import pl.perski.eattogether.viewModel.RegisterViewModel

//todo add field validation
class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        const val ACTIVITY_MODE_NAME = "ACTIVITY_MODE"
        const val REGISTER_CODE = 0
        const val UPDATE_CODE = 1
        const val UPDATE_RESULT_MESSAGE = "UPDATE_RESULT_MESSAGE"
    }


    private val compositeDisposable = CompositeDisposable()
    private val viewModel by lazy { ViewModelProvider(this).get(RegisterViewModel::class.java) }
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var spinDistanceAdapter: ArrayAdapter<CharSequence>
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var activityMode: Int = -1
    private var defaultDistance = 1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        sharedPrefHelper = SharedPrefHelper(this)
        activityMode = intent.getIntExtra(ACTIVITY_MODE_NAME, -1)
        if (activityMode == -1) {
            finish()
        }
        setControls(activityMode)
        setSupportActionBar(toolbar)
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 0)
        bindUIData()
        bindUIGestures()
    }

    private fun setControls(activityMode: Int) {
        spinDistanceAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.distance_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinDistance.adapter = adapter
        }
        if (activityMode == REGISTER_CODE) {
            toolbar.title = getString(R.string.app_name)
            etEmail.visibility = View.VISIBLE
            etPlaceName.visibility = View.VISIBLE
            btnSignUp.visibility = View.VISIBLE
        } else {
            tvAppName.text = getString(R.string.update_user_header)
            toolbar.title = getString(R.string.update_user_toolbar)
            viewModel.getUser(sharedPrefHelper.token!!)
            btnUpdateUser.visibility = View.VISIBLE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


    private fun bindUIData() {
        viewModel.progress.subscribe(this, ::updateProgress)
        viewModel.errors.subscribe(this, ::showErrorMessage)
        viewModel.tokenHeader.subscribe(this, ::goToApplication)
        viewModel.apiError.subscribe(this, ::showErrorMessage)
        viewModel.message.subscribe(this, ::showSnackBar)
        viewModel.user.subscribe(this, ::setUser)
    }

    private fun setUser(userModel: UserModel) {
        etFirstName.setText(userModel.firstName)
        etLastName.setText(userModel.lastName)
        etCompany.setText(userModel.companyName)
        etLocalization.setText(userModel.userLocationAddress)
        etDescription.setText(userModel.description)
        var spinDistanceDefaultSelection = 2
        spinDistanceDefaultSelection = try {
            spinDistanceAdapter.getPosition(userModel.distanceRange.toString())
        } finally {
            spinDistance.setSelection(spinDistanceDefaultSelection)
        }
    }

    private fun goToApplication(tokenHeader: String) {
        sharedPrefHelper.token = tokenHeader
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    private fun bindUIGestures() {
        val disposableSignUp = btnSignUp.clicks().observeOnMainThread().subscribe {
            val userModel = UserModel(
                firstName = etFirstName.text.toString(),
                lastName = etLastName.text.toString(),
                userLocationLatitude = latitude,
                userLocationLongitude = longitude,
                userLocationAddress = etLocalization.text.toString(),
                companyName = etCompany.text.toString(),
                description = etDescription.text.toString(),
                distanceRange = defaultDistance
            )
            val accountModel =
                AccountModel(
                    email = etEmail.text.toString(),
                    password = etPlaceName.text.toString()
                )
            viewModel.signUp(AddAccountModel(accountModel, userModel))
        }

        val disposableLocation = btnLocalization.clicks().observeOnMainThread().subscribe {
            getLocation()
        }

        val disposableUpdate = btnUpdateUser.clicks().observeOnMainThread().subscribe {
            val userModel = UserModel(
                firstName = etFirstName.text.toString(),
                lastName = etLastName.text.toString(),
                userLocationLatitude = latitude,
                userLocationLongitude = longitude,
                userLocationAddress = etLocalization.text.toString(),
                companyName = etCompany.text.toString(),
                description = etDescription.text.toString(),
                distanceRange = defaultDistance
            )
            viewModel.update(userModel, sharedPrefHelper.token!!)
        }

        compositeDisposable.addAll(disposableSignUp, disposableLocation, disposableUpdate)
    }

    private fun getLocation() {
        val geoLocator = GeoLocator(this, this@RegisterActivity)
        latitude = geoLocator.lattitude
        longitude = geoLocator.longitude
        etLocalization.setText(geoLocator.address)
    }

    private fun updateProgress(isDownloading: Boolean) {
        progressBarRegister.show(isDownloading)
    }

    private fun showErrorMessage(error: ErrorMessage) {
        Snackbar.make(
            rootRegisterLayout,
            error.getMessage(),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showSnackBar(message: String) {
        val data = Intent().apply {
            putExtra(UPDATE_RESULT_MESSAGE, message)
        }
        setResult(RESULT_OK, data)
        finish()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        try {
            defaultDistance = p0?.getItemAtPosition(p2).toString().toDouble()
        } catch (e: NumberFormatException) {
            print("Error during getting distance value.")
        }
    }

}
