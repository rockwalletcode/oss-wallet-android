package com.rockwallet.buy.utils

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View

object SpannableHelper {

    fun clickableText(fullText: String, linkText: String, boldTexts: Array<String> = emptyArray(), action: () -> Unit): CharSequence {
        val spannable = SpannableString(fullText)

        for (bold in boldTexts) {
            val startIndex = fullText.indexOf(bold)
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), startIndex, startIndex + bold.length, 0
            )
        }

        val startIndex = fullText.indexOf(linkText)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                action()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
            }
        }, startIndex, startIndex + linkText.length, 0)


        return spannable
    }
}