package com.bignerdranch.android.geomain

import android.util.Log
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {
    private val TAG = "QuizViewModel"

    private val questionBank: Array<Question> = arrayOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var questionChecked = BooleanArray(questionBank.size) { false }
    var isCheater = BooleanArray(questionBank.size) { false }

    var currentIndex = 0
    var answeredCount = 0
    var rightAnsCount = 0
    var cheaterCount = 3

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    val currentQuestionChecked: Boolean
        get() = questionChecked[currentIndex]
    val questionBankSize: Int
        get() = questionBank.size


    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = (questionBank.size + currentIndex - 1) % questionBank.size
    }

    fun questionCheck() {
        questionChecked[currentIndex] = true
    }
}