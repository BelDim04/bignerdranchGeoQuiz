package com.bignerdranch.android.geomain

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val REQUEST_CODE_CHEAT = 0

    private val KEY_CURRENT_INDEX = "CurrentIndex"
    private val KEY_ANSWERED_COUNT = "AnsweredCount"
    private val KEY_RIGHT_ANS_COUNT = "RightAnsCount"
    private val KEY_QUESTION_CHECKED = "QuestionChecked"
    private val KEY_IS_CHEATER = "IsCheater"
    private val KEY_CHEATER_COUNT = "CheaterCount"

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button
    private lateinit var cheaterChanceCountTextView: TextView
    private lateinit var versionSdkTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateViewModel(savedInstanceState)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)
        cheaterChanceCountTextView = findViewById(R.id.cheater_chance_count_text_view)

        versionSdkTextView = findViewById(R.id.version_sdk__text_view)
        versionSdkTextView.text = getString(R.string.api_level,Build.VERSION.SDK)


        updateQuestion()
        cheatButtonCountCheck()

        questionTextView.setOnClickListener { view: View ->

            updateQuestion()
        }
        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }
        nextButton.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }
        prevButton.setOnClickListener { view: View ->
            quizViewModel.moveToPrevious()
            updateQuestion()
        }
        cheatButton.setOnClickListener { view: View ->
            val intent = CheatActivity.newIntent(this, quizViewModel.currentQuestionAnswer)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options =
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_INDEX, quizViewModel.currentIndex)
        outState.putInt(KEY_ANSWERED_COUNT, quizViewModel.answeredCount)
        outState.putInt(KEY_RIGHT_ANS_COUNT, quizViewModel.rightAnsCount)
        outState.putBooleanArray(KEY_QUESTION_CHECKED, quizViewModel.questionChecked)
        outState.putBooleanArray(KEY_IS_CHEATER, quizViewModel.isCheater)
        outState.putInt(KEY_CHEATER_COUNT, quizViewModel.cheaterCount)
    }

    private fun updateViewModel(savedInstanceState: Bundle?) {
        if (savedInstanceState != null
            && quizViewModel.answeredCount != savedInstanceState.getInt(KEY_ANSWERED_COUNT)
        ) {
            quizViewModel.answeredCount = savedInstanceState.getInt(KEY_ANSWERED_COUNT)
            quizViewModel.currentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX)
            quizViewModel.rightAnsCount = savedInstanceState.getInt(KEY_RIGHT_ANS_COUNT)
            quizViewModel.questionChecked =
                savedInstanceState.getBooleanArray(KEY_QUESTION_CHECKED)!!
            quizViewModel.isCheater = savedInstanceState.getBooleanArray(KEY_IS_CHEATER)!!
            quizViewModel.cheaterCount = savedInstanceState.getInt(KEY_CHEATER_COUNT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater[quizViewModel.currentIndex] =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if (quizViewModel.isCheater[quizViewModel.currentIndex]) quizViewModel.cheaterCount--
            cheatButtonCountCheck()
        }
    }

    private fun cheatButtonCountCheck() {
        if (quizViewModel.cheaterCount <= 0) cheatButton.isEnabled = false
        cheaterChanceCountTextView.text = getString(R.string.count_chances,quizViewModel.cheaterCount)
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        if (quizViewModel.currentQuestionChecked) {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        } else {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        quizViewModel.answeredCount++
        quizViewModel.questionCheck()
        trueButton.isEnabled = false
        falseButton.isEnabled = false
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.isCheater[quizViewModel.currentIndex] -> R.string.judgment_toast
            userAnswer == correctAnswer -> {
                quizViewModel.rightAnsCount++
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        if (quizViewModel.answeredCount == quizViewModel.questionBankSize) {
            val result = Toast.makeText(
                this,
                "Sum: " + 100 * quizViewModel.rightAnsCount / quizViewModel.answeredCount + "%",
                Toast.LENGTH_LONG
            )
            result.setGravity(Gravity.TOP, 0, 0)
            result.show()
        }
    }
}