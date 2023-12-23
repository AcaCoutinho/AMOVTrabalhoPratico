package pt.isec.amovtp.touristapp.ui.viewmodels

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isec.amovtp.touristapp.R
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.utils.location.LocationHandler
import pt.isec.amovtp.touristapp.data.Location as LocationData

class LocationViewModelFactory (
    private val locationHandler: LocationHandler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LocationViewModel(locationHandler) as T
    }
}


class LocationViewModel(private val locationHandler: LocationHandler) : ViewModel() {
    val imagePath : MutableState<String?> = mutableStateOf(null)
    var selectedLocation by mutableStateOf<LocationData?>(null)
    var selectedPoi by mutableStateOf<PointOfInterest?>(null)
    var selectedCategory by mutableStateOf<Category?>(Category("","","",0, emptyList(),""))

    // Permissions
    var coarseLocationPermission = false
    var fineLocationPermission = false
    var backgroundLocationPermission = false

    private val _currentLocation = MutableLiveData(Location(null))
    val currentLocation : LiveData<Location>
        get() = _currentLocation


    private val locationEnabled : Boolean
        get() = locationHandler.locationEnabled

    init {
        locationHandler.onLocation = {
            _currentLocation.value = it
        }
    }

    fun startLocationUpdates() {
        if (fineLocationPermission && coarseLocationPermission) {
            locationHandler.startLocationUpdates()
        }

    }

    fun stopLocationUpdates() {
        locationHandler.stopLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}
