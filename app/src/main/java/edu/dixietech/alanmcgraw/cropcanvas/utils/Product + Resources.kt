package edu.dixietech.alanmcgraw.cropcanvas.utils

import androidx.annotation.DrawableRes
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product

@DrawableRes
fun Product.drawableResource(): Int {
    return when (name) {
        "Blueberries" -> R.drawable.blueberries
        "Carrots" -> R.drawable.carrots
        "Cauliflower" -> R.drawable.cauliflower
        "Corn" -> R.drawable.corn
        "Melons" -> R.drawable.melons
        "Peppers" -> R.drawable.peppers
        "Pomegranates" -> R.drawable.pomegranates
        "Potatoes" -> R.drawable.potato
        "Pumpkins" -> R.drawable.pumpkin_seeds
        else -> R.drawable.wheat
    }
}