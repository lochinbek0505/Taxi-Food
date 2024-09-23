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
import uz.falconmobile.taxifood.databinding.FragmentRegisterBinding
import uz.falconmobile.taxifood.db.models.UserData
import uz.falconmobile.taxifood.db.utilits.AppDao
import uz.falconmobile.taxifood.db.utilits.AppDatabase
import uz.falconmobile.taxifood.ui.activity.HomeActivity

class RegisterFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Initialize the database
        database = AppDatabase.getDatabase(requireActivity())
        dao = database.appDao()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.ivBack.setOnClickListener {

            requireActivity().finish()

        }

        binding.tvLogin.setOnClickListener {

            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)


        }

        binding.btnRegister.setOnClickListener {

            val email = binding.etMail.text.toString()
            val password = binding.etPas.text.toString()
            val firstName = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()
            val location = getStringData("adress", "")

            registerUser(email, password, firstName, phone, location!!)

        }

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

    private fun registerUser(
        email: String,
        password: String,
        firstName: String,
        phone: String,
        location: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = hashMapOf(
                        "firstName" to firstName,
                        "email" to email,
                        "phone" to phone,
                        "location" to location
                    )

                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {

                                CoroutineScope(Dispatchers.IO).launch {

                                    val data = UserData(
                                        name = firstName,
                                        email = email,
                                        number = phone,
                                        locate = location,

                                        longtitude = getStringData("long", "")!!,
                                        latitude = getStringData("lat", "")!!
                                    )
                                    dao.insertUser(data)

                                    saveData("is_reg", "reg")

                                    val users = dao.getAllUsers()
                                    Log.d(
                                        "MainActivity123",
                                        "Users: ${users.joinToString { "${it.name} (${it.email})" }}"
                                    )


                                }

//                                Toast.makeText(requireActivity(), "CLICK ZAYBAL BLAA ISHLAMAYABDI", Toast.LENGTH_SHORT).show()

                                startActivity(
                                    Intent(
                                        requireActivity(),
                                        HomeActivity::class.java
                                    )
                                )
                                requireActivity().finish()
                                Toast.makeText(
                                    requireContext(),
                                    "User registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Error saving user data: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


}