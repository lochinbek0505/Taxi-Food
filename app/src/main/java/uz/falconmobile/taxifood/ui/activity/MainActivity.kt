package uz.falconmobile.taxifood.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import uz.falconmobile.taxifood.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_sign_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}