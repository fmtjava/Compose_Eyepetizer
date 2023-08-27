package com.fmt.compose.eyepetizer.pages.person.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.mainApplication

class PersonViewModel : ViewModel() {

    val titles = mutableStateListOf(
        mainApplication.getString(R.string.my_message),
        mainApplication.getString(R.string.my_record),
        mainApplication.getString(R.string.my_cache),
        mainApplication.getString(R.string.watch_record),
        mainApplication.getString(R.string.my_barrage),
        mainApplication.getString(R.string.my_add_chase),
        mainApplication.getString(R.string.personalized_attire),
        mainApplication.getString(R.string.my_bookshelf),
        mainApplication.getString(R.string.my_chicken_leg),
        mainApplication.getString(R.string.my_circle),
        mainApplication.getString(R.string.my_order),
        mainApplication.getString(R.string.my_love),
        mainApplication.getString(R.string.study_center),
        mainApplication.getString(R.string.feedback),
    )
}