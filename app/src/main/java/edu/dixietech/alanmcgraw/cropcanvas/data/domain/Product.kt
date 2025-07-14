package edu.dixietech.alanmcgraw.cropcanvas.data.domain

import androidx.annotation.DrawableRes

data class Product(
    val name: String,
    val amount: Int,
    val worth: Int,
    @DrawableRes val drawableResource: Int,
)