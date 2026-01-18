package me.rafaelldi.einburgerungstest

import java.io.InputStream

object JsonResourceLoader {
    fun loadJson(resourcePath: String): String? {
        return javaClass.getResource(resourcePath)?.readText()
    }

    fun loadJsonStream(resourcePath: String): InputStream? {
        return javaClass.getResourceAsStream(resourcePath)
    }
}
