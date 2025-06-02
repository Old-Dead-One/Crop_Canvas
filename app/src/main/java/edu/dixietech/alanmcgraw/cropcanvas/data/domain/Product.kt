package edu.dixietech.alanmcgraw.cropcanvas.data.domain

import androidx.annotation.DrawableRes
import edu.dixietech.alanmcgraw.cropcanvas.R

data class Product(
    val name: String,
    val amount: Int,
    val worth: Int,
    @DrawableRes val drawableResource: Int,
) {

    companion object {
        val examples by lazy {
            listOf(
                Product(
                    name = "Blueberries",
                    amount = 10,
                    worth = 100,
                    drawableResource = R.drawable.blueberries
                ),
                Product(
                    name = "Carrots",
                    amount = 5,
                    worth = 10,
                    drawableResource = R.drawable.carrots
                ),
                Product(
                    name = "Melon Seeds",
                    amount = 10,
                    worth = 259,
                    drawableResource = R.drawable.melon_seeds
                ),
            )
        }
    }
}