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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.guessinggame.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater,container, false).apply {
            // Apply a compose view in the inflated fragment
            composeView.setContent {
                MaterialTheme {
                    Surface {
                        // provide our Composables with the viewModel
                        GameFragmentContent(viewModel)
                    }
                }
            }
        }
        val view = binding.root

        // What happens when the app runs flow:
        // 1. ask the view model provider for an instance of the view model
        // Which the first time will initialize it and it's properties
        // This includes private backing properties with public accessors for read-only privacy
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // setup data binding with live data
        binding.gameViewModel = viewModel // layout's views can now use this to access the viewModel's properties and methods
        binding.lifecycleOwner = viewLifecycleOwner // layout's views can now observe the viewModel's live data and respond to the changes

//        updateScreen() // no longer needed as we are using live data

        // No longer needed as the data binding is taking care of this and the string formatting
//        // 2. Fragment observes changes to dynamic properties of view model
//        viewModel.incorrectGuesses.observe(viewLifecycleOwner) { newIncorrectGuesses ->
//            // 3. Fragment updates its views with values of the observed properties
//            binding.incorrectGuesses.text = "Incorrect guesses: $newIncorrectGuesses"
//        }
//
//        // 2.
//        viewModel.livesLeft.observe(viewLifecycleOwner) { newLives ->
//            // 3.
//            binding.lives.text = "You have $newLives left"
//        }
//
//        // 2.
//        viewModel.secretWordDisplay.observe(viewLifecycleOwner) { newSecretWordDisplay ->
//            // 3.
//            binding.word.text = newSecretWordDisplay
//        }

        // we need to retain this because there is actual logic here for navigation
        viewModel.gameOver.observe(viewLifecycleOwner) { isGameOver ->
            if (isGameOver) {
                // 6. navigate to results when win or lose condition is met
                val action = GameFragmentDirections.actionGameFragmentToResultFragment(viewModel.wonLostMessage())
                view.findNavController().navigate(action)
            }
        }

        // 4a. user taps guess button
        binding.guessButton.setOnClickListener {
            val currentGuess = binding.guess.text.toString().uppercase()
            // 4b. step into makeGuess with currentGuess
            viewModel.makeGuess(currentGuess)
            binding.guess.text.clear() // clear the input after handling
//            updateScreen() // no longer needed as we are using live data
        }

        return view
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}