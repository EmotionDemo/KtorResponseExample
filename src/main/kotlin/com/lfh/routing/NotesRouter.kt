package com.lfh.routing

import com.lfh.db.DatabaseConnection
import com.lfh.entities.NoteEntity
import com.lfh.models.Note
import com.lfh.models.NoteRequest
import com.lfh.models.NoteResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
 import org.ktorm.dsl.*

fun Application.notesRouters() {
    val db = DatabaseConnection.database
    routing {
        get("/notes") {
            val notes = db.from(NoteEntity).select().map {
                val id = it[NoteEntity.id]
                val note = it[NoteEntity.note]
                Note(id ?: -1, note ?: "")
            }
            call.respond(notes)
        }

        post("/notes") {
            val request = call.receive<NoteRequest>()
            val result = db.insert(NoteEntity) {
                set(it.note, request.note)
            }
            if (result == 1) {
                //ok
                call.respond(HttpStatusCode.OK, NoteResponse(success = true, data = "insert success"))
            } else {
                //失败
                call.respond(HttpStatusCode.BadRequest, NoteResponse(success = false, data = "insert failed"))
            }
        }

        get("notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1

            val note = db.from(NoteEntity)
                .select()
                .where { NoteEntity.id eq id }
                .map {
                    val id = it[NoteEntity.id]!!
                    val note = it[NoteEntity.note]!!
                    Note(id = id, note = note)
                }.firstOrNull()
            if (note == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    NoteResponse(success = false, data = "query error not found by id:$id")
                )
            } else {
                call.respond(HttpStatusCode.OK, NoteResponse(success = true, data = note))
            }
        }

        put("notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val updatedNote = call.receive<NoteRequest>()

            val rowsEffected = db.update(NoteEntity) {
                set(it.note, updatedNote.note)
                where {
                    it.id eq (id)
                }
            }
            if (rowsEffected == 1) {
                call.respond(
                    HttpStatusCode.OK,
                    NoteResponse(success = true, data = "note has been updata")
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(success = false, data = "note updata failed")
                )
            }

        }

        delete("notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val rowsEffect = db.delete(NoteEntity) {
                it.id eq (id)
            }
            if (rowsEffect == 1) {
                call.respond(HttpStatusCode.OK, NoteResponse(success = true, data = "id is $id has been deleted"))
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(success = false, data = "id is ${id} deleted failed")
                )
            }
        }
    }
}