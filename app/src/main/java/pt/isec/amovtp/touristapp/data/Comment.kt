package pt.isec.amovtp.touristapp.data

data class Comment (
    val description: String,
    val userName: String,
    val userUID: String,
    val date: String,
    val rating: Int = 0
)
