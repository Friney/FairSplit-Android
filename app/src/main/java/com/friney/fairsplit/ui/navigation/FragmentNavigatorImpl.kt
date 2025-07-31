package com.friney.fairsplit.ui.navigation

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import com.friney.fairsplit.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FragmentNavigatorImpl @Inject constructor() : FragmentNavigator {

    private var navController: NavController? = null

    override fun setNavController(navController: NavController) {
        Log.i("FragmentNavigator", "NavController set")
        this.navController = navController
    }

    override fun navigateToLogin() {
        Log.i("FragmentNavigator", "Navigating to login")
        navController?.navigate(R.id.loginFragment)
    }

    override fun navigateMainToDetailsEvent(bundle: Bundle) {
        Log.i("FragmentNavigator", "Navigating to details event")
        navController?.navigate(
            R.id.action_mainFragment_to_detailsEventFragment,
            bundle
        )
    }

    override fun navigateMainToCreateEvent() {
        Log.i("FragmentNavigator", "Navigating to create event")
        navController?.navigate(R.id.action_mainFragment_to_createEventFragment)
    }

    override fun navigateMainToDetailsReceipt(bundle: Bundle) {
        Log.i("FragmentNavigator", "Navigating to details receipt")
        navController?.navigate(
            R.id.action_detailsEventFragment_to_detailsReceiptFragment,
            bundle
        )
    }

    override fun navigateBack() {
        Log.i("FragmentNavigator", "Navigating back")
        navController?.navigateUp()
    }

    override fun navigateToRegister() {
        Log.i("FragmentNavigator", "Navigating to register")
        navController?.navigate(R.id.registerFragment)
    }

    override fun navigateToMain() {
        Log.i("FragmentNavigator", "Navigating to main")
        navController?.navigate(R.id.mainFragment)
    }
}