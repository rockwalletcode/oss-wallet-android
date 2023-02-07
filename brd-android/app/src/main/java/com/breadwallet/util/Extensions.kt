package com.breadwallet.util

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.bluelinelabs.conductor.Router
import com.breadwallet.ui.BaseController
import com.breadwallet.ui.navigation.fragmentManager
import com.rockwallet.common.ui.dialog.RockWalletGenericDialog
import com.rockwallet.common.ui.dialog.RockWalletGenericDialogArgs

fun BaseController.registerForGenericDialogResult(requestKey: String, callback: (String?, Bundle) -> Unit) {
    router.fragmentManager()?.setFragmentResultListener(requestKey, activity as LifecycleOwner) { _, bundle ->
        val resultKey = bundle.getString(RockWalletGenericDialog.EXTRA_RESULT)
        callback(resultKey, bundle)
    }
}

fun Router.showRokWalletGenericDialog(args: RockWalletGenericDialogArgs) {
    fragmentManager()?.let { fm ->
        RockWalletGenericDialog.newInstance(args)
            .show(fm)
    }
}