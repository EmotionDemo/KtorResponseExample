package com.lfh.models

import org.mindrot.jbcrypt.BCrypt

@kotlinx.serialization.Serializable
data class UserCredentials(val username: String, val password: String) {
    fun hashedPassword(): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun isValidCredentials(): Boolean {
        return username.length in 3..10
    }

    /**
     * @Description: 明文，密文
     * @Author: 李丰华
     * @Email: 739574055@qq.com
     * @CreateDate: 2022/9/7 14:38
     */
    fun getHashedPassword(plainText: String,keyedPassword:String):Boolean{
        return BCrypt.checkpw(plainText,keyedPassword)
    }
}