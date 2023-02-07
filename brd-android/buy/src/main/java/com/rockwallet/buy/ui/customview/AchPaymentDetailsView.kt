package com.rockwallet.buy.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.rockwallet.buy.databinding.ViewAchPaymentDetailsBinding
import com.rockwallet.common.data.model.PaymentInstrument

class AchPaymentDetailsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: ViewAchPaymentDetailsBinding

    init {
        binding = ViewAchPaymentDetailsBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setTitle(title: String?) {
        binding.tvTitle.text = title
    }

    @SuppressLint("SetTextI18n")
    fun setPaymentInstrument(paymentInstrument: PaymentInstrument.BankAccount) {
        binding.tvTitle.text = paymentInstrument.accountName
        binding.tvSubtitle.text = "**** **** **** **** ${paymentInstrument.last4Numbers}"
    }
}

