package com.example.fitnesspaw.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Responsive {

    @Composable
    fun screenWidth() =

        LocalConfiguration.current.screenWidthDp

    @Composable
    fun screenHeight() =

        LocalConfiguration.current.screenHeightDp

    // RESPONSIVE FONT

    @Composable
    fun titleSize() =

        if (screenWidth() < 360)
            24.sp
        else
            30.sp

    @Composable
    fun normalText() =

        if (screenWidth() < 360)
            14.sp
        else
            16.sp

    // RESPONSIVE IMAGE

    @Composable
    fun petSize() =

        if (screenWidth() < 360)
            140.dp
        else
            180.dp

    // RESPONSIVE PADDING

    @Composable
    fun screenPadding() =

        if (screenWidth() < 360)
            16.dp
        else
            20.dp
}