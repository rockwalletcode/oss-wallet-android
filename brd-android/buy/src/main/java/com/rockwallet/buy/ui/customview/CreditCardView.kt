package com.rockwallet.buy.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.buy.databinding.ViewCreditCardBinding
import com.rockwallet.common.utils.dp

class CreditCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: ViewCreditCardBinding

    init {
        binding = ViewCreditCardBinding.inflate(LayoutInflater.from(context), this)
        setPaddingRelative(12.dp, 8.dp, 12.dp, 8.dp)
    }

    fun setPaymentInstrument(instrument: PaymentInstrument.Card?) {
        with(binding) {
            tvDate.text = instrument?.expiryDate
            tvCardNumber.text = instrument?.hiddenCardNumber
            ivCardLogo.setImageResource(instrument?.cardTypeIcon ?: 0)
        }
    }
}

