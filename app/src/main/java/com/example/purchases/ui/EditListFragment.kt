package com.example.purchases.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.purchases.R
import com.example.purchases.databinding.EditListLayoutBinding

class EditListFragment: Fragment() {
    private var _binding: EditListLayoutBinding? = null

    private val binding get() = _binding
        ?: throw IllegalStateException("Binding for FragmentEditListBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditListLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonEditScreen.setOnClickListener { findNavController().navigate(R.id.action_edit_to_main) }

        binding.textView.setText(R.string.edit_screen)


    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


}