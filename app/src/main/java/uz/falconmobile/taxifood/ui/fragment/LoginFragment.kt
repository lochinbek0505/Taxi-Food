package uz.falconmobile.taxifood.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.databinding.FragmentLoginBinding
import uz.falconmobile.taxifood.db.models.UserData
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.ui.activity.HomeActivity

class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore


    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        database = AppDatabase.getDatabase(requireActivity())
        dao = database.appDao()

        binding.ivBack.setOnClickListener {

            requireActivity().finish()

        }

        binding.tvReg.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)


        }

        binding.btnLogin.setOnClickListener {

            val email = binding.etMail.text.toString()
            val password = binding.etPas.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            }

        }


        val root = binding.root

        return root
    }

    private fun saveData(key: String, value: String) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply() // Apply asynchronously
    }

    private fun getStringData(key: String, defaultValue: String): String? {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue)
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()

                    saveData("is_reg", "reg")

                    readDataWithoutDataClass(auth.currentUser?.uid!!)

                    // Navigate to another fragment or activity
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun readDataWithoutDataClass(userid: String) {
        val docRef = db.collection("users").document(userid) // Replace with actual document ID

//        val user = hashMapOf(
//            "firstName" to firstName,
//            "email" to email,
//            "phone" to phone,
//            "location" to location
//        )


        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve the data as a Map
                    val data = document.data
                    Log.d("Firestore", "DocumentSnapshot data: $data")

                    // Access specific fields
                    val firstName = document.getString("firstName")
                    val email = document.getString("email")
                    val phone = document.getString("phone")
                    val location = document.getString("location")


                    CoroutineScope(Dispatchers.IO).launch {

                        dao.insertUser(
                            UserData(
                                name = firstName!!,
                                email = email!!,
                                number = phone!!,
                                locate = location!!
                            )
                        )


                    }

//                    Toast.makeText(requireActivity(), "CLICK ZAYBAL BLAA ISHLAMAYABDI", Toast.LENGTH_SHORT).show()

                    startActivity(
                        Intent(
                            requireActivity(),
                            HomeActivity::class.java
                        )
                    )
                    requireActivity().finish()


                    Log.d("Firestore", "First Name: $firstName, Last Name: , Email: $email")
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }


}