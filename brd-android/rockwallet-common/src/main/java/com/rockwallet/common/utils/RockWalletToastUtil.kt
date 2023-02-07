package com.rockwallet.common.utils

import android.os.Build
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.setPadding
import com.rockwallet.common.R
import com.google.android.material.snackbar.Snackbar

object RockWalletToastUtil {

    private const val DURATION = 7_000

    fun showInfo(parentView: View, message: CharSequence) {
        showCustomSnackBar(
            parentView = parentView,
            message = message,
            background = R.drawable.bg_info_bubble,
            textAppearance = R.style.RockWalletToastTextAppearance_Info
        )
    }

    fun showError(parentView: View, message: CharSequence) {
        showCustomSnackBar(
            parentView = parentView,
            message = message,
            background = R.drawable.bg_error_bubble,
            textAppearance = R.style.RockWalletToastTextAppearance_Error
        )
    }

    private fun showCustomSnackBar(
        parentView: View,
        message: CharSequence,
        gravity: Int = Gravity.TOP,
        @DrawableRes background: Int,
        @StyleRes textAppearance: Int,
    ) {
        val view = TextView(parentView.context).apply {
            text = message
            setPadding(16.dp)
            setTextAppearance(textAppearance)
            movementMethod = LinkMovementMethod.getInstance()
        }

        val snackBar = Snackbar.make(parentView, "", DURATION).apply {
            this.view.setBackgroundResource(background)
        }

        val topMargin = when {
            gravity == Gravity.TOP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ->
                parentView.rootWindowInsets?.displayCutout?.safeInsetTop ?: 0
            else -> 0
        }

        // setup snackBar view
        (snackBar.view as Snackbar.SnackbarLayout).let {
            val params = it.layoutParams as ViewGroup.LayoutParams
            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            if (params is CoordinatorLayout.LayoutParams) {
                params.gravity = gravity
                params.topMargin = topMargin
            } else if (params is FrameLayout.LayoutParams) {
                params.gravity = gravity
                params.topMargin = topMargin
            }

            it.layoutParams = params
            it.addView(view, 0)
            snackBar.show()
        }
    }
}