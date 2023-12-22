package pt.isec.amovtp.touristapp.ui.screens

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import pt.isec.amovtp.touristapp.ui.viewmodels.LocationViewModel
@Composable
fun ShowMapScreen(modifier: Modifier = Modifier, navController: NavHostController?, viewModel : LocationViewModel){
    //var autoEnabled by remember{ mutableStateOf(false) }
    val location = viewModel.currentLocation.observeAsState()

    var geoPoint by remember{ mutableStateOf(GeoPoint(
        location.value?.latitude ?: 0.0, location.value?.longitude ?: 0.0
    )) }


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
                //.padding(8.dp)
                .fillMaxWidth()
                //.fillMaxHeight(0.8f)
                .clipToBounds()
                .background(Color(255, 240, 128)),
        ){
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK);//==TileSourceFactory.DEFAULT_TILE_SOURCE
                        setMultiTouchControls(true)
                        controller.setCenter(geoPoint)
                        controller.setZoom(8.0)

                        overlays.add(
                            Marker(this).apply {
                                position = GeoPoint(
                                    location.value?.latitude ?: 0.0,
                                    location.value?.longitude ?: 0.0
                                )
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Estou aqui!"
                                //icon = ContextCompat.getDrawable(context,android.R.drawable.arrow_down_float)
                                isFlat = true
                            }
                        )

                    }
                },
                update = { view ->
                    view.controller.setCenter(geoPoint)
                }
            )
        }
    }

}