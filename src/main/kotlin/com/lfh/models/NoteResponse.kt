package com.lfh.models

@kotlinx.serialization.Serializable
data class NoteResponse<T>(val data: T, val success: Boolean)