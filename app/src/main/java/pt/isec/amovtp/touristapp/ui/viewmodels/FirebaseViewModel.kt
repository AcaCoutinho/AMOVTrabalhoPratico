package pt.isec.amovtp.touristapp.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import pt.isec.amovtp.touristapp.data.AuthUser
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.data.ImagesPOIs
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.data.User
import pt.isec.amovtp.touristapp.utils.FireAuthUtil
import pt.isec.amovtp.touristapp.utils.StorageUtil

fun FirebaseUser.toAuthUser() : AuthUser {
    val userUID = this.uid
    val userDisplayName = this.displayName ?: ""
    val userEmail = this.email ?: ""

    return AuthUser(userUID, userDisplayName, userEmail)
}

class FirebaseViewModel : ViewModel() {
    private val _error = mutableStateOf<String?>(null)
    val error : MutableState<String?>
        get() = _error

    private val _authUser = mutableStateOf(FireAuthUtil.currentUser?.toAuthUser())
    val authUser : MutableState<AuthUser?>
        get() = _authUser

    private val _user = mutableStateOf<User?>(null)
    val user : MutableState<User?>
        get() = _user

    fun createUserWithEmail(user: User, password: String, callback: () -> Unit) {
        if(user.email.isBlank() || password.isBlank())
            return
        viewModelScope.launch {
            FireAuthUtil.createUserWithEmail(user.email, password) { exception ->
                if(exception == null) {
                    _authUser.value = FireAuthUtil.currentUser?.toAuthUser()
                    StorageUtil.addUserToFirestore(_authUser.value!!.uid ,user) { e ->
                        _error.value = e?.message
                    }
                }
                _error.value = exception?.message
            }
            FireAuthUtil.signOut()
            callback()
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if(email.isBlank() || password.isBlank())
            return
        //viewModelScope.launch {
            FireAuthUtil.signInWithEmail(email, password) { exception ->
                if(exception == null) {
                    _authUser.value = FireAuthUtil.currentUser?.toAuthUser()
                    StorageUtil.getUserFromFirestore(_authUser.value!!.uid) { user ->
                        _user.value = user
                    }

                }
                _error.value = exception?.message
            }
        //}
    }

    fun signOut () {
        FireAuthUtil.signOut()
        _error.value = null
        _authUser.value = null
        _user.value = null
    }

    fun getUserFromFirestore(userUID: String) {
        viewModelScope.launch {
            StorageUtil.getUserFromFirestore(userUID){ user ->
                _user.value = user
            }
        }
    }

    fun getLocationFromFirestore(callback: (List<Location>) -> Unit){
        viewModelScope.launch {
            StorageUtil.getLocationFromFirestore { locations ->
                callback(locations)
            }
        }
    }
    fun addLocationsToFirestore(location:Location) {
        viewModelScope.launch {
            StorageUtil.addLocationToFirestore(location){ exception ->
                _error.value = exception?.message
            }
        }
    }
    fun deleteLocationsFromFirestore(location: Location) {
        viewModelScope.launch {
            StorageUtil.deleteLocationFromFirestore(location){ exception ->
                _error.value = exception?.message
            }
        }
    }
    fun updateAprovalLocationInFirestore(location: Location, userUID: String,) {
        viewModelScope.launch {
            StorageUtil.updateAprovalLocationFirestore(location,userUID){ exception ->
                _error.value = exception?.message
            }
        }
    }
    fun updateAprovalPOIsInFirestore(location: Location?, poi: PointOfInterest, userUID: String) {
        viewModelScope.launch {
            StorageUtil.updateAprovalPOIsFirestore(location,poi,userUID){ exception ->
                _error.value = exception?.message
            }
        }
    }
    fun addPOIToFirestore(locationName:String, poi: PointOfInterest) {
        viewModelScope.launch {
            StorageUtil.addPOIToFirestore(locationName,poi){ exception ->
                _error.value = exception?.message
            }
        }
    }
    fun addCommentToFirestore(comment: Comment,selectedLocation: Location?, selectedPoi: PointOfInterest?) {
        viewModelScope.launch {
            StorageUtil.addCommentToFirestore(comment,selectedLocation,selectedPoi){ exception ->
                _error.value = exception?.message
            }
        }
    }
    fun addPOIsImagesDataToFirestore(images: ImagesPOIs,selectedLocation: Location?, selectedPoi: PointOfInterest?) {
        viewModelScope.launch {
            StorageUtil.addPOIsImagesDataToFirestore(images,selectedLocation,selectedPoi){ exception ->
                _error.value = exception?.message
            }
        }
    }
    fun uploadLocationToStorage(directory: String,imageName: String, path: String) {
        viewModelScope.launch {
            StorageUtil.getFileFromPath(path)?.let { inputStream ->
                StorageUtil.uploadLocationFile(directory,inputStream, imageName)
            }
        }
    }

    fun uploadPOIToStorage(directory: String,imageName: String, path: String,locationName: String) {
        viewModelScope.launch {
            StorageUtil.getFileFromPath(path)?.let { inputStream ->
                StorageUtil.uploadPOIFile(directory,inputStream, imageName, locationName )
            }
        }
    }
    fun uploadPOIUniquePictureToStorage(directory: String,imageName: String, path: String,locationName: String,poi: String) {
        viewModelScope.launch {
            StorageUtil.getFileFromPath(path)?.let { inputStream ->
                StorageUtil.uploadPOIUniquePictureFile(directory,inputStream, imageName, locationName,poi )
            }
        }
    }

    fun getPoisFromFirestore(selectedLocation: Location?, callback: (List<PointOfInterest>) -> Unit) {
        viewModelScope.launch {
            StorageUtil.getPoisFromFirestore(selectedLocation) { pois ->
                callback(pois)
            }
        }
    }

    fun addCategoryToFirestore(category: Category, callback: () -> Unit) {
        viewModelScope.launch {
            StorageUtil.addCategoryToFirestore (category) { exception ->
                _error.value = exception?.message
            }
        }
        callback()
    }

    fun getCategoriesFromFirestore(callback: (List<Category>) -> Unit) {
        viewModelScope.launch {
            StorageUtil.getCategoryFromFirestore () { categories ->
                callback(categories)
            }
        }
    }

    fun getCommentsFromFirestore(selectedLocation: Location?, selectedPoi: PointOfInterest?, callback: (List<Comment>) -> Unit) {
        viewModelScope.launch {
            StorageUtil.getCommentFromFirestore (selectedLocation,selectedPoi) { comments ->
                callback(comments)
            }
        }
    }

    fun getPOIUniquePhotoFromFirestore(selectedLocation: Location?, selectedPoi: PointOfInterest?,callback: (List<ImagesPOIs>) -> Unit) {
        viewModelScope.launch {
            StorageUtil.getPOIUniquePhotoFromFirestore (selectedLocation,selectedPoi) { img ->
                callback(img)
            }
        }
    }




}