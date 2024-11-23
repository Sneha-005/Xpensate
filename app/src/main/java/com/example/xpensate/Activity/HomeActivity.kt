package com.example.xpensate.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        AuthInstance.init(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController

        if (navController != null) {
            setupActionBarWithNavController(navController)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.blankFragment) {
                    supportActionBar?.hide()
                    binding.bottomNavigation.visibility = View.VISIBLE
                } else if (isLaterFragment(destination.id)) {
                    supportActionBar?.hide()
                    binding.bottomNavigation.visibility = View.GONE
                } else {
                    supportActionBar?.show()
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }

        val bottomNav: BottomNavigationView = binding.bottomNavigation
        if (navController != null) {
            bottomNav.setupWithNavController(navController)
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.blankFragment -> {
                    if (navController?.currentDestination?.id != R.id.blankFragment) {

                        navController?.navigate(R.id.blankFragment)
                    }
                    true
                }
                R.id.profile2 -> {
                    if (navController?.currentDestination?.id != R.id.profile2) {
                        navController?.navigate(R.id.profile2)
                    }
                    true
                }
                R.id.bill_container -> {
                    if (navController?.currentDestination?.id != R.id.profile2) {
                        navController?.navigate(R.id.bill_container)
                    }
                    true
                }
                R.id.currencyConverter -> {
                    if (navController?.currentDestination?.id != R.id.currencyConverter) {
                        navController?.navigate(R.id.currencyConverter)
                    }
                    true
                }
                R.id.debtsAndLends -> {
                    if (navController?.currentDestination?.id != R.id.currencyConverter) {
                        navController?.navigate(R.id.debtsAndLends)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun isLaterFragment(destinationId: Int): Boolean {
        val visibleFragments = setOf(
            R.id.blankFragment,
            R.id.login2,
            R.id.splashScreen,
            R.id.sign_up,
            R.id.started_1,
            R.id.started_2,
            R.id.started_3,
            R.id.started_4,
            R.id.started_5,
            R.id.reset,
            R.id.verify,
            R.id.slider,
            R.id.verifyReset
        )
        return destinationId in visibleFragments
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

        if (navController.currentDestination?.id?.let { onBackFromToolBar(it)} == true ) {
            navController.navigate(R.id.profile2)
            return true
        }
        else{
            navController.navigate(R.id.blankFragment)
            return true
        }

        return NavigationUI.navigateUp(navController, null) || super.onSupportNavigateUp()
    }
    private fun onBackFromToolBar(destinationId: Int): Boolean {
        val visibleFragments = setOf(
            R.id.blankFragment,
            R.id.preferredCurrency,
            R.id.appLock,
            R.id.updateContact
        )
        return destinationId in visibleFragments
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
