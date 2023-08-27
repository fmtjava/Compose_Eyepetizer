package com.fmt.compose.eyepetizer.pages.discover.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.Category
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    val categoryList = mutableStateListOf<Category>()
    var refreshing = mutableStateOf(false)

    init {
        getCategoryList()
    }

    fun getCategoryList() {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                categoryList.clear()
                categoryList.addAll(ApiService.getCategoryList())
                refreshing.value = false
            }.onFailure {
                refreshing.value = false
            }
        }
    }
}