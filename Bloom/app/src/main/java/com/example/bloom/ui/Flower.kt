package com.example.bloom.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.bloom.R

//Flower composable for displaying the flower image
@Composable
fun Flower(count: Int){

    //Showing the appropriate flower image based on the count
    val imageResource = when (count)
    {
        1 -> R.drawable.flower_1
        2 -> R.drawable.flower_2
        3 -> R.drawable.flower_3
        4 -> R.drawable.flower_4
        5 -> R.drawable.flower_5
        else -> R.drawable.flower_0
    }

    //Displaying the flower image using the Image composable
    Image(
        painter = painterResource(imageResource),
        contentDescription = "Completed tasks"
    )
}