package com.example.kotlinreflectionpractice

class User{
    var name: String = ""
    get() = field
        set(value) {field = value}
}


