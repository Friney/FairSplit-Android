package com.friney.fairsplit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.friney.fairsplit.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationMenu.setupWithNavController(navController)
//        binding.bottomNavigationMenu.setupWithNavController(
//            navController = binding.navigationHostFragment.findNavController()
//        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}