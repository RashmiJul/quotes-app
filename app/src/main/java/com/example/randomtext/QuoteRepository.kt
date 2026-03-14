package com.example.randomtext

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class QuoteRepository(private val context: Context) {

    private fun loadQuotes(): List<String> {
        val inputStream = context.assets.open("quotes.txt")
        return BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
            lines.filter { it.isNotBlank() }.toList()
        }
    }

    fun getRandomQuote(): String {
        val quotes = loadQuotes()
        if (quotes.isEmpty()) return "No quotes available."
        return quotes.random()
    }
}
