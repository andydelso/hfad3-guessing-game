package com.example.guessinggame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        _binding = FragmentGameBinding.inflate(inflater,container, false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}