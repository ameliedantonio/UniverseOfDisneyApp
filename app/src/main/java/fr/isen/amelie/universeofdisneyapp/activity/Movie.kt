package fr.isen.amelie.universeofdisneyapp.activity

data class Movie(
    val id: String = "",
    val title: String = "",
    val universeId: String = "",
    val category: String = "",
    val releaseDate: String = "",
    val genre: String = "",
    val posterPath: String = ""
    ){
    val imageUrl: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"
}