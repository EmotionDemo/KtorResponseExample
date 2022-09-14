package com.lfh.models

@kotlinx.serialization.Serializable
data class NoteRequest(
    val note: String
)