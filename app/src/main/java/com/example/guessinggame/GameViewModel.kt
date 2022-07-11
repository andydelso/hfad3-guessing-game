package com.example.guessinggame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController

class GameViewModel : ViewModel() {
    private val words = listOf("Android", "Activity", "Fragment")
    private val secretWord = words.random().uppercase()
    private var correctGuesses = ""

    // private mutable backing properties with immutable accessors
    private val _secretWordDisplay = MutableLiveData<String>()
    val secretWordDisplay: LiveData<String>
        get() = _secretWordDisplay

    private val _incorrectGuesses = MutableLiveData("")
    val incorrectGuesses: LiveData<String>
        get() = _incorrectGuesses

    private val _livesLeft = MutableLiveData(8)
    val livesLeft: LiveData<Int>
        get() = _livesLeft

    private val _gameOver = MutableLiveData(false)
    val gameOver: LiveData<Boolean>
        get() = _gameOver

    init {
        _secretWordDisplay.value = deriveSecretWordDisplay()
    }

    private fun deriveSecretWordDisplay(): String {
        var display = ""
        secretWord.forEach {
            display += checkLetter(it.toString())
        }
        return display
    }

    private fun checkLetter(letter: String) = when (correctGuesses.contains(letter)) {
        true -> letter
        false -> "_"
    }

    fun makeGuess(currentGuess: String) {
        if (currentGuess.length == 1) {
            // 4b. when a correct guess is made, the secretWordDisplay is updated and passed from viewModel to Fragment, as it is observed
            if (secretWord.contains(currentGuess)) {
                correctGuesses += currentGuess
                _secretWordDisplay.value = deriveSecretWordDisplay()

            // 5. when an incorrect guess is made, the incorrectGuesses is updated and passed from viewModel to Fragment, as it is observed
            } else {
                _incorrectGuesses.value += "$currentGuess " // add a space between incorrect guesses
                _livesLeft.value = livesLeft.value?.dec()
            }

            _gameOver.value = isLost() || isWon()
            // The book solution is as follows
            // if (isWon() || isLost())_gameOver.value = true
            // which is basically the same, but might read cleaner
        }
    }

    private fun isWon() = secretWord.equals(secretWordDisplay.value, true)

    private fun isLost() = (livesLeft.value ?: 0) <= 0

    fun wonLostMessage() : String {
        var message = ""
        if (isWon()) message = "You Won!"
        else if(isLost()) message = "You Lost!"
        message += " The secret word was $secretWord"
        return message
    }

    fun finishGame() {
        // cause the fragment to navigate to result fragment
        _gameOver.value = true
    }
}