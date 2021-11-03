package com.example.user

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route

/**
 **/
@Route(path = "/user/data")
class UserServiceImpl: UserService {
    override fun checkLogin(): Boolean {
        return false
    }

    override fun getUserInfo(): UserInfo {
        return UserInfo("user", 1000)
    }

    override fun init(context: Context?) {}
}