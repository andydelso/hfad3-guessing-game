package com.example.guessinggame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

class ResultFragment : Fragment() {
    lateinit var viewModel: ResultViewModel
    lateinit var viewModelFactory: ResultViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val result = ResultFragmentArgs.fromBundle(requireArguments()).result

        viewModelFactory = ResultViewModelFactory(result)
        viewModel = ViewModelProvider(this@ResultFragment, viewModelFactory).get(ResultViewModel::class.java)

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Surface {
                        view?.let { ResultFragmentContent(view = it, viewModel = viewModel) }
                    }
                }
            }
        }
    }

    @Composable
    fun ResultFragmentContent(view: View, viewModel: ResultViewModel) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResultText(result = viewModel.result)
            NewGameButton {
                view.findNavController().navigate(R.id.action_resultFragment_to_gameFragment)
            }
        }
    }

    @Composable
    fun NewGameButton(clicked: () -> Unit) {
        Button(onClick = clicked) {
            Text(text = "Start New Game")
        }
    }

    @Composable
    fun ResultText(result: String) {
        Text(
            text = result,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
    }
}