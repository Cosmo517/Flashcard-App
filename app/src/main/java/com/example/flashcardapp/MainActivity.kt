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
import com.google.android.material.snackbar.Snackbar

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
        val editCard = findViewById<ImageView>(R.id.edit_button)
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
            flashcardQuestion.visibility = View.VISIBLE
            flashcardAnswer.visibility = View.INVISIBLE
            wrongAnswer1.text = "The programming language C++"
            wrongAnswer2.text = "The programming language Java"
            correctAnswer.text = "The programming language C"
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
                val userQuestion = data.getStringExtra("QUESTION_KEY")
                val userAnswer = data.getStringExtra("ANSWER_KEY")
                val userWrongAnswer1 = data.getStringExtra("WRONG_ANSWER_1_KEY")
                val userWrongAnswer2 = data.getStringExtra("WRONG_ANSWER_2_KEY")

                // Makes a snackbar letting the user know the flashcard was created
                Snackbar.make(findViewById(R.id.flashcard_question),
                    "Flashcard successfully created",
                    Snackbar.LENGTH_SHORT)
                    .show()

                Log.i("MainActivityUser", "userQuestion: $userQuestion")
                Log.i("MainActivityUser", "userAnswer: $userAnswer")
                Log.i("MainActivityUser", "userWrongAnswer1: $userWrongAnswer1")
                Log.i("MainActivityUser", "userWrongAnswer2: $userWrongAnswer2")

                // set the questions/answers to the users questions
                flashcardQuestion.text = userQuestion
                flashcardAnswer.text = userAnswer
                correctAnswer.text = userAnswer


                // check to see if the user used multiple choice questions, and if they did, set the text
                if (userWrongAnswer1.toString() != "null" && userWrongAnswer2.toString() != "null")
                {
                    hasMultipleChoice = true
                    wrongAnswer1.text = userWrongAnswer1
                    wrongAnswer2.text = userWrongAnswer2
                    wrongAnswer1.visibility = View.VISIBLE
                    wrongAnswer2.visibility = View.VISIBLE
                    correctAnswer.visibility = View.VISIBLE
                }

                // if the user did not use MCQ, hide the MC answers
                if (userWrongAnswer1.toString() == "null" && userWrongAnswer2.toString() == "null")
                {
                    hasMultipleChoice = false;
                    wrongAnswer1.text = ""
                    wrongAnswer2.text = ""
                    wrongAnswer1.visibility = View.INVISIBLE
                    wrongAnswer2.visibility = View.INVISIBLE
                    correctAnswer.visibility = View.INVISIBLE
                }
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

        editCard.setOnClickListener()
        {
            val intent = Intent(this, AddCardActivity::class.java)
            intent.putExtra("question", flashcardQuestion.text)
            intent.putExtra("answer", flashcardAnswer.text)
            if (wrongAnswer1.text.toString() != "null" && wrongAnswer2.text.toString() != "null")
            {
                intent.putExtra("wrongAnswer1", wrongAnswer1.text)
                intent.putExtra("wrongAnswer2", wrongAnswer2.text)
            }
            resultLauncher.launch(intent)
        }

    }
}