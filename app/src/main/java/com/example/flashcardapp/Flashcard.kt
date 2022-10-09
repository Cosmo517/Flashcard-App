package com.example.flashcardapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Flashcard(
    @ColumnInfo(name = "question") var question: String,
    @ColumnInfo(name = "answer") var answer: String,
    @ColumnInfo(name = "wrong_answer_1") var wrongAnswer1: String? = null,
    @ColumnInfo(name = "wrong_answer_2") var wrongAnswer2: String? = null,
    @PrimaryKey(autoGenerate = true) var uuid: Int = 0,
)
