package com.example.flashcardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import org.w3c.dom.Text

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        val cancel_create = findViewById<ImageView>(R.id.cancel_creation)
        val save_create = findViewById<ImageView>(R.id.save_creation)
        val questionText = findViewById<EditText>(R.id.editTextQuestion)
        val answerText = findViewById<EditText>(R.id.editTextAnswer)

        cancel_create.setOnClickListener()
        {
            finish()
        }

        save_create.setOnClickListener()
        {
            val data = Intent() // create a new intent to store data

            data.putExtra(
                "question",
                questionText.text.toString()
            ) // puts one string into the Intent, with the key as 'string1'

            data.putExtra(
                "answer",
                answerText.text.toString()
            ) // puts one string into the Intent, with the key as 'string2'

            setResult(RESULT_OK, data)
            finish()
        }

    }
}