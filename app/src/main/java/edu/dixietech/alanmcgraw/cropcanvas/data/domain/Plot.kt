package edu.dixietech.alanmcgraw.cropcanvas.data.domain

import androidx.annotation.DrawableRes

data class Plot(
    val id: String,
    val name: String,
    val size: Int,
    val plant: Plant? = null,
    @DrawableRes val drawableResource: Int? = null,
    val price: Int? = null,
)
