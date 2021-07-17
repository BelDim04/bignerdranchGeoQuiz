package com.bignerdranch.android.geomain

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.TextView

val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true"
val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown"

class CheatActivity : AppCompatActivity() {

    private var answerIsTrue = false
    private var isAnswerShown = false

    private val KEY_IS_ANSWER_SHOWN = "IsAnswerShown"

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).putExtra(
                EXTRA_ANSWER_IS_TRUE,
                answerIsTrue
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        isAnswerShown = savedInstanceState?.getBoolean(KEY_IS_ANSWER_SHOWN, false) ?: false

        setAnswerShownResult()

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        if (isAnswerShown) {
            val answerText = if (answerIsTrue) R.string.true_button
            else R.string.false_button
            answerTextView.setText(answerText)
        }
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener { view: View ->
            val answerText = if (answerIsTrue) R.string.true_button
            else R.string.false_button
            answerTextView.setText(answerText)
            if (!answerTextView.hasOnClickListeners()) {
                answerTextView.setOnClickListener { view: View ->
                    finish()
                }
            }
            isAnswerShown = true
            setAnswerShownResult()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_ANSWER_SHOWN, isAnswerShown)
    }

    private fun setAnswerShownResult() {
        val data = Intent().putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        setResult(RESULT_OK, data)
    }
}