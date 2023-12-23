package pt.isec.amovtp.touristapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.ui.composables.ErrorAlertDialog
import pt.isec.amovtp.touristapp.ui.composables.TakePhotoOrLoadFromGallery
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel


@Composable
fun AddLocationScreen(modifier: Modifier.Companion, navController: NavHostController?,locationViewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel) {
    val context = LocalContext.current
    val location = locationViewModel.currentLocation.observeAsState()
    val focusManager = LocalFocusManager.current

    var locationName by remember { mutableStateOf("") }
    var locationDescription by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf((location.value?.longitude ?: 0.0).toString()) }
    var latitude by remember { mutableStateOf((location.value?.latitude ?: 0.0).toString()) }
    var isFormValid by remember { mutableStateOf(false) }
    var isInputEnabled by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var writenCoords by remember { mutableStateOf(false) }
    val userUID = firebaseViewModel.authUser.value!!.uid

    fun validateForm() {
        val longitudeDouble: Double? = longitude.toDoubleOrNull()
        val latitudeDouble: Double? = latitude.toDoubleOrNull()

        isFormValid = locationName.isNotBlank() &&
                locationDescription.isNotBlank() &&
                longitudeDouble != null &&
                latitudeDouble != null &&
                locationViewModel.imagePath.value != null

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
            value = locationName,
            onValueChange ={
                locationName = it
                validateForm()
            },
            singleLine = true,
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Next)
            },
            label = { Text(text = "Location Name") },
            modifier = Modifier.fillMaxWidth()

        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = locationDescription,
            onValueChange ={
                locationDescription = it
                validateForm()
            },
            maxLines = 2,
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            label = { Text(text = "Location Description") },
            modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.padding(4.dp, 0.dp)
            )
            Text(
                text = "Write coordinates",
                modifier = Modifier.padding(8.dp, 0.dp)
            )
        }

            Button(
                onClick = {
                    isInputEnabled = true
                    writenCoords = true
                    validateForm()
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = { Text(text = "Longitude") },
                enabled = isInputEnabled,
                modifier = Modifier.weight(1f, false)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = latitude,
                onValueChange ={
                    latitude = it
                    validateForm()
                },
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                label = { Text(text = "Latitude") },
                enabled = isInputEnabled,
                modifier = Modifier.weight(1f, false)
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
                    var location = Location(
                        name = locationName,
                        description = locationDescription,
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
                        photoUrl = "",
                        writenCoords = writenCoords,
                        approvals = 0,
                        userUIDsApprovals =  emptyList(),
                        userUID = userUID,
                        totalPois = 0
                    )
                    firebaseViewModel.addLocationsToFirestore(location)
                    firebaseViewModel.uploadLocationToStorage(directory = "images/"+locationName ,imageName = locationName, path = locationViewModel.imagePath.value ?: "")
                    locationViewModel.imagePath.value = null
                    navController?.popBackStack()
                    Toast.makeText(context,"Localização adicionada com sucesso!",Toast.LENGTH_LONG).show()
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

