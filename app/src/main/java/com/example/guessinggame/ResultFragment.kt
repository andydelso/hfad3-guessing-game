package com.example.guessinggame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.guessinggame.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: ResultViewModel
    lateinit var viewModelFactory: ResultViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment from the view binding
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val view = binding.root

        val result = ResultFragmentArgs.fromBundle(requireArguments()).result

        viewModelFactory = ResultViewModelFactory(result)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)

        binding.wonLost.text = viewModel.result

        binding.newGameButton.setOnClickListener {
            val action = ResultFragmentDirections.actionResultFragmentToGameFragment()
            view.findNavController().navigate(action)
        }

        return view
    }
}