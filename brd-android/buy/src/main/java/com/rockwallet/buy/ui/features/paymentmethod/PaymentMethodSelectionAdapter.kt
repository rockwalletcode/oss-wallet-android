package com.rockwallet.buy.ui.features.paymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rockwallet.common.data.model.PaymentInstrument
import com.rockwallet.buy.databinding.ListItemPaymentMethodBinding

class PaymentMethodSelectionAdapter(
    private val clickCallback: (PaymentInstrument.Card) -> Unit,
    private val optionsClickCallback: (PaymentInstrument.Card) -> Unit
) : ListAdapter<PaymentInstrument.Card, PaymentMethodSelectionAdapter.ViewHolder>(PaymentInstrumentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPaymentMethodBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            item = getItem(position),
            clickCallback = clickCallback,
            optionsClickCallback = optionsClickCallback
        )
    }

    class ViewHolder(val binding: ListItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PaymentInstrument.Card, clickCallback: (PaymentInstrument.Card) -> Unit, optionsClickCallback: (PaymentInstrument.Card) -> Unit) {
            with(binding) {
                tvDate.text = item.expiryDate
                tvCardNumber.text = item.hiddenCardNumber
                ivCardLogo.setImageResource(item.cardTypeIcon)
                root.setOnClickListener { clickCallback(item) }
                btnMore.setOnClickListener { optionsClickCallback(item) }
            }
        }
    }

    object PaymentInstrumentDiffCallback : DiffUtil.ItemCallback<PaymentInstrument.Card>() {
        override fun areItemsTheSame(oldItem: PaymentInstrument.Card, newItem: PaymentInstrument.Card) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: PaymentInstrument.Card, newItem: PaymentInstrument.Card) =
            oldItem.last4Numbers == newItem.last4Numbers &&
                    oldItem.expiryMonth == newItem.expiryMonth &&
                    oldItem.expiryYear == newItem.expiryYear
    }
}