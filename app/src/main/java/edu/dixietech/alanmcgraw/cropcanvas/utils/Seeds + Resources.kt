package edu.dixietech.alanmcgraw.cropcanvas.utils

import androidx.annotation.DrawableRes
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed

@DrawableRes
fun Seed.drawableResource(): Int {
    return when (name) {
        "Blueberry Seeds" -> R.drawable.blueberry_seeds
        "Carrot Seeds" -> R.drawable.carrot_seeds
        "Cauliflower Seeds" -> R.drawable.cauliflower_seeds
        "Corn Seeds" -> R.drawable.corn_seeds
        "Melon Seeds" -> R.drawable.melon_seeds
        "Pepper Seeds" -> R.drawable.pepper_seeds
        "Pomegranate Seeds" -> R.drawable.pomegranate_seeds
        "Potato Seeds" -> R.drawable.potato_seeds
        "Pumpkin Seeds" -> R.drawable.pumpkin_seeds
        else -> R.drawable.wheat_seeds
    }
}