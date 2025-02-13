package com.aidaole.easytrace

import android.app.Application
import android.content.Context

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        fabnic2(10)
    }

    fun fabnic2(n: Int): Int {
        if (n == 1 || n == 2) {
            return 1
        }
        return fabnic2(n - 1) + fabnic2(n - 2)
    }

    private fun fab2(n: Int): Int {
        var a = 0
        var b = 0
        var c = 0
        var m = 1
        while(m <= n){
            if(m == 1) {
                a = 0
                b = 1
            }
            c = a + b
            a = b
            b = c
            m++
        }
        return c
    }
}