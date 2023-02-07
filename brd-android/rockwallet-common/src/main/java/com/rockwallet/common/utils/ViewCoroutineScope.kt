package com.rockwallet.common.utils

import android.view.View
import com.rockwallet.common.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

val View.viewScope: CoroutineScope
    get() {
        val storedScope = getTag(R.string.view_coroutine_scope) as? CoroutineScope
        if (storedScope != null) return storedScope

        val newScope = ViewCoroutineScope()
        if (isAttachedToWindow) {
            addOnAttachStateChangeListener(newScope)
            setTag(R.string.view_coroutine_scope, newScope)
        } else newScope.cancel()

        return newScope
    }

private class ViewCoroutineScope : CoroutineScope, View.OnAttachStateChangeListener {
    override val coroutineContext = SupervisorJob() + Dispatchers.Main

    override fun onViewAttachedToWindow(view: View) = Unit

    override fun onViewDetachedFromWindow(view: View) {
        coroutineContext.cancel()
        view.setTag(R.string.view_coroutine_scope, null)
    }
}