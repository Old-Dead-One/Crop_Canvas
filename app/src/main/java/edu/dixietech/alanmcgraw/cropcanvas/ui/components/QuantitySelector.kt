package edu.dixietech.alanmcgraw.cropcanvas.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    numbers: List<Int> = listOf(1, 5, 10, 15, 25, 50)
) {
    var isShowQuantityMenu by remember { mutableStateOf(false) }

    OutlinedButton(
        shape = MaterialTheme.shapes.small,
        onClick = { isShowQuantityMenu = true },
        modifier = modifier
    ) {
        Text(
            text = "Quantity: $quantity",
            modifier = Modifier
                .weight(1f)
        )

        Box {
            Icon(
                imageVector =
                    if (isShowQuantityMenu) Icons.Default.ArrowDropDown
                    else Icons.Default.ArrowDropUp,
                contentDescription = null
            )

            DropdownMenu(
                expanded = isShowQuantityMenu,
                onDismissRequest = { isShowQuantityMenu = false }
            ) {
                for (number in numbers) {
                    DropdownMenuItem(
                        text = { Text(number.toString()) },
                        onClick = {
                            onQuantityChange(number)
                            isShowQuantityMenu = false
                        }
                    )
                }
            }
        }
    }
}