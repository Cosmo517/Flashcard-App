package com.example.flashcardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import org.w3c.dom.Text

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        var s1 = intent.getStringExtra("question")
        var s2 = intent.getStringExtra("answer")
        var s3 = intent.getStringExtra("wrongAnswer1")
        var s4 = intent.getStringExtra("wrongAnswer2")

        val cancelCreate = findViewById<ImageView>(R.id.cancel_creation)
        val saveCreate = findViewById<ImageView>(R.id.save_creation)
        val questionText = findViewById<EditText>(R.id.editTextQuestion)
        val answerText = findViewById<EditText>(R.id.editTextAnswer)
        val wrongAnswer1 = findViewById<EditText>(R.id.editTextWrongAnswer1)
        val wrongAnswer2 = findViewById<EditText>(R.id.editTextWrongAnswer2)

        questionText.setText(s1)
        answerText.setText(s2)
        wrongAnswer1.setText(s3)
        wrongAnswer2.setText(s4)

        cancelCreate.setOnClickListener()
        {
            finish()
        }

        saveCreate.setOnClickListener()
        {
            if ((questionText.text.toString() != "" && answerText.text.toString() != ""
                        && wrongAnswer1.text.toString() == "" && wrongAnswer2.text.toString() == "")
                || (questionText.text.toString() != "" && answerText.text.toString() != ""
                        && wrongAnswer1.text.toString() != "" && wrongAnswer2.text.toString() != ""))
            {
                val data = Intent() // create a new intent to store data
                data.putExtra(
                    "QUESTION_KEY",
                    questionText.text.toString()
                ) // puts one string into the Intent, with the key as 'question'

                data.putExtra(
                    "ANSWER_KEY",
                    answerText.text.toString()
                ) // puts one string into the Intent, with the key as 'answer'

                if  (!wrongAnswer1.text.toString().equals(""))
                {
                    data.putExtra(
                        "WRONG_ANSWER_1_KEY",
                        wrongAnswer1.text.toString()
                    ) // puts one string into the Intent, with the key as 'wrongAnswer1'
                }

                if (!wrongAnswer2.text.toString().equals(""))
                {
                    data.putExtra(
                        "WRONG_ANSWER_2_KEY",
                        wrongAnswer2.text.toString()
                    ) // puts one string into the Intent, with the key as 'wrongAnswer2'
                }

                setResult(RESULT_OK, data)
                finish()
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Question, answer, and all multiple choices should be filled out if applicable", Toast.LENGTH_LONG).show();
            }
        }

    }
}