package com.lfh

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.lfh.plugins.*
import com.lfh.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {


    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        val config = HoconApplicationConfig(ConfigFactory.load())
        val tokenManager = TokenManager(config)
        println("11111122222222222222222")
        install(Authentication) {
            println("222222222222222222222")
            jwt {
                println("3333333333333")
                verifier(tokenManager.verifyJwtToken())
                realm = config.property("realm").getString()
                validate {jwtCredential ->
                    println("55555555555555")
                    if (jwtCredential.payload.getClaim("username").asString().isNotEmpty()){
                        JWTPrincipal(jwtCredential.payload)
                    }else{
                        null
                    }
                }
            }
        }
        install(ContentNegotiation) {
            json()
        }

        configureRouting()
    }.start(wait = true)
}
