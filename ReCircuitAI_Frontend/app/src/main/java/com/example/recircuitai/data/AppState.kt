package com.example.recircuitai.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object GlobalAppState {
    var isCompanyMode by mutableStateOf(false)
    var selectedItem: RecycleItem? = null
}
