package com.example.listapp

import com.example.listapp.Data.UserData

class UserStore private constructor() {
    private var user: UserData? = null
    companion object {
        @Volatile
        private var instance: UserStore? = null

        fun getInstance(): UserStore {
            return instance ?: synchronized(this) {
                instance ?: UserStore().also { instance = it }
            }
        }
    }
    fun setUser(user: UserData){
        this.user = user
    }
    fun getUser(): UserData?{
        return user
    }
}



