package edu.dixietech.alanmcgraw.cropcanvas.data.domain

data class Seed(
    val name: String,
    val amount: Int?,
    val price: Int?,
    val growthDuration: Int,
) {
    companion object {
        val examples by lazy {
            listOf(
                Seed(
                    growthDuration = 1100,
                    name = "Melon Seeds",
                    amount = 10,
                    price = 10,
                ),
                Seed(
                    growthDuration = 1000,
                    name = "Blueberry Seeds",
                    amount = 6,
                    price = 100,
                ),
            )
        }
    }
}