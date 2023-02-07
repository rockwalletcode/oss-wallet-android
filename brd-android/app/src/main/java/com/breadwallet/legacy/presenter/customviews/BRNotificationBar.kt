package com.breadwallet.legacy.presenter.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.breadwallet.R
import com.rockwallet.common.utils.underline

class BRNotificationBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    private var btnDismiss: ImageButton
    private var btnContinue: Button
    private var tvTitle: TextView
    private var tvDescription: TextView

    private var callback: Callback? = null

    init {
        inflate(context, R.layout.notification_bar, this)

        tvTitle = findViewById(R.id.tv_title)
        tvDescription = findViewById(R.id.tv_description)
        btnDismiss = findViewById(R.id.btn_dismiss)
        btnContinue = findViewById(R.id.btn_continue)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.BRNotificationBar)
        val title = attributes.getString(R.styleable.BRNotificationBar_title)
        val action = attributes.getString(R.styleable.BRNotificationBar_action)
        val description = attributes.getString(R.styleable.BRNotificationBar_text)
        attributes.recycle()

        tvTitle.text = title
        btnContinue.text = action
        tvDescription.text = description

        btnContinue.underline()

        btnDismiss.setOnClickListener { this.visibility = GONE }
        btnContinue.setOnClickListener {
            callback?.onActionClicked()
            this.visibility = GONE
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun onActionClicked()
    }
}