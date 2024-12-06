package com.example.xpensate.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.test.isSelected
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.ActivityHomeBinding
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var selectedModelId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                when (destination.id) {
                    R.id.blankFragment -> binding.bottomNavigation.show(0)
                    R.id.debtsAndLends -> binding.bottomNavigation.show(1)
                    R.id.bill_container -> binding.bottomNavigation.show(2)
                    R.id.tripTrackerDashBoard -> binding.bottomNavigation.show(3)
                }
                if (restrictToolBar(destination.id)) {
                    supportActionBar?.hide()
                    binding.bottomNavigation.visibility = View.VISIBLE
                } else if (restrictNavBar(destination.id)) {
                    supportActionBar?.show()
                    binding.bottomNavigation.visibility = View.GONE
                } else if (restrictBoth(destination.id)) {
                    supportActionBar?.hide()
                    binding.bottomNavigation.visibility = View.GONE
                } else {
                    supportActionBar?.show()
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }

        val bottomNav: CurvedBottomNavigation = binding.bottomNavigation

        bottomNav.add(CurvedBottomNavigation.Model(0, "Home", R.drawable.home_nav))
        bottomNav.add(CurvedBottomNavigation.Model(1, "Debts", R.drawable.debts_nav))
        bottomNav.add(CurvedBottomNavigation.Model(2, "add", R.drawable.add))
        bottomNav.add(CurvedBottomNavigation.Model(3, "Trip Tracker", R.drawable.trip_nav))
        bottomNav.add(CurvedBottomNavigation.Model(4, "Profile", R.drawable.profile_nav))


        bottomNav.setOnClickMenuListener { model ->
            if (selectedModelId != model.id) {
                selectedModelId = model.id
                navigateToDestination(model.id, navController)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun navigateToDestination(modelId: Int, navController: NavController?) {
        when (modelId) {
            0 -> {
                if (navController?.currentDestination?.id != R.id.blankFragment) {
                    navController?.navigate(R.id.blankFragment)
                }
            }

            1 -> {
                if (navController?.currentDestination?.id != R.id.debtsAndLends) {
                    navController?.navigate(R.id.debtsAndLends)
                }
            }

            2 -> {
                if (navController?.currentDestination?.id != R.id.bill_container) {
                    navController?.navigate(R.id.bill_container)
                }
            }

            3 -> {
                if (navController?.currentDestination?.id != R.id.tripTrackerDashBoard) {
                    navController?.navigate(R.id.tripTrackerDashBoard)
                }
            }

            4 -> {
                if (navController?.currentDestination?.id != R.id.profile2) {
                    navController?.navigate(R.id.profile2)
                }
            }

            5 -> {
                if (navController?.currentDestination?.id != R.id.preferredCurrency) {
                    navController?.navigate(R.id.preferredCurrency)
                }
            }
        }
    }



    private fun restrictToolBar(destinationId: Int): Boolean {
        val restrictToolBar = setOf(
            R.id.addTripMember,
            R.id.removeFromTrip,
            R.id.addSplit,
            R.id.selectedTripDetails,
            R.id.bill_container,
            R.id.blankFragment,
            R.id.splitBillMore,
            R.id.splitAmountPage,
            R.id.addMember,
            R.id.splitBillGroupShow,
            R.id.addSplit,
            R.id.updateGroup,
            R.id.addMember,
            R.id.selectedTripDetails,
            R.id.addTripMember,
            R.id.addSplit,
            R.id.profile2,
            R.id.split_bill2

        )
        return destinationId in restrictToolBar
    }

    private fun restrictNavBar(destinationId: Int): Boolean {
        val visibleFragments = setOf(
            R.id.updateContact,
            R.id.appLock
        )
        return destinationId in visibleFragments
    }
    private fun restrictBoth(destinationId: Int): Boolean {
        val restrictBoth = setOf(
            R.id.login2,
            R.id.sign_up,
            R.id.reset,
            R.id.verifyReset,
            R.id.slider,
            R.id.started_1,
            R.id.started_2,
            R.id.started_3,
            R.id.started_4,
            R.id.started_5,
            R.id.verify,
            R.id.splashScreen
        )
        return destinationId in restrictBoth
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

        if (navController.currentDestination?.id == R.id.preferredCurrency) {
            navController.navigate(R.id.profile2)
            return true
        }

        return NavigationUI.navigateUp(navController, null) || super.onSupportNavigateUp()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}