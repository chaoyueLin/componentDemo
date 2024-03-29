package com.example.user

import com.alibaba.android.arouter.facade.template.IProvider

interface UserService : IProvider {
    fun checkLogin(): Boolean

    fun getUserInfo(): UserInfo

}