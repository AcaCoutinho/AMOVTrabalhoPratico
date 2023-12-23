package pt.isec.amovtp.touristapp.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.ImagesPOIs
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.ui.composables.TakePhotoOrLoadFromGallery
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddPOIPicturesScreen(modfier: Modifier.Companion, locationViewModel: LocationViewModel, firebaseViewModel: FirebaseViewModel){
    val selectedLocation = locationViewModel.selectedLocation
    val selectedPoi = locationViewModel.selectedPoi
    val context = LocalContext.current
    var alreadyPosted by remember { mutableStateOf(false) }

    var images by remember {
        mutableStateOf<List<ImagesPOIs>>(emptyList())
    }


    //sempre que é iniciado, carrega os POIS
    LaunchedEffect(Unit) {
        firebaseViewModel.getPOIUniquePhotoFromFirestore(selectedLocation,selectedPoi){ loadedImages ->
            images = loadedImages
            for (i in images) {
                if (i?.userUID == firebaseViewModel.authUser.value?.uid) {
                    alreadyPosted = true
                    break
                }
            }
        }
    }

    Column {
        if(alreadyPosted)
            Text(
                text = "You already posted",
                color = Color.Green, fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        else
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(8.dp)
                    .weight(0.7f)
            ) {
                TakePhotoOrLoadFromGallery(locationViewModel.imagePath, Modifier.fillMaxSize())
                Button(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClick = {
                        val userName = firebaseViewModel.user.value?.firstName + " " + firebaseViewModel.user.value?.lastName
                        val userUID = firebaseViewModel.authUser.value!!.uid
                        val date = getDate()
                        firebaseViewModel.addPOIsImagesDataToFirestore(ImagesPOIs("",userName,userUID,date),selectedLocation,selectedPoi)
                        firebaseViewModel.uploadPOIUniquePictureToStorage(directory = "images/" + selectedLocation?.name +"/pois/images/",imageName = "Image[$userName]", path = locationViewModel.imagePath.value ?: "", locationName = selectedLocation?.name ?:"",poi = selectedPoi?.name ?: "" )
                        locationViewModel.imagePath.value = null
                        Toast.makeText(context,"Foto adicionada com sucesso!", Toast.LENGTH_LONG).show()
                        alreadyPosted = true
                        firebaseViewModel.getPOIUniquePhotoFromFirestore(selectedLocation,selectedPoi){ loadedImages ->
                            images = loadedImages
                        }
                    }
                ) {
                    Text(text = "Submit Picture")
                }
            }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(images) { image ->
                Card(
                    modifier = Modifier
                        //.fillMaxHeight(0,5f) // Use 50% of the screen height
                        //.height(324.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),

                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            //.padding(8.dp)
                            .wrapContentHeight(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        //Image(painter = painterResource(u = ), contentDescription = "city picture")
                        AsyncImage(model = image.photoUrl, contentDescription = "POI Picture")
                        Text(text = image.userName, fontSize = 20.sp)
                        Text(text = image.date, fontSize = 20.sp)

                    }

                }
            }
        }
    }

}
