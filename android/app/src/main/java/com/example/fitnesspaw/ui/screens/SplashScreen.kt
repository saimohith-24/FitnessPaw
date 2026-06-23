package com.example.fitnesspaw.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesspaw.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(

    onSplashFinished: () -> Unit
) {

    var startAnim by remember {

        mutableStateOf(false)
    }

    val scale = animateFloatAsState(

        targetValue =

            if (startAnim)
                1f

            else
                0.7f,

        animationSpec = tween(
            durationMillis = 1200
        ),

        label = ""
    )

    LaunchedEffect(Unit) {

        startAnim = true

        delay(2500)

        onSplashFinished()
    }

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(

                Brush.verticalGradient(

                    colors = listOf(

                        Color(0xFF020617),

                        Color(0xFF081120),

                        Color(0xFF0F172A)
                    )
                )
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Image(

                painter = painterResource(
                    id = R.drawable.fitnesspaw_logo
                ),

                contentDescription = null,

                modifier = Modifier
                    .size(200.dp)
                    .scale(scale.value),

                contentScale =
                    ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            androidx.compose.material3.Text(

                text = "FitnessPaw",

                color = Color.White,

                fontSize = 34.sp
            )
        }
    }
}