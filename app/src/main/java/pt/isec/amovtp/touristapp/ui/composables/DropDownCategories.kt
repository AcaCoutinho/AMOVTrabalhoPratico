package pt.isec.amovtp.touristapp.ui.composables

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.ui.screens.Screens
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

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
                    }
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