package com.rockwallet.buy.ui.features.paymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.rockwallet.buy.databinding.BottomSheetPaymentMethodOptionsBinding
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.common.ui.dialog.RockWalletBottomSheet

class PaymentMethodOptionsBottomSheet: RockWalletBottomSheet<BottomSheetPaymentMethodOptionsBinding>() {

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?, attach: Boolean) =
        BottomSheetPaymentMethodOptionsBinding.inflate(inflater, container, attach)

    override fun setupBottomSheet() {
        binding.btnCancel.setOnClickListener { dismissWithResult(REQUEST_KEY, RESULT_KEY_CANCEL) }
        binding.btnRemove.setOnClickListener { dismissWithResult(REQUEST_KEY, RESULT_KEY_REMOVE) }
    }

    companion object {
        const val REQUEST_KEY = "PaymentMethodOptionsBottomSheet"
        const val RESULT_KEY_CANCEL = "PaymentMethodOptionsBottomSheet_cancel"
        const val RESULT_KEY_REMOVE = "PaymentMethodOptionsBottomSheet_remove"
        const val EXTRA_PAYMENT_INSTRUMENT = "PaymentMethodOptionsBottomSheet_paymentInstrument"

        fun newInstance(paymentInstrument: PaymentInstrument) = PaymentMethodOptionsBottomSheet().apply {
            arguments = bundleOf(
                EXTRA_PAYMENT_INSTRUMENT to paymentInstrument
            )
        }
    }
}