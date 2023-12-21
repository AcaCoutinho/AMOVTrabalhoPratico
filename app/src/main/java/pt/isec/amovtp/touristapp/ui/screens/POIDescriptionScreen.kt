package pt.isec.amovtp.touristapp.ui.screens

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.ui.viewmodels.FirebaseViewModel
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel

@Composable
fun POIDescriptionScreen(modifier: Modifier = Modifier, viewModel: LocationViewModel,firebaseViewModel: FirebaseViewModel) {

    val currentPoi = viewModel.selectedPoi

    val currentGeoPoint by remember{ mutableStateOf(GeoPoint(
       currentPoi?.latitude ?: 0.0, currentPoi?.longitude ?: 0.0
    )) }
    var pois by remember { mutableStateOf<List<PointOfInterest>>(emptyList()) }
    val selectedLocation = viewModel.selectedLocation

    var loaded by remember { mutableStateOf(false)    }
    //sempre que é iniciado, carrega os POIS
    LaunchedEffect(Unit) {
        firebaseViewModel.getPoisFromFirestore(selectedLocation) { loadedPois ->
            pois = loadedPois
            loaded = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(16.dp))
        Box (
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .clipToBounds()
        ){
            if(loaded)
                AndroidView(
                    factory = { context ->
                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK);//==TileSourceFactory.DEFAULT_TILE_SOURCE
                            setMultiTouchControls(true)
                            controller.setCenter(currentGeoPoint)
                            controller.setZoom(13.0)
                            Log.i("POIS_FOR", "POIDescriptionScreen: " + pois)
                                for(poi in pois) {
                                    val marker = Marker(this).apply {
                                        position = GeoPoint(poi.latitude, poi.longitude)
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        title = poi.name
                                        subDescription = poi.description
                                        //icon = ShapeDrawable(OvalShape())
                                    }
                                    if(! poi.name.equals(currentPoi?.name))
                                        marker.icon = ShapeDrawable(OvalShape()).apply {
                                            intrinsicHeight = 40 // Altura do círculo em pixels
                                            intrinsicWidth = 40 // Largura do círculo em pixels
                                            paint.color = Color.Red.toArgb()
                                        }
                                    overlays.add(marker)
                                }

                        }
                    },
                    update = { view ->
                        view.controller.setCenter(currentGeoPoint)
                    }
                )
        }
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Ecrã de adicionar classificação */ },
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            shape = CutCornerShape(percent = 0)
        ) {
            Text(text = "Add Rating", maxLines = 2)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(pois) { poi->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(128,192,255)
                    ),
                    onClick = {
                        //geoPoint = GeoPoint(it.latitude,it.longitude)
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Comment", fontSize = 20.sp)
                        //Text(text = "${it.latitude} ${it.longitude}", fontSize = 14.sp)
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(pois) { poi->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(128,192,255)
                    ),
                    onClick = {
                        //geoPoint = GeoPoint(it.latitude,it.longitude)
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Photo", fontSize = 20.sp)
                        //Text(text = "${it.latitude} ${it.longitude}", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}