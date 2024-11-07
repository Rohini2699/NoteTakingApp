package com.note.notetakingapp.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.note.notetakingapp.R
import com.note.notetakingapp.databinding.FragmentLoginBinding
import com.note.notetakingapp.databinding.FragmentSignUpBinding


class LoginFragment : Fragment() {
    private var _binding:FragmentLoginBinding?=null
//    _binding is a reference to the view binding object for your fragment's layout, created by the View Binding library.
    private  val binding get() = _binding!!
//    The _binding variable is declared as nullable (FragmentLoginBinding?), allowing you to set it to null when the view is destroyed (to prevent memory leaks).

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            val confirmPassword = binding.loginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                if (password == confirmPassword){
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            findNavController().navigate(R.id.action_loginFragment2_to_splashFragment)
                        } else {
                            Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Password does not matched", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirectText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_signUpFragment)
        }

    }
    }
