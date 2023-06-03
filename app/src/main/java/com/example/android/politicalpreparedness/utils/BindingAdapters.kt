package com.example.android.politicalpreparedness.utils

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.view.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("electionList")
fun RecyclerView.setElectionData(electionList: List<Election>?) {
    val adapter = adapter as ElectionListAdapter
    adapter.submitList(electionList)
}

@BindingAdapter("upcomingImage")
fun bindApiStatus(imageView: ImageView, status: UpcomingImageStatus?) {
    when (status) {
        UpcomingImageStatus.LOADING -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.loading_animation)
        }
        UpcomingImageStatus.ERROR -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.ic_connection_error)
        }
        UpcomingImageStatus.DONE -> {
            imageView.visibility = View.GONE
        }
        else -> {
            Timber.e("Error: %s", "Null state!")
        }
    }
}

@BindingAdapter("electionDay")
fun TextView.setElectionDay(date: Date?) {
    val calendar = Calendar.getInstance()
    date?.let { dateTime ->
        calendar.time = dateTime
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        text = dateFormat.format(calendar.time)
    }
}

@BindingAdapter("electionButtonText")
fun Button.isElectionSaved(isSaved: Boolean?) {
    isSaved?.let {
        text =
            if (it) resources.getString(R.string.unfollow_btn) else resources.getString(R.string.follow_btn)
    }
}
