package com.aidaole.easytrace

class ExcludedClass {
    fun shouldNotBeTraced() {
        println("This method should not be instrumented")
    }
}