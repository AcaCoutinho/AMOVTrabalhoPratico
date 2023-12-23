package pt.isec.amovtp.touristapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.ui.composables.DropDownComposable
import pt.isec.amovtp.touristapp.ui.composables.ErrorAlertDialog
import pt.isec.amovtp.touristapp.ui.composables.TakePhotoOrLoadFromGallery
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel


@Composable
fun AddPOIScreen(modifier: Modifier.Companion, navController: NavHostController?,locationViewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    val context = LocalContext.current
    val location = locationViewModel.currentLocation.observeAsState()
    val focusManager = LocalFocusManager.current
    val selectedLocation = locationViewModel.selectedLocation
    var selectedCategory = locationViewModel.selectedCategory

    var poiName by remember { mutableStateOf("") }
    var poiDescription by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf((location.value?.longitude ?: 0.0).toString()) }
    var latitude by remember { mutableStateOf((location.value?.latitude ?: 0.0).toString()) }

    var isFormValid by remember { mutableStateOf(false) }
    var isInputEnabled by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    //coordenadas
    val location = locationViewModel.currentLocation.observeAsState()
    val focusManager = LocalFocusManager.current
    var writenCoords by remember { mutableStateOf(false) }

    val userUID = firebaseViewModel.authUser.value!!.uid

    fun validateForm() {
        val longitudeDouble: Double? = longitude.toDoubleOrNull()
        val latitudeDouble: Double? = latitude.toDoubleOrNull()

        isFormValid = poiName.isNotBlank() &&
                poiDescription.isNotBlank() &&
                longitudeDouble != null &&
                latitudeDouble != null &&
                locationViewModel.imagePath.value != null &&
                selectedCategory!!.name != ""
    }

    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        if(isError)
            ErrorAlertDialog {
                isError = false
            }

        OutlinedTextField(
            value = poiName,
            onValueChange ={
                poiName = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            label = { Text(text = "POI Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = poiDescription,
            onValueChange ={
                poiDescription = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            label = { Text(text = "Location Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        DropDownComposable(
            navController = navController,
            viewModel = locationViewModel,
            firebaseViewModel = firebaseViewModel
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 12.dp)
        ) {

            Button(
                onClick = {
                    isInputEnabled = false;
                    latitude = (location.value?.latitude ?: 0.0).toString()
                    longitude = (location.value?.longitude ?: 0.0).toString()
                    writenCoords = false
                    validateForm()

                },
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = CutCornerShape(percent = 0)
                ) {
                Text(text = "Get coordinates from current location")
            }

            Button(
                onClick = {
                    isInputEnabled = true
                    writenCoords = true
                    validateForm()
                },
                modifier = Modifier.padding(4.dp, 0.dp)
            )
            Text(
                text = "Write coordinates",
                modifier = Modifier.padding(8.dp, 0.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = longitude,
                onValueChange = {
                    longitude = it
                    validateForm()
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = { Text(text = "Longitude") },
                enabled = isInputEnabled,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            OutlinedTextField(
                value = latitude,
                onValueChange = {
                    latitude = it
                    validateForm()
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = { Text(text = "Latitude") },
                enabled = isInputEnabled,
                modifier = Modifier.weight(1f)

            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .weight(1f)
        ) {
            TakePhotoOrLoadFromGallery(locationViewModel.imagePath, Modifier.fillMaxSize())
            validateForm()
        }
        Button(
            onClick = {
                if (!isFormValid) {
                    isError = true
                } else {
                    isError = false
                    var POI = PointOfInterest(
                        name = poiName,
                        description = poiDescription,
                        category = selectedCategory ?: Category("","",""),
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
                        photoUrl = "",
                        writenCoords = writenCoords,
                        approvals = 0,
                        userUIDsApprovals = emptyList(),
                        userUID = userUID
                    )
                    Log.i("TAG", "AddPOIScreen: " + POI)

                    firebaseViewModel.addPOIToFirestore(selectedLocation?.name ?: ""  ,POI)
                    firebaseViewModel.uploadPOIToStorage(directory = "images/" + selectedLocation?.name +"/pois/",imageName = poiName, path = locationViewModel.imagePath.value ?: "", locationName = selectedLocation?.name ?: "" )
                    locationViewModel.imagePath.value = null
                    navController?.popBackStack()
                    Toast.makeText(context,"POI adicionada com sucesso!",Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        ) {
            Text(text = "Submit")
        }


    }


}

