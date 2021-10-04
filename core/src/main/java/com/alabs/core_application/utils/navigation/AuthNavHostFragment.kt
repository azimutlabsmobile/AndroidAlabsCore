package com.alabs.core_application.utils.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.navigation.NavController
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.alabs.core_application.R
import com.alabs.core_application.data.constants.CoreConstant
import org.koin.core.KoinComponent


class AuthNavHostFragment : NavHostFragment(), KoinComponent {

    private var pathAuthActivity = CoreConstant.EMPTY

    companion object {
        var isUserSuccessAuthorization = false
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val navHost = context.obtainStyledAttributes(
            attrs,
            R.styleable.AuthNavHostFragment
        )
        pathAuthActivity =
        navHost.getString(R.styleable.AuthNavHostFragment_pathAuthActivity).orEmpty()
        navHost.recycle()
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreateNavController(navController: NavController) {
        navController.navigatorProvider.addNavigator(
            DialogFragmentNavigator(
                requireContext(),
                childFragmentManager
            )
        )
        navController.navigatorProvider.addNavigator(createFragmentNavigator())
        navController.navigatorProvider.addNavigator(AuthFragmentNavigator(requireActivity(), childFragmentManager, id, pathAuthActivity))
        navController.navigatorProvider.addNavigator(AuthActivityNavigator(requireActivity(), pathAuthActivity))
    }
}