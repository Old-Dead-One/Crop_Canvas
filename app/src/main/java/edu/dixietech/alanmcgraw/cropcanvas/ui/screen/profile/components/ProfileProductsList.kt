package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowAmountLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowCostLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.NoContent

@Composable
fun ProfileProductList(
    products: List<Product>,
    onSellProduct: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (products.isNotEmpty()) {
        LazyColumn(modifier) {
            items(products, key = { product -> product.name }) { product ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.medium_spacing))
                ) {
                    ListRow(
                        image = product.drawableResource,
                        title = product.name,
                        labelOne = { ListRowAmountLabel(amount = product.amount) },
                        labelTwo = { ListRowCostLabel(cost = product.worth) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    Button(
                        onClick = { onSellProduct(product.name, product.amount) },
                        enabled = product.amount > 0
                    ) {
                        Text("Sell All (${product.amount})")
                    }
                }
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