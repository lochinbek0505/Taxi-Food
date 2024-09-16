package uz.falconmobile.taxifood.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.falconmobile.taxifood.R
import uz.falconmobile.taxifood.adapter.ViewPagerAdapter
import uz.falconmobile.taxifood.databinding.ActivityWishlistBinding
import uz.falconmobile.taxifood.ui.fragment.FavoriteFoodFragment
import uz.falconmobile.taxifood.ui.fragment.FavoriteRestouranFragment

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // To make our toolbar show the application
        // we need to give it to the ActionBar
        setSupportActionBar(binding.toolbar)

        // Initializing the ViewPagerAdapter
        val adapter = ViewPagerAdapter(supportFragmentManager)

        // add fragment to the list
        adapter.addFragment(FavoriteRestouranFragment(), "Restourans")
        adapter.addFragment(FavoriteFoodFragment(), "Foods")

        // Adding the Adapter to the ViewPager
        binding.viewPager.adapter = adapter

        // bind the viewPager with the TabLayout.
        binding.tabs.setupWithViewPager(binding.viewPager)

    }
}