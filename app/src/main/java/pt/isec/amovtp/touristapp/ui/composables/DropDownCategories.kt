package pt.isec.amovtp.touristapp.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.ui.screens.Screens
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

fun getImageVectorFromName (iconName: String) : ImageVector? {
    return when (iconName) {
        "Filled.Call" -> Icons.Default.Call
        "Filled.Face" -> Icons.Default.Face
        "Filled.Favorite" -> Icons.Default.Favorite
        "Filled.Delete" -> Icons.Default.Delete
        "Filled.ShoppingCart" -> Icons.Default.ShoppingCart
        "Filled.Home" -> Icons.Default.Home
        "Filled.Build" -> Icons.Default.Build
        "Filled.Warning" -> Icons.Default.Warning
        "Filled.LocationOn" -> Icons.Default.LocationOn
        "Filled.Create" -> Icons.Default.Create
        "Filled.Email" -> Icons.Default.Email
        "Filled.Star" -> Icons.Default.Star
        "Filled.Lock" -> Icons.Default.Lock
        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownComposable(modifier: Modifier = Modifier, navController: NavHostController?, viewModel : LocationViewModel, firebaseViewModel: FirebaseViewModel){
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var categories by remember {
        mutableStateOf<List<Category>>(emptyList())
    }

    //sempre que é iniciado, carrega as categorias
    LaunchedEffect(Unit) {
        firebaseViewModel.getCategoriesFromFirestore(){ loadedCategories ->
            categories = loadedCategories
        }
    }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = {isExpanded = it}
    ) {
        TextField(
            value = viewModel.selectedCategory?.name ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor(),
            placeholder = { Text(text = "Select a category...") }
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {isExpanded=false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "Tudo") },
                onClick = {
                    viewModel.selectedCategory = Category("All","","")
                    isExpanded=false
                }
            )
            for( c in categories)
                DropdownMenuItem(
                    text = { Text(text = c.name) },
                    onClick = {
                        viewModel.selectedCategory = c
                        isExpanded=false
                    },
                    leadingIcon = { Icon(getImageVectorFromName(c.icon)!!, contentDescription = c.name) }
                )
            DropdownMenuItem(
                text = { Text(text = "Add new category") },
                onClick = {
                    isExpanded=false
                    navController?.navigate(Screens.ADD_CATEGORY.route)
                }
            )
        }

    }
}