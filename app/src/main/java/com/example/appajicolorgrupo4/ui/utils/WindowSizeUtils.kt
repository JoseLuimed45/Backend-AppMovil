package com.example.appajicolorgrupo4.ui.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.activity.compose.LocalActivity

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun getWindowSizeClass(): WindowSizeClass {
    val activity = LocalActivity.current
    return calculateWindowSizeClass(activity!!)
}