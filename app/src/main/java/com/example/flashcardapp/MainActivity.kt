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
    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()
    override fun onCreate(savedInstanceState: Bundle?) {
        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()
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
        val nextCard = findViewById<ImageView>(R.id.next_button)
        val deleteCard = findViewById<ImageView>(R.id.delete_card_button)

        var currentCardIndex = 0;
        var isShowingAnswers = true
        var hasMultipleChoice = true

        if (allFlashcards.size > 0)
        {
            flashcardQuestion.text = allFlashcards[0].question
            flashcardAnswer.text = allFlashcards[0].answer
            if (allFlashcards[0].wrongAnswer1 != "NULL" && allFlashcards[0].wrongAnswer2 != "NULL")
            {
                wrongAnswer1.text = allFlashcards[0].wrongAnswer1
                correctAnswer.text = allFlashcards[0].answer
                wrongAnswer2.text = allFlashcards[0].wrongAnswer2
            }
        }

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

        nextCard.setOnClickListener()
        {
            // TODO: Randomize the order in which the cards are shown

            if (allFlashcards.size == 0)
            {
                return@setOnClickListener
            }
            else if (allFlashcards.size > currentCardIndex + 1)
            {
                currentCardIndex++;
            }
            else
            {
                Snackbar.make(flashcardQuestion, "You've reached the end of you're flashcards. Going back to the start.", Snackbar.LENGTH_SHORT).show()
                currentCardIndex = 0;
            }

            flashcardQuestion.text = allFlashcards[currentCardIndex].question
            flashcardAnswer.text = allFlashcards[currentCardIndex].answer
            if (allFlashcards[currentCardIndex].wrongAnswer1 != null && allFlashcards[currentCardIndex].wrongAnswer2 != null)
            {
                wrongAnswer1.text = allFlashcards[currentCardIndex].wrongAnswer1
                correctAnswer.text = allFlashcards[currentCardIndex].answer
                wrongAnswer2.text = allFlashcards[currentCardIndex].wrongAnswer2
                wrongAnswer1.visibility = View.VISIBLE
                correctAnswer.visibility = View.VISIBLE
                wrongAnswer2.visibility = View.VISIBLE
            }
            else if (allFlashcards[currentCardIndex].wrongAnswer1 == null && allFlashcards[currentCardIndex].wrongAnswer2 == null)
            {
                wrongAnswer1.visibility = View.INVISIBLE
                correctAnswer.visibility = View.INVISIBLE
                wrongAnswer2.visibility = View.INVISIBLE
            }
        }

        deleteCard.setOnClickListener()
        {
            flashcardDatabase.deleteCard(flashcardQuestion.text.toString())
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
            if (currentCardIndex > 0)
            {
                currentCardIndex--;
                flashcardQuestion.text = allFlashcards[currentCardIndex].question
                flashcardAnswer.text = allFlashcards[currentCardIndex].answer
                if (allFlashcards[currentCardIndex].wrongAnswer1 != null && allFlashcards[currentCardIndex].wrongAnswer2 != null)
                {
                    wrongAnswer1.text = allFlashcards[currentCardIndex].wrongAnswer1
                    correctAnswer.text = allFlashcards[currentCardIndex].answer
                    wrongAnswer2.text = allFlashcards[currentCardIndex].wrongAnswer2
                    wrongAnswer1.visibility = View.VISIBLE
                    correctAnswer.visibility = View.VISIBLE
                    wrongAnswer2.visibility = View.VISIBLE
                }
                else if (allFlashcards[currentCardIndex].wrongAnswer1 == null && allFlashcards[currentCardIndex].wrongAnswer2 == null)
                {
                    wrongAnswer1.visibility = View.INVISIBLE
                    correctAnswer.visibility = View.INVISIBLE
                    wrongAnswer2.visibility = View.INVISIBLE
                }
            }
            else if (currentCardIndex == 0 && allFlashcards.size == 0)
            {
                flashcardDatabase.deleteCard(flashcardQuestion.text.toString())
                allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                flashcardQuestion.text = "Please create a card"
                flashcardAnswer.text = "Please create a card"
                wrongAnswer1.visibility = View.INVISIBLE
                correctAnswer.visibility = View.INVISIBLE
                wrongAnswer2.visibility = View.INVISIBLE
            }
            else if (currentCardIndex == 0 && allFlashcards.size > 0)
            {
                currentCardIndex = allFlashcards.size - 1
                flashcardQuestion.text = allFlashcards[currentCardIndex].question
                flashcardAnswer.text = allFlashcards[currentCardIndex].answer
                if (allFlashcards[currentCardIndex].wrongAnswer1 != null && allFlashcards[currentCardIndex].wrongAnswer2 != null)
                {
                    wrongAnswer1.text = allFlashcards[currentCardIndex].wrongAnswer1
                    correctAnswer.text = allFlashcards[currentCardIndex].answer
                    wrongAnswer2.text = allFlashcards[currentCardIndex].wrongAnswer2
                    wrongAnswer1.visibility = View.VISIBLE
                    correctAnswer.visibility = View.VISIBLE
                    wrongAnswer2.visibility = View.VISIBLE
                }
                else if (allFlashcards[currentCardIndex].wrongAnswer1 == null && allFlashcards[currentCardIndex].wrongAnswer2 == null)
                {
                    wrongAnswer1.visibility = View.INVISIBLE
                    correctAnswer.visibility = View.INVISIBLE
                    wrongAnswer2.visibility = View.INVISIBLE
                }

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
                val userQuestion = data.getStringExtra("QUESTION_KEY").toString()
                val userAnswer = data.getStringExtra("ANSWER_KEY").toString()
                val userWrongAnswer1 = data.getStringExtra("WRONG_ANSWER_1_KEY").toString()
                val userWrongAnswer2 = data.getStringExtra("WRONG_ANSWER_2_KEY").toString()

                // Makes a snackbar letting the user know the flashcard was created
                Snackbar.make(findViewById(R.id.flashcard_question),
                    "Flashcard successfully created",
                    Snackbar.LENGTH_SHORT)
                    .show()

                Log.i("MainActivityUser", "userQuestion: $userQuestion")
                Log.i("MainActivityUser", "userAnswer: $userAnswer")
                Log.i("MainActivityUser", "userWrongAnswer1: $userWrongAnswer1")
                Log.i("MainActivityUser", "userWrongAnswer2: $userWrongAnswer2")

                if (userWrongAnswer1 != "null" && userWrongAnswer2 != "null")
                {
                    flashcardDatabase.insertCard(Flashcard(userQuestion, userAnswer, userWrongAnswer1, userWrongAnswer2))
                }
                else if (userWrongAnswer1 == "null" && userWrongAnswer2 == "null")
                {
                    flashcardDatabase.insertCard(Flashcard(userQuestion, userAnswer))
                }
                allFlashcards = flashcardDatabase.getAllCards().toMutableList()


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

        // TODO: User should be able to edit a card and see the edit saved when they browse through their deck of cards


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