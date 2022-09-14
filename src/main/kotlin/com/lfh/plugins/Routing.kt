package com.lfh.plugins

import com.lfh.routing.authenticationRoutes
import com.lfh.routing.notesRouters
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import java.io.File

fun Application.configureRouting() {

    routing {
        get("/") {
            val responseBody = UserResponse("王志宾","wangzhibin@gmail.com")
            call.respond(responseBody)
        }
        get("/headers"){
            call.response.headers.append("server-name","ktorServer")
            call.response.headers.append("user-name","wangzhibin")
            call.respondText("Headers Attached")
        }
        get ("fileDownload"){
            val file = File("jayMusic/PANO0002.jpg")
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName,"downloadImage.jpg"
                ).toString()
            )

            call.respondFile(file)
        }
        get ("fileOpen"){
            val file = File("jayMusic/jay.mp3")
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Inline.withParameter(
                    ContentDisposition.Parameters.FileName,"jay.mp3"
                ).toString()
            )
            call.respondFile(file)
        }
    }
    //notes
//    notesRouters()
    authenticationRoutes()

}

@kotlinx.serialization.Serializable
data class UserResponse(val name:String,val email:String)