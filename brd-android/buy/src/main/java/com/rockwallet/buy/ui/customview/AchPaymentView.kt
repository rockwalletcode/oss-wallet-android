package com.rockwallet.buy.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.rockwallet.buy.R
import com.rockwallet.buy.databinding.ViewAchPaymentBinding
import com.rockwallet.common.data.model.PaymentInstrument
import com.google.android.material.card.MaterialCardView

class AchPaymentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding: ViewAchPaymentBinding

    init {
        binding = ViewAchPaymentBinding.inflate(LayoutInflater.from(context), this, true)
        setContentPadding(0, 0, 0, 0)
        parseAttrs(attrs)
    }

    fun setTitle(title: String?) {
        binding.tvTitle.text = title
    }

    fun setPaymentInstrument(paymentInstrument: PaymentInstrument.BankAccount?) {
        binding.tvAccountNumber.text = paymentInstrument?.accountName ?: context.getString(R.string.Buy_linkBankAccount)
        binding.ivChevron.isVisible = paymentInstrument == null
        binding.ivLogo.isVisible = paymentInstrument != null
        isEnabled = paymentInstrument == null
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AchPaymentView)
        setTitle(typedArray.getString(R.styleable.AchPaymentView_title))
        typedArray.recycle()
    }
}

