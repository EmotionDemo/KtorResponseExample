package com.lfh.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.lfh.models.User
import io.ktor.server.config.*
import java.util.*
import kotlin.math.exp

class TokenManager(config: HoconApplicationConfig) {

    val audience = config.property("audience").getString()
    val secret = config.property("secret").getString()
    val issuer = config.property("issuer").getString()
    val expirationDate = System.currentTimeMillis() + 600000

    fun generateJWTToken(user: User): String {

        println("audience-->$audience")
        println("secret-->$secret")
        println("issuer-->$issuer")
        println("expirationDate-->$expirationDate")

        //每60分钟过期一次
        val token = JWT.create().withAudience(audience).withIssuer(issuer).withClaim("username", user.username)
            .withClaim("userId", user.id).withExpiresAt(Date(expirationDate)).sign(Algorithm.HMAC256(secret))
        println("current Token ----->$token")
        return token
    }


    /**
     * @Description:验证Token
     * @Author: 李丰华
     * @Email: 739574055@qq.com
     * @CreateDate: 2022/9/7 21:28
     */
    fun verifyJwtToken(): JWTVerifier {
        return JWT.require(Algorithm.HMAC256(secret)).withAudience(audience).withIssuer(issuer).build()
    }

}