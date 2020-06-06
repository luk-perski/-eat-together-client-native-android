package pl.perski.eattogether.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*
import pl.perski.eattogether.R
import pl.perski.eattogether.model.EventModel
import pl.perski.eattogether.utils.inflate
import pl.perski.eattogether.view.EventActivity
import pl.perski.eattogether.view.MainActivity
import java.text.SimpleDateFormat
import java.util.*


class RecyclerAdapter(private val events: List<EventModel>) :
    RecyclerView.Adapter<RecyclerAdapter.EventHolder>() {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return EventHolder(inflatedView)
    }

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val itemPhoto = events[position]
        holder.bindEvent(itemPhoto)
    }

    class EventHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var event: EventModel? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val context = itemView.context
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(EventActivity.EVENT_MODE_NAME, EventActivity.VIEW_EVENT_CODE)
            intent.putExtra(EventActivity.EVENT_DATA_NAME, event)
            if (context is Activity) {
                context.startActivityForResult(
                    intent,
                    MainActivity.EVENT_ACTIVITY_RESULT
                )
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindEvent(event: EventModel) {
            val now = Date()
            var format = "EEEE, MMMM d"
            if (Date(now.year, now.month, now.day) == Date(
                    event.date.year,
                    event.date.month,
                    event.date.day
                )
            ) {
                format = "HH:mm"
            }
            if (event.callerJoin!!) {
                view.title.setTextColor(Color.parseColor("#FF4CAF50"))
            }
            this.event = event
            view.title.text = "${event.placeName} (${SimpleDateFormat(format).format(event.date)})"
            view.subtitle.text = "Added by ${event.creatorName}"
            view.eventAvatar.setInitials("A B") //todo
        }
    }
}