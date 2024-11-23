package com.example.xpensate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedCurrency = MutableLiveData<Triple<String, String, String>>()
    val selectedCurrency: LiveData<Triple<String, String, String>> get() = _selectedCurrency

    private val _isFromSelected = MutableLiveData<Boolean>()
    val isFromSelected: LiveData<Boolean> get() = _isFromSelected

    fun setSelectedCurrency(shortForm: String, heading: String, symbol: String) {
        _selectedCurrency.value = Triple(shortForm, heading, symbol)
    }

    fun setIsFromSelected(isFrom: Boolean) {
        _isFromSelected.value = isFrom
    }
}