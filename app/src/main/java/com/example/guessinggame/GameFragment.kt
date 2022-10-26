package com.example.guessinggame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

class GameFragment : Fragment() {
    lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // get the viewmodel using the basic provider since there are no arguments
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // we need to retain this because there is actual logic here for navigation
        viewModel.gameOver.observe(viewLifecycleOwner) { isGameOver ->
            if (isGameOver) {
                // 6. navigate to results when win or lose condition is met
                val action = GameFragmentDirections.actionGameFragmentToResultFragment(viewModel.wonLostMessage())
                view?.findNavController()?.navigate(action)
            }
        }

        // set the content to a ComposeView for the Fragment
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Surface {
                        // provide our Composables with the viewModel
                        GameFragmentContent(viewModel)
                    }
                }
            }
        }
    }

    @Composable
    fun GameFragmentContent(viewModel: GameViewModel) {
        val guess = remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SecretWordDisplay(viewModel = viewModel)
            }
            LivesLeftText(viewModel = viewModel)
            IncorrectGuessesText(viewModel = viewModel)
            // show the initial blank guess and update it as typing happens
            EnterGuess(guess = guess.value) { guess.value = it }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // guess button will make a guess to the viewmodel and then return to blank in EnterGuess
                GuessButton {
                    viewModel.makeGuess(guess.value.uppercase())
                    guess.value = ""
                }
                // Finish game button will request the viewmodel to finish the game
                FinishGameButton {
                    viewModel.finishGame()
                }

            }
        }
    }

    @Composable
    fun FinishGameButton(clicked: () -> Unit) {
        Button(onClick = clicked) {
            Text(text = "Finish Game")
        }
    }

    @Composable
    fun EnterGuess(guess: String, changed: (String) -> Unit) {
        TextField(
            value = guess,
            label = { Text(text = "Guess a letter")},
            onValueChange = changed
        )
    }

    @Composable
    fun GuessButton(clicked: () -> Unit) {
        Button(onClick = clicked) {
            Text(text = "Guess!")
        }
    }
    
    @Composable
    fun IncorrectGuessesText(viewModel: GameViewModel) {
        val incorrectGuesses = viewModel.incorrectGuesses.observeAsState()
        incorrectGuesses.value?.let { 
            Text(text = stringResource(id = R.string.incorrect_guesses, it))
        }
    }

    @Composable
    fun LivesLeftText(viewModel: GameViewModel) {
        val livesLeft = viewModel.livesLeft.observeAsState()
        livesLeft.value?.let {
            Text(text = stringResource(id = R.string.lives_left, it))
        }
    }
    
    @Composable
    fun SecretWordDisplay(viewModel: GameViewModel) {
        val display = viewModel.secretWordDisplay.observeAsState()
        display.value?.let {
            Text(text = it, fontSize = 36.sp, letterSpacing = 0.1.em)
        }
    }
}