package pl.perski.eattogether.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.content_event.*
import pl.perski.eattogether.R
import pl.perski.eattogether.model.EventModel
import pl.perski.eattogether.utils.*
import pl.perski.eattogether.viewModel.EventViewModel
import pl.perski.eattogether.viewModel.factory.EventViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

//todo add field validation
class EventActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    companion object {
        const val EVENT_MODE_NAME = "EVENT_MODE"
        const val ADD_EVENT_CODE = 0
        const val VIEW_EVENT_CODE = 1
        const val EVENT_DATA_NAME = "EVENT_DATA"
        const val EVENT_RESULT_MESSAGE = "EVENT_RESULT_MESSAGE"
    }

    private val compositeDisposable = CompositeDisposable()
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var viewModel: EventViewModel
    private var eventId: Int = -1
    private var activityMode: Int = -1
    private val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0
    private var myDay = 0
    private var myMonth: Int = 0
    private var myYear: Int = 0
    private var myHour: Int = 0
    private var myMinute: Int = 0
    private lateinit var eventDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        activityMode = intent.getIntExtra(EVENT_MODE_NAME, -1)
        if (activityMode == -1) {
            finish()
        }
        setControls(activityMode)
        setSupportActionBar(toolbar)
        sharedPrefHelper = SharedPrefHelper(this)
        viewModel =
            ViewModelProvider(this, EventViewModelFactory(sharedPrefHelper.token!!, eventId)).get(
                EventViewModel::class.java
            )
        bindUIData()
        bindUIGestures()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun bindUIGestures() {
        val disposableGetDate = etEventDate.clicks().observeOnMainThread().subscribe {
            showTimePicker()
        }

        val disposableJoinEvent = btnJoinEvent.clicks().observeOnMainThread().subscribe {
            viewModel.joinEvent()
        }
        val disposableLeftEvent = btnLeftEvent.clicks().observeOnMainThread().subscribe {
            viewModel.leftFromEvent()
        }
        val disposableDeleteEvent = btnDeleteEvent.clicks().observeOnMainThread().subscribe {
            viewModel.deleteEvent()
        }

        val disposableAddEvent = btnAddEvent.clicks().observeOnMainThread().subscribe {
            viewModel.addEvent(
                EventModel(
                    date = Date(),
                    placeName = etPlaceName.text.toString(),
                    description = etEventDesc.text.toString(),
                    placeLocation = etLocation.text.toString(),
                    locationLongitude = 52.408756,
                    locationLatitude = 16.920957
                )
            )
        }
        compositeDisposable.addAll(
            disposableAddEvent,
            disposableDeleteEvent,
            disposableGetDate,
            disposableJoinEvent,
            disposableLeftEvent
        )
    }

    private fun showTimePicker() {
        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        val datePickerDialog =
            DatePickerDialog(this, this, year, month, day)
        datePickerDialog.show()
    }

    private fun bindUIData() {
        viewModel.result.subscribe(this, ::showSnackBar)
        viewModel.progress.subscribe(this, ::updateProgress)
        viewModel.errors.subscribe(this, ::showErrorMessage)
    }

    private fun updateProgress(isDownloading: Boolean) {
        progressBarEvent.show(isDownloading)
    }

    private fun showErrorMessage(error: ErrorMessage) {
        showSnackBar(error.getMessage())
    }

    private fun showSnackBar(message: String) {
        val data = Intent().apply {
            putExtra(EVENT_RESULT_MESSAGE, message)
        }
        setResult(RESULT_OK, data)
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun setControls(activityMode: Int) {
        if (activityMode == ADD_EVENT_CODE) {
            toolbar.title = getString(R.string.title_activity_add_event)
            btnAddEvent.visibility = View.VISIBLE
        } else {
            toolbar.title = getString(
                R.string.title_activity_event
            )
            if (intent.extras?.get(EVENT_DATA_NAME) != null) {
                val event: EventModel = intent.getSerializableExtra(EVENT_DATA_NAME) as EventModel
                eventId = event.id!!
                tvHeader.text = "${getString(R.string.eventLayoutHeader)}\n${event.placeName}"
                etEventDate.isClickable = false
                etPlaceName.isClickable = false
                etPlaceName.isLongClickable = false
                etPlaceName.focusable = View.NOT_FOCUSABLE
                etEventDesc.isClickable = false
                etEventDesc.isLongClickable = false
                etEventDesc.focusable = View.NOT_FOCUSABLE
                etParticipants.visibility = View.VISIBLE

                etPlaceName.setText(event.placeName)
                etEventDate.setText(formatter.format(event.date))
                etLocation.setText(event.placeLocation)
                etEventDesc.setText(event.description)
                etParticipants.setText(event.participants)
                if (!event.callerJoin!!) {
                    btnJoinEvent.visibility = View.VISIBLE
                } else if (event.callerIsCreator!!) {
                    btnDeleteEvent.visibility = View.VISIBLE
                } else {
                    btnLeftEvent.visibility = View.VISIBLE
                }
            } else {
                finish()
            }

        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month
        myMonth++
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this, this, hour, minute,
            DateFormat.is24HourFormat(this)
        )
        timePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        val dateTime = LocalDateTime.of(myYear, myMonth, myDay, myHour, myMinute)
        eventDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant())
        etEventDate.setText("$myYear-$myMonth-$myDay $myHour:${String.format("%02d", myMinute)}")
    }
}
