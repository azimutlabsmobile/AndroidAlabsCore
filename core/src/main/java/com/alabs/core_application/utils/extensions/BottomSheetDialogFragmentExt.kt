package com.alabs.core_application.utils.extensions

import android.app.Dialog
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.alabs.core_application.R
import java.lang.NullPointerException

/**
 * Открытие BottomSheetDialog в полный экран
 * Пример :
 *  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
 *       val dialog: Dialog = super.onCreateDialog(savedInstanceState)
 *       return showFullScreen(dialog)
 *    }
 *
 * @param [Dialog] берем из super.onCreateDialog(savedInstanceState)
 */
fun BottomSheetDialogFragment.showFullScreen(dialog: Dialog): Dialog {
    dialog.setOnShowListener { dialogInterface ->
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        if (layoutParams != null) {
            layoutParams.height = height
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    return dialog
}


/**
 * Запрешает перетаскивать диалог жестами
 * Пример : override fun onCreateDialog(savedInstanceState: Bundle?) = banDragDialog()
 */
fun BottomSheetDialogFragment.banDragDialog(): BottomSheetDialog {
    return BottomSheetDialog(this.context ?: throw NullPointerException(), theme).apply {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        })
    }
}