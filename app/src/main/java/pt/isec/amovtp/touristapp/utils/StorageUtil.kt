package pt.isec.amovtp.touristapp.utils

import android.content.ContentValues.TAG
import android.content.res.AssetManager
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import pt.isec.amovtp.touristapp.data.Category
import pt.isec.amovtp.touristapp.data.Comment
import pt.isec.amovtp.touristapp.data.ImagesPOIs
import pt.isec.amovtp.touristapp.data.Location
import pt.isec.amovtp.touristapp.data.PointOfInterest
import pt.isec.amovtp.touristapp.data.User
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


enum class Collections(val collectionName: String){
    Locations("Locations"),
    Users("Users"),
    Comments("Comments"),
    Category("Category"),
    ImagesPOI("Images"), //Imagens de cada POI
    POIs("POIs");

    val route : String
        get() = this.collectionName

}
class StorageUtil {
    companion object {
        //private val db = Firebase.firestore
        /*
         *  Adicionar Novos dados na Firebase
         */

        fun addLocationToFirestore(location: Location,onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val locationData = hashMapOf(
                "Description" to location.description,
                "Latitude" to location.latitude,
                "Longitude" to location.longitude,
                "PhotoUrl" to location.photoUrl,
                "WritenCoords" to location.writenCoords,
                "Approvals" to location.approvals,
                "UserUID" to location.userUID,
                "TotalPois" to location.totalPois,
            )
            db.collection(Collections.Locations.route).
            document(location.name)
                .set(locationData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }
        fun deleteLocationFromFirestore(location: Location, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            db.collection(Collections.Locations.route)
                .document(location.name)
                .delete()
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        fun updateAprovalLocationFirestore(location: Location, userUID: String ,onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val num = location.approvals + 1
            val updatedUserUIDs = location.userUIDsApprovals.toMutableList().apply {
                add(userUID)
            }
            val locationData = hashMapOf(
                "Approvals" to num as Any,
                "UserUIDs" to updatedUserUIDs
            )

            db.collection(Collections.Locations.route)
                .document(location.name)
                .update(locationData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        fun updateApprovalCategoryInFirestore(category: Category,userUID: String ,onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val num = category.approvals + 1
            val updatedUserUIDs = category.userUIDsApprovals.toMutableList().apply {
                add(userUID)
            }
            val locationData = hashMapOf(
                "Approvals" to num as Any,
                "UserUIDs" to updatedUserUIDs
            )

            db.collection(Collections.Category.route)
                .document(category.name)
                .update(locationData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        fun updateAprovalPOIsFirestore(location: Location?, poi: PointOfInterest, userUID: String, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val num = poi.approvals + 1
            val updatedUserUIDs = poi.userUIDsApprovals.toMutableList().apply {
                add(userUID)
            }
            val locationData = hashMapOf(
                "Approvals" to num as Any,
                "UserUIDs" to updatedUserUIDs
            )

            db.collection(Collections.Locations.route)
                .document(location!!.name)
                .collection(Collections.POIs.route)
                .document(poi.name)
                .update(locationData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }


        fun addPOIToFirestore(locationName: String, poi: PointOfInterest,onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val poisData = hashMapOf(
                "Description" to poi.description,
                "Category" to poi.category,
                "Latitude" to poi.latitude,
                "Longitude" to poi.longitude,
                "PhotoUrl" to poi.photoUrl,
                "WritenCoords" to poi.writenCoords,
                "Approvals" to poi.approvals,
                "UserUID" to poi.userUID,
            )

            db.collection(Collections.Locations.route)
                .document(locationName)
                .collection(Collections.POIs.route)
                .document(poi.name)
                .set(poisData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }

            incrementTotalPois(locationName,1)
        }

        fun deletePOIFromFirestore(locationName: String, poi: PointOfInterest, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            db.collection(Collections.Locations.route)
                .document(locationName)
                .collection(Collections.POIs.route)
                .document(poi.name)
                .delete()
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }

            incrementTotalPois(locationName,-1)
        }

        fun incrementTotalPois(locationName: String,value:Long) {
            val db = Firebase.firestore

            // Crie um mapa para a operação de incremento
            val incrementData = hashMapOf(
                "TotalPois" to FieldValue.increment(value)
            )

            db.collection(Collections.Locations.route)
                .document(locationName)
                .update(incrementData as Map<String, Any>)

        }
        fun addCommentToFirestore(comment: Comment,location: Location?, poi: PointOfInterest?,onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val commentData = hashMapOf(
                "Comment" to comment.description,
                "UserName" to comment.userName,
                "UserUID" to comment.userUID,
                "Date" to comment.date,
                "Rating" to comment.rating,
            )

            db.collection(Collections.Locations.route)
                .document(location?.name ?: "")
                .collection(Collections.POIs.route)
                .document(poi?.name?: "")
                .collection(Collections.Comments.route)
                .document("Comment-[${comment.userName}]")
                .set(commentData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        fun addPOIsImagesDataToFirestore(images: ImagesPOIs, location: Location?, poi: PointOfInterest?, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val imageData = hashMapOf(
                "PhotoUrl" to images.photoUrl,
                "UserName" to images.userName,
                "UserUID" to images.userUID,
                "Date" to images.date,
            )

            db.collection(Collections.Locations.route)
                .document(location?.name ?: "")
                .collection(Collections.POIs.route)
                .document(poi?.name?: "")
                .collection(Collections.ImagesPOI.route)
                .document("Image[${images.userName}]")
                .set(imageData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }
        fun addLocationPhotoUrlToFirestore(name: String ,photoUrl: Uri) {
            val db = Firebase.firestore
            val newPhotoUrl = photoUrl.toString()
            val updateData = hashMapOf(
                "PhotoUrl" to newPhotoUrl as Any
            )
            db.collection(Collections.Locations.route).document(name)
                .update(updateData)
        }

        fun addPOIPhotoUrlToFirestore(name: String ,photoUrl: Uri,locationName: String) {
            val db = Firebase.firestore
            val newPhotoUrl = photoUrl.toString()
            val updateData = hashMapOf(
                "PhotoUrl" to newPhotoUrl as Any
            )
            db.collection(Collections.Locations.route)
                .document(locationName)
                .collection(Collections.POIs.route)
                .document(name)
                .update(updateData)
        }
        private fun addPOIUniquePhotoUrlToFirestore(imageName: String, downloadUri: Uri?, locationName: String, poiName: String) {
            val db = Firebase.firestore
            val newPhotoUrl = downloadUri.toString()
            val updateData = hashMapOf(
                "PhotoUrl" to newPhotoUrl as Any
            )
            db.collection(Collections.Locations.route)
                .document(locationName)
                .collection(Collections.POIs.route)
                .document(poiName)
                .collection(Collections.ImagesPOI.route)
                .document(imageName)
                .update(updateData)
        }

        fun addCategoryToFirestore (category: Category, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val categoryData = hashMapOf(
                "Name" to category.name,
                "Description" to category.description,
                "Icon" to category.icon
            )

            db.collection(Collections.Category.route).document(category.name).set(categoryData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }



        fun addUserToFirestore (uid: String, user: User, onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val userData = hashMapOf(
                "Email" to user.email,
                "FirstName" to user.firstName,
                "LastName" to user.lastName,
            )

            db.collection(Collections.Users.route).document(uid).set(userData)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        /*
         *  Ler dados da Firestore
         */


        fun getLocationFromFirestore(callback: (List<Location>) -> Unit) {
            val db = Firebase.firestore

            db.collection(Collections.Locations.route)
                .get()
                .addOnSuccessListener { result ->
                    val locations = mutableListOf<Location>()

                    for (document in result) {
                        Log.i(TAG, "getLocationFromFirestore: " +document.data.toString())
                        val name = document.id
                        val description = document.getString("Description") ?: ""
                        val latitude = document.getDouble("Latitude") ?: 0.0
                        val longitude = document.getDouble("Longitude") ?: 0.0
                        val imageUrl = document.getString("PhotoUrl") ?: ""
                        val writenCoords  = document.getBoolean("WritenCoords") ?: false
                        val approvals  = document.getLong("Approvals")?.toInt() ?: 0
                        val userUIDs = document.get("UserUIDs") as? List<String> ?: emptyList()
                        val userUID = document.getString("UserUID") ?: ""
                        val totalPois = document.getLong("TotalPois")?.toInt() ?: 0
                        val location = Location(name, description, latitude, longitude, imageUrl,writenCoords,approvals,userUIDs,userUID,totalPois)
                        locations.add(location)
                    }
                    callback(locations)
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "ERROR: ${e.toString()}")
                    callback(emptyList())
                }
        }
        fun getPoisFromFirestore(location: Location?, callback: (List<PointOfInterest>) -> Unit) {
            val db = Firebase.firestore
            val idDocument = location?.name ?: ""

            db.collection(Collections.Locations.route)
                .document(idDocument)
                .collection(Collections.POIs.route)
                .get()
                .addOnSuccessListener { result ->
                    val pois = mutableListOf<PointOfInterest>()

                    for (document in result) {
                        try {
                            val name = document.id
                            val description = document.getString("Description") ?: ""
                            val latitude = document.getDouble("Latitude") ?: 0.0
                            val longitude = document.getDouble("Longitude") ?: 0.0
                            val imageUrl = document.getString("PhotoUrl") ?: ""
                            val writenCoords  = document.getBoolean("WritenCoords") ?: false
                            val approvals  = document.getLong("Approvals")?.toInt() ?: 0
                            val userUIDs = document.get("UserUIDs") as? List<String> ?: emptyList()
                            val userUID = document.getString("UserUID") ?: ""

                            val categoryData = document.get("Category") as Map<*, *>
                            val catName = categoryData["name"].toString()
                            val catIcon = categoryData["icon"].toString()
                            val catDesc = categoryData["description"].toString()

                            Log.i(TAG, "getPoisFromFirestore: " + categoryData.toString())
                            Log.i(TAG, "getPoisFromFirestore DESCRICAO: " +catDesc)

                            val pointOfInterest = PointOfInterest(name, description, latitude, longitude, imageUrl,Category(catName,catIcon,catDesc,-1,
                                emptyList(),""),writenCoords,approvals,userUIDs,userUID)
                            pois.add(pointOfInterest)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing POI document: ${e.message}")
                        }
                    }

                    callback(pois)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching POIs: ${e.message}")
                    callback(emptyList())
                }
        }
        fun getCategoryFromFirestore(callback: (List<Category>) -> Unit) {
            val db = Firebase.firestore

            db.collection(Collections.Category.route)
                .get()
                .addOnSuccessListener { result ->
                    val categories = mutableListOf<Category>()

                    for (document in result) {
                        val name = document.id
                        val description = document.getString("Description") ?: ""
                        val icon = document.getString("Icon") ?: ""
                        val approvals  = document.getLong("Approvals")?.toInt() ?: 0
                        val userUIDs = document.get("UserUIDs") as? List<String> ?: emptyList()
                        val userUID = document.getString("UserUID") ?: ""

                        val category = Category(name, description,icon,approvals,userUIDs,userUID)
                        categories.add(category)
                    }
                    callback(categories)
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "ERROR: ${e.toString()}")
                    callback(emptyList())
                }
        }

        fun getPOIUniquePhotoFromFirestore(location: Location?, poi: PointOfInterest?,callback: (List<ImagesPOIs>) -> Unit) {
            val db = Firebase.firestore
            val idDocumentLocation = location?.name ?: ""
            val idDocumentPoi = poi?.name ?: ""

            db.collection(Collections.Locations.route)
                .document(idDocumentLocation)
                .collection(Collections.POIs.route)
                .document(idDocumentPoi)
                .collection(Collections.ImagesPOI.route)
                .get()
                .addOnSuccessListener { result ->
                    val images = mutableListOf<ImagesPOIs>()

                    for (document in result) {
                        try {
                            //val name = document.id
                            val photoUrl = document.getString("PhotoUrl") ?: ""
                            val userName = document.getString("UserName") ?: ""
                            val userUID = document.getString("UserUID") ?: ""
                            val date = document.getString("Date") ?: ""

                            val image = ImagesPOIs(photoUrl,userName,userUID,date)
                            images.add(image)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing POI document: ${e.message}")
                        }
                    }
                    callback(images)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching POIs: ${e.message}")
                    callback(emptyList())
                }
        }

        fun getCommentFromFirestore(location: Location?, poi: PointOfInterest?, callback: (List<Comment>) -> Unit) {
            val db = Firebase.firestore
            val idDocumentLocation = location?.name ?: ""
            val idDocumentPoi = poi?.name ?: ""

            db.collection(Collections.Locations.route)
                .document(idDocumentLocation)
                .collection(Collections.POIs.route)
                .document(idDocumentPoi)
                .collection(Collections.Comments.route)
                .get()
                .addOnSuccessListener { result ->
                    val comments = mutableListOf<Comment>()

                    for (document in result) {
                        try {
                            //val name = document.id
                            val commentString = document.getString("Comment") ?: ""
                            val userName = document.getString("UserName") ?: ""
                            val userUID = document.getString("UserUID") ?: ""
                            val date = document.getString("Date") ?: ""
                            val rating = document.getLong("Rating")?.toInt() ?: 0

                            val comment = Comment(commentString,userName,userUID,date,rating)
                            comments.add(comment)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing POI document: ${e.message}")
                        }
                    }
                    callback(comments)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching POIs: ${e.message}")
                    callback(emptyList())
                }

        }


        fun getUserFromFirestore (userUID: String, userData: (User) -> Unit){
            val db = Firebase.firestore

            db.collection(Collections.Users.route).document(userUID).get()
                .addOnSuccessListener { doc ->
                    if(doc.exists()) {
                        val user = User(doc.get("Email").toString(), doc.get("FirstName").toString(), doc.get("LastName").toString()) //doc.toObject<User>()!!
                        userData(user)
                    }
                }
        }

        //Storage

        fun getFileFromAsset(assetManager: AssetManager, strName: String): InputStream? {
            var istr: InputStream? = null
            try {
                istr = assetManager.open(strName)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return istr
        }

        fun getFileFromPath(filePath: String): InputStream? {
            return try {
                FileInputStream(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
        fun uploadLocationFile(directory: String,inputStream: InputStream, imgFile: String) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child(directory)
            val ref3 = ref2.child(imgFile)

            val uploadTask = ref3.putStream(inputStream)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref3.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    println(downloadUri.toString())
                    //add Uti to database
                    addLocationPhotoUrlToFirestore(imgFile,downloadUri)


                    //   https://firebasestorage.googleapis.com/v0/b/p0405ansamov.appspot.com/o/images%2Fimage.png?alt=media&token=302c7119-c3a9-426d-b7b4-6ab5ac25fed9
                } else {
                    // Handle failures
                    // ...
                }
            }


        }

        fun uploadPOIFile(directory: String,inputStream: InputStream, imgFile: String,locationName: String) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child(directory)
            val ref3 = ref2.child(imgFile)

            val uploadTask = ref3.putStream(inputStream)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref3.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    println(downloadUri.toString())
                    //add Uti to database
                    addPOIPhotoUrlToFirestore(imgFile,downloadUri,locationName)
                }
            }
        }

        fun uploadPOIUniquePictureFile(directory:String, inputStream: InputStream, imageName: String, locationName: String, poiName: String) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child(directory)
            val ref3 = ref2.child(imageName)

            val uploadTask = ref3.putStream(inputStream)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref3.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    println(downloadUri.toString())
                    addPOIUniquePhotoUrlToFirestore(imageName,downloadUri,locationName,poiName)
                }
            }
        }




    }
}