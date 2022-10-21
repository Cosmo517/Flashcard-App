package com.example.flashcardapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()
    var countDownTimer: CountDownTimer? = null
    // Timers
    private fun startTimer()
    {
        countDownTimer?.cancel()
        countDownTimer?.start()
    }
    private fun cancelTimer()
    {
        countDownTimer?.cancel()
    }

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
        val timeMode = findViewById<ImageView>(R.id.timed_mode)
        val hideAnswers = findViewById<ImageView>(R.id.toggle_choices)
        val addCard = findViewById<ImageView>(R.id.add_card_button)
        val editCard = findViewById<ImageView>(R.id.edit_button)
        val nextCard = findViewById<ImageView>(R.id.next_button)
        val deleteCard = findViewById<ImageView>(R.id.delete_card_button)
        val timerText = findViewById<TextView>(R.id.timer)
        val viewKonfetti = findViewById<KonfettiView>(R.id.konfettiView)
        var currentCardIndex = 0
        var isShowingAnswers = true
        var hasMultipleChoice = true
        var isTimedMode = false

        // Camera distances so the flashcard animations don't look sloppy
        flashcardQuestion.cameraDistance = 28000f
        flashcardAnswer.cameraDistance = 28000f

        // Display countdown timer
        countDownTimer = object : CountDownTimer(16000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "" + millisUntilFinished / 1000
            }

            override fun onFinish() {}
        }

        // Determine if there should be a default flashcard or if there are flashcards saved (and choose one to display)
        if (allFlashcards.size > 0)
        {
            flashcardQuestion.text = allFlashcards[0].question
            flashcardAnswer.text = allFlashcards[0].answer
            if (allFlashcards[currentCardIndex].wrongAnswer1 != null && allFlashcards[currentCardIndex].wrongAnswer2 != null)
            {
                wrongAnswer1.text = allFlashcards[currentCardIndex].wrongAnswer1
                correctAnswer.text = allFlashcards[currentCardIndex].answer
                wrongAnswer2.text = allFlashcards[currentCardIndex].wrongAnswer2
                wrongAnswer1.visibility = View.VISIBLE
                correctAnswer.visibility = View.VISIBLE
                wrongAnswer2.visibility = View.VISIBLE
                hasMultipleChoice = true
                if (isTimedMode)
                    startTimer()
            }
            else if (allFlashcards[currentCardIndex].wrongAnswer1 == null && allFlashcards[currentCardIndex].wrongAnswer2 == null)
            {
                wrongAnswer1.visibility = View.INVISIBLE
                correctAnswer.visibility = View.INVISIBLE
                wrongAnswer2.visibility = View.INVISIBLE
                hasMultipleChoice = false
                if (isTimedMode)
                    startTimer()
            }
        }

        // Detects if the user clicks the Question to reveal the answer
        flashcardQuestion.setOnClickListener()
        {
            flashcardQuestion.animate()
                .rotationY(90f)
                .setDuration(200)
                .withEndAction(
                    Runnable {
                        flashcardQuestion.setVisibility(View.INVISIBLE)
                        flashcardAnswer.visibility = View.VISIBLE
                        // second quarter turn
                        flashcardAnswer.rotationY = -90f
                        flashcardAnswer.animate()
                            .rotationY(0f)
                            .setDuration(200)
                            .start()
                    }
                ).start()
        }

        // Detects if the user clicks the Answer to toggle back to the question
        flashcardAnswer.setOnClickListener()
        {
            flashcardAnswer.animate()
                .rotationY(-90f)
                .setDuration(200)
                .withEndAction(
                    Runnable {
                        flashcardAnswer.setVisibility(View.INVISIBLE)
                        flashcardQuestion.visibility = View.VISIBLE
                        // second quarter turn
                        flashcardQuestion.rotationY = 90f
                        flashcardQuestion.animate()
                            .rotationY(0f)
                            .setDuration(200)
                            .start()
                    }
                ).start()

        }

        val correctAnswerColor = "#6fdbff"
        val incorrectAnswerColor = "#ddf6ff"
        // Detects if wrongAnswer 1 was clicked and changes the corresponding background colors
        wrongAnswer1.setOnClickListener()
        {
            wrongAnswer1.setBackgroundColor(Color.parseColor(incorrectAnswerColor))
            correctAnswer.setBackgroundColor(Color.parseColor(correctAnswerColor))
            cancelTimer()
        }

        // Detects if wrongAnswer 2 was clicked and changes the corresponding background colors
        wrongAnswer2.setOnClickListener()
        {
            wrongAnswer2.setBackgroundColor(Color.parseColor(incorrectAnswerColor))
            correctAnswer.setBackgroundColor(Color.parseColor(correctAnswerColor))
            cancelTimer()
        }

        // Detects if correctAnswer was clicked and changes the corresponding background colors
        correctAnswer.setOnClickListener()
        {
            correctAnswer.setBackgroundColor(Color.parseColor(correctAnswerColor))
            // Confetti Library taken from: https://github.com/DanielMartinus/Konfetti
            val party = Party(
                speed = 0f,
                maxSpeed = 20f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                position = Position.Relative(0.5, 0.35)
            )
            viewKonfetti.start(party)
            cancelTimer()
        }

        // Detects if the user wants to be timed
        timeMode.setOnClickListener()
        {
            isTimedMode = !isTimedMode
            if (isTimedMode)
            {
                startTimer()
            }
            else
            {
                cancelTimer()
                timerText.text = ""
            }
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

        // Detects if the nextCard button was clicked, and if so, move to the next card and display it
        nextCard.setOnClickListener()
        {
            val leftOutAnim = AnimationUtils.loadAnimation(this, R.anim.left_out)
            val rightInAnim = AnimationUtils.loadAnimation(this, R.anim.right_in)

            // LeftIn animation
            leftOutAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // this method is called when the animation first starts
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // this method is called when the animation ends
                    // Start the next animation to make it look smooth (and change the colors of the MCQ)
                    findViewById<TextView>(R.id.flashcard_question).startAnimation(rightInAnim)
                    wrongAnswer1.setBackgroundColor(Color.parseColor("#bbeeff"))
                    wrongAnswer2.setBackgroundColor(Color.parseColor("#bbeeff"))
                    correctAnswer.setBackgroundColor(Color.parseColor("#bbeeff"))
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // we don't need to worry about this method
                }
            })

            // RightIn animation
            rightInAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // this method is called when the animation first starts
                    // Randomize the flashcard the player is going to next and dont pick the current flashcard the user is on
                    var oldCardIndex = currentCardIndex
                    while (oldCardIndex == currentCardIndex)
                    {
                        currentCardIndex = (0..allFlashcards.size-1).random()
                    }

                    flashcardQuestion.text = allFlashcards[currentCardIndex].question
                    flashcardAnswer.text = allFlashcards[currentCardIndex].answer
                    // Checks to see if the flashcard the user is going to has Multiple Choices or not
                    if (allFlashcards[currentCardIndex].wrongAnswer1 != null && allFlashcards[currentCardIndex].wrongAnswer2 != null)
                    {
                        hasMultipleChoice = true
                        hideAnswers.setImageResource(R.drawable.hide_answers)
                        isShowingAnswers = true
                        wrongAnswer1.text = allFlashcards[currentCardIndex].wrongAnswer1
                        correctAnswer.text = allFlashcards[currentCardIndex].answer
                        wrongAnswer2.text = allFlashcards[currentCardIndex].wrongAnswer2
                        wrongAnswer1.visibility = View.VISIBLE
                        correctAnswer.visibility = View.VISIBLE
                        wrongAnswer2.visibility = View.VISIBLE
                    }
                    else if (allFlashcards[currentCardIndex].wrongAnswer1 == null && allFlashcards[currentCardIndex].wrongAnswer2 == null)
                    {
                        hasMultipleChoice = false
                        hideAnswers.setImageResource(R.drawable.hide_answers)
                        isShowingAnswers = false
                        wrongAnswer1.visibility = View.INVISIBLE
                        correctAnswer.visibility = View.INVISIBLE
                        wrongAnswer2.visibility = View.INVISIBLE
                    }
                    if (isTimedMode)
                        startTimer()
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // this method is called when the animation ends
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // we don't need to worry about this method
                }
            })

            // This determines whether or not to start the animation
            if (allFlashcards.size == 0)
            {
                return@setOnClickListener
            }
            else if (allFlashcards.size > 1)
            {
                findViewById<TextView>(R.id.flashcard_question).startAnimation(leftOutAnim)
            }
        }


        // Detects if the deleteCard button was clicked, and if so, deletes the current card and moves the user back one card
        deleteCard.setOnClickListener()
        {
            flashcardDatabase.deleteCard(flashcardQuestion.text.toString())
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
            // This handles moving the user back by one card.
            // If they have more than one card in their deck, update all the flashcard information
            // If not, display a splashscreen for them to make a new card
            // This also handles updating all the information when they move back by a card
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


        // This is used for when the user wants to make their own question
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

        // Detects if addCard button was clicked, and if so, allow the user to add a card
        addCard.setOnClickListener()
        {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

        // This is used when the user wants to edit a card
        val editResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            val data: Intent? = result.data
            if (data != null) { // Check that we have data returned
                // grab the data passed from AddCardActivity
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
                // set the TextViews to show the EDITED question and answer
                flashcardQuestion.text = userQuestion
                flashcardAnswer.text = userAnswer
                correctAnswer.text = userAnswer
                if (userWrongAnswer1 != "null" && userWrongAnswer2 != "null")
                {
                    wrongAnswer1.text = userWrongAnswer1
                    wrongAnswer2.text = userWrongAnswer2
                    allFlashcards[currentCardIndex].wrongAnswer1 = userWrongAnswer1
                    allFlashcards[currentCardIndex].wrongAnswer2 = userWrongAnswer2
                    wrongAnswer1.visibility = View.VISIBLE
                    correctAnswer.visibility = View.VISIBLE
                    wrongAnswer2.visibility = View.VISIBLE
                    hasMultipleChoice = true
                }
                else
                {
                    allFlashcards[currentCardIndex].wrongAnswer1 = null
                    allFlashcards[currentCardIndex].wrongAnswer2 = null
                    wrongAnswer1.visibility = View.INVISIBLE
                    correctAnswer.visibility = View.INVISIBLE
                    wrongAnswer2.visibility = View.INVISIBLE
                    hasMultipleChoice = false
                }
                allFlashcards[currentCardIndex].question = userQuestion
                allFlashcards[currentCardIndex].answer = userAnswer
                flashcardDatabase.updateCard(allFlashcards[currentCardIndex])

            }
            else
            {
                Log.i("MainActivity", "Returned null data from AddCardActivity")
            }
        }

        // Detects if editCard button was clicked, and if so, allow the user to edit the current card
        editCard.setOnClickListener()
        {
            val intent = Intent(this, AddCardActivity::class.java)
            intent.putExtra("question", flashcardQuestion.text)
            intent.putExtra("answer", flashcardAnswer.text)
            Log.i("MainActivityUser", "CurrentIndex: $currentCardIndex")
            if (allFlashcards[currentCardIndex].wrongAnswer1 != null && allFlashcards[currentCardIndex].wrongAnswer1 != null)
            {
                intent.putExtra("wrongAnswer1", wrongAnswer1.text)
                intent.putExtra("wrongAnswer2", wrongAnswer2.text)
            }
            editResultLauncher.launch(intent)
        }



    }
}