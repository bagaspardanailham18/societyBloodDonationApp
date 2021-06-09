package com.capstoneproject.society.ui.personaluser.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.society.model.OrganizationSupplyItem
import com.capstoneproject.society.model.PersonalUser

class NotificationsViewModel : ViewModel() {

    private val listNotification = MutableLiveData<ArrayList<PersonalUser>>()

    fun setNotification(iddonor: String) {
        val listItems = ArrayList<PersonalUser>()
    }
}