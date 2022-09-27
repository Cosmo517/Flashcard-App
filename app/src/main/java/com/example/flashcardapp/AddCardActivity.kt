package com.example.flashcardapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        var cancel_create = findViewById<ImageView>(R.id.cancel_creation)

        cancel_create.setOnClickListener()
        {
            finish()
        }
    }
}