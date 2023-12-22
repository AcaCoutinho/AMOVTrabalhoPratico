package pt.isec.amovtp.touristapp.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.ui.composables.RatingBar
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel
import java.time.LocalDateTime
import kotlin.math.log

@Composable
fun AddCommentsScreen(modfier: Modifier.Companion, locationViewModel: LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    val selectedLocation = locationViewModel.selectedLocation
    val selectedPoi = locationViewModel.selectedPoi
    var commentToSubmit by remember { mutableStateOf("") }
    var isInputEnabled by remember { mutableStateOf(true) }
    var charLimitExceeded by remember { mutableStateOf(false) }
    var alreadyComment by remember { mutableStateOf(false) }
    var alreadyRated by remember { mutableStateOf(false) }

    var myRating by remember { mutableIntStateOf(0) }
    var rating by remember { mutableIntStateOf(0) }
    var isRatingEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val userName = firebaseViewModel.user.value?.firstName + " " + firebaseViewModel.user.value?.lastName
    val userUID = firebaseViewModel.authUser.value!!.uid
    val date = getDate()

    var comments by remember {
        mutableStateOf<List<Comment?>>(emptyList())
    }

    LaunchedEffect(Unit) {
        firebaseViewModel.getCommentsFromFirestore(selectedLocation,selectedPoi) { loadedComments ->
            comments = loadedComments
            for (c in comments) {
                if (c?.userUID == firebaseViewModel.authUser.value?.uid && c?.description != "") {
                    alreadyComment = true
                }
                if(c?.userUID == firebaseViewModel.authUser.value?.uid && c?.rating != 0){
                    alreadyRated = true
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Comentários [${selectedPoi?.name}]", fontSize = 24.sp)

        Spacer(Modifier.height(16.dp))
        if(charLimitExceeded)
            Text(text = "You can only use 200 caracteres", color = Color.Red)

        if(alreadyComment)
            Text(text = "You already commented", color = Color.Green, fontSize = 16.sp)

        if(!alreadyComment)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(8.dp)
                    .weight(0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp) // Adicionando algum espaçamento horizontal
                ) {
                    OutlinedTextField(
                        value = commentToSubmit,
                        onValueChange = {
                            commentToSubmit = it
                            //validateForm()
                        },
                        singleLine = false,
                        maxLines = 5,
                        label = { Text(text = "Comment") },
                        enabled = isInputEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if(commentToSubmit.length >= 200)
                                charLimitExceeded = true
                            else {
                                charLimitExceeded = false
                                isInputEnabled = false
                                for (c in comments) {
                                    if (c?.userUID == firebaseViewModel.authUser.value?.uid) {
                                        rating = c!!.rating
                                    }
                                }
                                firebaseViewModel.addCommentToFirestore(Comment(commentToSubmit,userName, userUID,date,rating),selectedLocation,selectedPoi)
                                //atualizar a lista
                                firebaseViewModel.getCommentsFromFirestore(selectedLocation, selectedPoi) { loadedComments ->
                                    comments = loadedComments
                                }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),

                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        shape = CutCornerShape(percent = 0)
                    ) {
                        Text(text = "Submit Comment")
                    }
                }
            }
        if(alreadyRated)
            Text(text = "You already rated", color = Color.Green, fontSize = 16.sp)
        if(!alreadyRated)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(8.dp)
                    .weight(0.5f)
            ){
                Spacer(Modifier.height(16.dp))
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    RatingBar(
                        currentRating = myRating,
                        onRatingChanged = {
                            if (isRatingEnabled) {
                                myRating = it
                            }
                        },
                        starsColor = Color.Yellow,
                        size = 48.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            isRatingEnabled = false
                            rating = myRating
                            for (c in comments) {
                                if (c?.userUID == firebaseViewModel.authUser.value?.uid) {
                                    commentToSubmit = c!!.description
                                }
                            }
                            firebaseViewModel.addCommentToFirestore(Comment(commentToSubmit,userName, userUID,date,rating),selectedLocation,selectedPoi)
                            //atualizar a lista
                            firebaseViewModel.getCommentsFromFirestore(selectedLocation, selectedPoi) { loadedComments ->
                                comments = loadedComments
                            }
                            Toast.makeText(context, "Classificação submetida com sucesso!", Toast.LENGTH_LONG).show()
                        },
                        enabled = isRatingEnabled
                    ) {
                        Text(text = "Submeter Classificação")
                    }

                }
            }

        if(comments.size == 0){
            Text(text = "Ainda não há comentários...", fontSize = 24.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)

        ) {
            items(comments) { comment->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(128, 192, 255)
                    ),
                    onClick = {}
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = comment!!.userName,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            if(comment.rating != 0)
                                Text(
                                    text = comment.rating.toString()+ "*", //+ Icons.Filled.Star,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(0.5f)
                                )
                            Text(
                                text = comment.date,
                                fontSize = 14.sp,
                                modifier = Modifier

                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        if(comment?.description != "")
                            Text(text = comment!!.description, fontSize = 20.sp)
                    }
                }

            }
        }
    }
}

fun getDate(): String {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yy")
        LocalDateTime.now().format(formatter)
    } else {
        ""
    }
}