package com.friney.fairsplit.ui.navigation

import android.os.Bundle
import androidx.navigation.NavController

interface FragmentNavigator {

    fun navigateToLogin()

    fun setNavController(navController: NavController)

    fun navigateMainToDetailsEvent(bundle: Bundle)

    fun navigateMainToCreateEvent()

    fun navigateMainToDetailsReceipt(bundle: Bundle)

    fun navigateBack()

    fun navigateToRegister()

    fun navigateToMain()
}