package com.lfh.routing

import com.lfh.utils.TokenManager
import com.lfh.db.DatabaseConnection
import com.lfh.entities.UserEntity
import com.lfh.models.NoteResponse
import com.lfh.models.User
import com.lfh.models.UserCredentials
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import java.util.*


fun Application.authenticationRoutes() {
    val db = DatabaseConnection.database
    val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))
    routing {
        post("notes/register") {
            val userCredentials = call.receive<UserCredentials>()

            if (!userCredentials.isValidCredentials()) {
                call.respond(HttpStatusCode.BadRequest, NoteResponse(success = false, data = "注册失败，用户名长度在3~6之间"))
                return@post
            }

            val username = userCredentials.username.lowercase(Locale.getDefault())
            val password = userCredentials.hashedPassword()

            val user = db.from(UserEntity).select().where {
                UserEntity.username eq (username)
            }.map {
                it[UserEntity.username]
            }.firstOrNull()
            //拿到一个列的数据
            if (null != user) {
                call.respond(HttpStatusCode.BadRequest, NoteResponse(success = false, data = "该用户名已被添加"))
                return@post
            }

            val result = db.insert(UserEntity) {
                set(it.username, username)
                set(it.password, password)
            }
            if (result == 1) {
                call.respond(HttpStatusCode.Created, NoteResponse(success = true, data = "user has been created"))
            }
        }

        post("notes/login") {
            val userCredentials = call.receive<UserCredentials>()
            //判断用户名是否合法
            if (!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.BadRequest, NoteResponse(success = false, data = "your login info is invalid")
                )
                return@post
            }
            //获取用户传入的用户名和密码
            val username = userCredentials.username.lowercase(Locale.getDefault())
            val password = userCredentials.password
            //根据用户名查询整个用户的信息
            val user: User? = db.from(UserEntity).select().where {
                UserEntity.username eq username
            }.map {
                val id = it[UserEntity.id]!!
                val username = it[UserEntity.username]!!
                val password = it[UserEntity.password]!!
                User(id, username, password)
            }.firstOrNull()
            //判断相关合法性
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(success = false, data = "your username or password error")
                )
                return@post
            }
            //判断密码是否正确
            val passwordRealOk = userCredentials.getHashedPassword(password, user.password)
            if (!passwordRealOk) {
                call.respond(HttpStatusCode.BadRequest, NoteResponse(success = false, data = "your password error"))
            } else {
                val token = tokenManager.generateJWTToken(user)
                call.respond(HttpStatusCode.OK, NoteResponse(success = true, data = token))
            }
        }

        authenticate {
            get("/me") {
                println("11111---->")
                val principle = call.principal<JWTPrincipal>()
                val token = call.parameters
                print(token)
                val username = principle!!.payload.getClaim("username").asString()
                val userId = principle.payload.getClaim("userId").asString()
                call.respondText("hello,$username with id:$userId  ")
            }
        }
    }
}