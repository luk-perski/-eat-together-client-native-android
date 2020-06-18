package pl.perski.eattogether.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import pl.perski.eattogether.R
import pl.perski.eattogether.adapter.RecyclerAdapter
import pl.perski.eattogether.model.EventModel
import pl.perski.eattogether.utils.*
import pl.perski.eattogether.viewModel.MainViewModel
import pl.perski.eattogether.viewModel.factory.MainViewModelFactory


class MainActivity : AppCompatActivity() {


    companion object {
        const val EVENT_ACTIVITY_RESULT = 10
        const val ADD_EVENT_ACTIVITY_RESULT = 20
        const val UPDATE_USER_ACTIVITY_RESULT = 30
    }

    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var viewModel: MainViewModel
    private var disposable: Disposable? = null
    private var linearLayoutManager = LinearLayoutManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        sharedPrefHelper = SharedPrefHelper(this)
        if (!sharedPrefHelper.checkIfExists(SharedPrefHelper.TOKEN)) {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            viewModel = ViewModelProvider(this, MainViewModelFactory(sharedPrefHelper.token!!))
                .get(MainViewModel::class.java)
            bindUIData()
            bindUIGestures()
            setupRecyclerView()
            viewModel.getEventsData()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.getEventsData()
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, RegisterActivity::class.java)
                intent.putExtra(RegisterActivity.ACTIVITY_MODE_NAME, RegisterActivity.UPDATE_CODE)
                startActivityForResult(intent, UPDATE_USER_ACTIVITY_RESULT)
                true
            }
            R.id.action_log_out -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.getEventsData()
        if (requestCode == EVENT_ACTIVITY_RESULT || requestCode == ADD_EVENT_ACTIVITY_RESULT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                data.apply {
                    showSnackBar(getStringExtra(EventActivity.EVENT_RESULT_MESSAGE)!!)
                }
            } else {
                // todo
            }
        } else if (requestCode == UPDATE_USER_ACTIVITY_RESULT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                data.apply {
                    showSnackBar(getStringExtra(RegisterActivity.UPDATE_RESULT_MESSAGE)!!)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerViewMain.layoutManager = linearLayoutManager
    }

    private fun bindUIGestures() {
        disposable = fabAddEvent.clicks()
            .observeOnMainThread()
            .subscribe {
                val intent = Intent(this, EventActivity::class.java)
                intent.putExtra(EventActivity.EVENT_MODE_NAME, EventActivity.ADD_EVENT_CODE)
                startActivityForResult(intent, EVENT_ACTIVITY_RESULT)
            }
    }

    private fun bindUIData() {
        viewModel.events.subscribe(this, ::showAllCoins)
        viewModel.progress.subscribe(this, ::updateProgress)
        viewModel.errors.subscribe(this, ::showErrorMessage)
    }

    private fun updateProgress(isDownloading: Boolean) {
        progressBarMain.show(isDownloading)
    }

    private fun showErrorMessage(error: ErrorMessage) {
        showSnackBar(error.getMessage())
    }

    private fun logout() {
        sharedPrefHelper.clearAll()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showAllCoins(list: List<EventModel>) {
        recyclerViewMain.adapter = RecyclerAdapter(list)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            rootMainLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

}


