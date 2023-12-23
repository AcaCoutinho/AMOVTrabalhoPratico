package pt.isec.amovtp.touristapp.ui.screens

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.ui.composables.RatingBar
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

@Composable
fun ListCategoryScreen(modifier: Modifier = Modifier, viewModel: LocationViewModel,firebaseViewModel: FirebaseViewModel) {
    var myRating by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var isRatingEnabled by remember { mutableStateOf(true) }
    val currentPoi = viewModel.selectedPoi
    val userUID = firebaseViewModel.authUser.value!!.uid


    var categories by remember {
        mutableStateOf<List<Category>>(emptyList())
    }

    //sempre que é iniciado, carrega as categorias
    LaunchedEffect(Unit) {
        firebaseViewModel.getCategoriesFromFirestore(){ loadedCategories ->
            categories = loadedCategories
            for (c in categories) {
                if (userUID in c.userUIDsApprovals || userUID == c.userUID) {
                    c.enableBtn = false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(categories) { category ->
                Card(
                    modifier = Modifier
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = category.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = category.description,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        //if(location.approvals < 2) {
                        //if(category.userUID == userUID) {
                        //}


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                IconButton(
                                    onClick = {
                                        firebaseViewModel.updateAprovalCategoryInFirestore(
                                            category,
                                            userUID
                                        )
                                        firebaseViewModel.getCategoriesFromFirestore(){ loadedCategories ->
                                            categories = loadedCategories
                                            for (c in categories) {
                                                if (userUID in c.userUIDsApprovals || userUID == c.userUID) {
                                                    c.enableBtn = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null
                                    )
                                }
                                Text("${category.approvals}/2") // Uncomment this line if needed
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(
                                    onClick = {
                                        // Handle click for the Edit IconButton
                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        // Handle click for the Delete IconButton
                                        Toast.makeText(context, "s", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.padding(8.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteForever,
                                        contentDescription = null
                                    )
                                }
                            }
                        }


                    }

                }
            }
        }

    }
}