package net.azarquiel.retrofitcajonbindig.model

import java.io.Serializable

data class post (
    var publisher: String,
    var descripcion: String,
    var imagen: String
):Serializable

data class persona (
    var username:String,
    var imagen: String,
    var fullname: String,
    var biografia: String,
    var uid: String
):Serializable

data class feedPost (
    var postdocument: String,
    var publisher: String,
    var descripcion: String,
    var imagenPost: String,
    var username:String,
    var likes: String,
    var imagenPerfil: String,
    var date: String
):Serializable

