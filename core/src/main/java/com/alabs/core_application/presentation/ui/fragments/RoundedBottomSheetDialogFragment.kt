package com.alabs.core_application.presentation.ui.fragments

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.alabs.core_application.R

open class RoundedBottomSheetDialogFragment(
    private val style: Int = R.style.BottomSheetDialogTheme
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = style

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)
}