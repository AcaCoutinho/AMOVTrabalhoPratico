package pt.isec.amovtp.touristapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(modifier: Modifier, navController: NavHostController?, firebaseViewModel: FirebaseViewModel) {
    val icons = listOf(
        Icons.Default.Call,
        Icons.Default.Face,
        Icons.Default.Favorite,
        Icons.Default.Delete,
        Icons.Default.ShoppingCart,
        Icons.Default.Home,
        Icons.Default.Build,
        Icons.Default.Warning,
        Icons.Default.LocationOn,
        Icons.Default.Create,
        Icons.Default.Email,
        Icons.Default.Star,
        Icons.Default.Lock
    )

    var expanded by remember { mutableStateOf(false) }

    var selectedIcon by remember { mutableStateOf<ImageVector?>(null) }
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val userUID = firebaseViewModel.authUser.value!!.uid
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        Row {
            OutlinedTextField(
                label = { Text(text = "Name") },
                value = categoryName,
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                onValueChange = {
                    categoryName = it
                }
            )

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.Center),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = {
                        expanded = true
                    }
                ) {
                    if(selectedIcon == null)
                        Icon(Icons.Default.List, contentDescription = "List")
                    else
                        Icon(selectedIcon!!, contentDescription = selectedIcon!!.name)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    scrollState = scrollState
                ) {
                    for (icon in icons) {
                        DropdownMenuItem(
                            text = {
                                Text(icon.name)
                            },
                            leadingIcon = { Icon(imageVector = icon, contentDescription = icon.name) },
                            onClick = {
                                expanded = false
                                selectedIcon = icon
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            label = { Text(text = "Description") },
            value = categoryDescription,
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            onValueChange = {
                categoryDescription = it
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val category = Category(categoryName, categoryDescription, selectedIcon!!.name,0,
                    emptyList(),userUID)
                firebaseViewModel.addCategoryToFirestore(category) {
                    navController?.popBackStack()
                }
            }
        ) {
            Text(text = "Submit")
        }
    }
}
