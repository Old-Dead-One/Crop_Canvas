package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowAmountLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowCostLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.NoContent
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme

@Composable
fun ProfileProductList(
    products: List<Product>,
    modifier: Modifier = Modifier
) {
    if (products.isNotEmpty()) {
        LazyColumn(modifier) {
            items(products) { product ->
                ListRow(
                    image = product.drawableResource,
                    title = product.name,
                    labelOne = { ListRowAmountLabel(amount = product.amount) },
                    labelTwo = { ListRowCostLabel(cost = product.worth) }
                )
                HorizontalDivider()
            }
        }
    } else {
        NoContent(
            title = "No Products",
            icon = Icons.Default.LocalFlorist,
            modifier = modifier
        )
    }
}

@Composable
fun ListRowLabel(
    icon: ImageVector,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )

        Text(value)
    }
}

@Preview
@Composable
private fun ProfileProductListPrev() {
    CropCanvasTheme {
        Surface(Modifier.fillMaxSize()) {
            ProfileProductList(Product.examples)
        }
    }
}