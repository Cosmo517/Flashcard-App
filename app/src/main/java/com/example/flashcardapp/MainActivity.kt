package com.example.flashcardapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)
        val wrongAnswer1 = findViewById<TextView>(R.id.wrong_answer_1)
        val wrongAnswer2 = findViewById<TextView>(R.id.wrong_answer_2)
        val correctAnswer = findViewById<TextView>(R.id.correct_answer)
        val resetAnswers = findViewById<ImageView>(R.id.reset_answers)
        val hideAnswers = findViewById<ImageView>(R.id.toggle_choices)
        val addCard = findViewById<ImageView>(R.id.add_card_button)
        var isShowingAnswers = true
        var hasMultipleChoice = true

        // Detects if the user clicks the Question to reveal the answer
        flashcardQuestion.setOnClickListener()
        {
                flashcardQuestion.visibility = View.INVISIBLE
                flashcardAnswer.visibility = View.VISIBLE
        }

        // Detects if the user clicks the Answer to toggle back to the question
        flashcardAnswer.setOnClickListener()
        {
                flashcardQuestion.visibility = View.VISIBLE
                flashcardAnswer.visibility = View.INVISIBLE
        }

        // Detects if wrongAnswer 1 was clicked and changes the corresponding background colors
        wrongAnswer1.setOnClickListener()
        {
            wrongAnswer1.setBackgroundColor(Color.parseColor("#FF0000"))
            correctAnswer.setBackgroundColor(Color.parseColor("#00FF00"))
        }

        // Detects if wrongAnswer 2 was clicked and changes the corresponding background colors
        wrongAnswer2.setOnClickListener()
        {
            wrongAnswer2.setBackgroundColor(Color.parseColor("#FF0000"))
            correctAnswer.setBackgroundColor(Color.parseColor("#00FF00"))
        }

        // Detects if correctAnswer was clicked and changes the corresponding background colors
        correctAnswer.setOnClickListener()
        {
            correctAnswer.setBackgroundColor(Color.parseColor("#00FF00"))
        }

        // Detects if resetAnswers was clicked and resets all colors/views
        resetAnswers.setOnClickListener()
        {
            wrongAnswer1.setBackgroundColor(Color.parseColor("#FFEB3B"))
            wrongAnswer2.setBackgroundColor(Color.parseColor("#FFEB3B"))
            correctAnswer.setBackgroundColor(Color.parseColor("#FFEB3B"))
            hideAnswers.setImageResource(R.drawable.hide_answers)
            isShowingAnswers = true
            wrongAnswer1.text = "The programming language C++"
            wrongAnswer2.text = "The programming language java"
            correctAnswer.text = "the programming language C"
            flashcardQuestion.text = "What programming language came out in 1978?"
            flashcardAnswer.text = "The programming language C"
            wrongAnswer1.visibility = View.VISIBLE
            wrongAnswer2.visibility = View.VISIBLE
            correctAnswer.visibility = View.VISIBLE
            hasMultipleChoice = true
        }

        // Detects if hideAnswers was clicked and shows/hides the answers as appropriate
        hideAnswers.setOnClickListener()
        {
            if (isShowingAnswers && hasMultipleChoice)
            {
                hideAnswers.setImageResource(R.drawable.show_answers)
                wrongAnswer1.visibility = View.INVISIBLE
                wrongAnswer2.visibility = View.INVISIBLE
                correctAnswer.visibility = View.INVISIBLE
                isShowingAnswers = false
            }
            else if (hasMultipleChoice)
            {
                hideAnswers.setImageResource(R.drawable.hide_answers)
                wrongAnswer1.visibility = View.VISIBLE
                wrongAnswer2.visibility = View.VISIBLE
                correctAnswer.visibility = View.VISIBLE
                isShowingAnswers = true
            }
        }

        // this is used for when the user wants to make their own question
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            // This code is executed in StartingActivity after we come back from EndingActivity
            // This extracts any data that was passed back from EndingActivity
            val data: Intent? = result.data

            if (data != null)
            {
                val question = data.getStringExtra("question")
                val answer = data.getStringExtra("answer")

                //Log.i("MainActivity", "string1: $question")
                //Log.i("MainActivity", "string2: $answer")
                flashcardQuestion.text = question
                flashcardAnswer.text = answer
                hasMultipleChoice = false;
                wrongAnswer1.visibility = View.INVISIBLE
                wrongAnswer2.visibility = View.INVISIBLE
                correctAnswer.visibility = View.INVISIBLE
            }
            else {
                Log.i("MainActivity", "Returned null data from AddCardActivity")
            }
        }


        addCard.setOnClickListener()
        {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }

    }
}