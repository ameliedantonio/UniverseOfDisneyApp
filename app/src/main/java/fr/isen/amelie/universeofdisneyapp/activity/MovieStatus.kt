package fr.isen.amelie.universeofdisneyapp.activity

data class MovieStatus(
    val movieId: String = "",
    val title: String = "",
    val universeId: String = "",
    val category: String = "",
    val releaseDate: String = "",
    val status: String = "",
    val userEmail: String = "",
    val posterPath: String = ""
) {
    val imageUrl: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"
}