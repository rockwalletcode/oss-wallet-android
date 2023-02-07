package com.breadwallet.ui.wallet

import android.content.res.ColorStateList
import android.text.format.DateUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.breadwallet.R
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.databinding.ItemSwapDetailsBinding
import com.breadwallet.databinding.TxItemBinding
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.util.BRDateUtil
import com.breadwallet.util.formatFiatForUi
import com.breadwallet.util.formatFiatForUiAdvanced
import com.breadwallet.util.isBitcoinLike
import com.rockwallet.trade.data.response.ExchangeOrderStatus.*
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem

private const val DP_120 = 120
private const val PROGRESS_FULL = 100

class TransactionListItem(
    transaction: WalletTransaction,
    var isCryptoPreferred: Boolean
) : ModelAbstractItem<WalletTransaction, TransactionListItem.ViewHolder>(transaction) {

    override val layoutRes: Int =
        if (model.exchangeData != null) R.layout.item_swap_details else
            R.layout.tx_item

    override val type: Int = if (model.exchangeData != null) R.id.swap_transaction_item else
        R.id.transaction_item

    override var identifier: Long = model.txHash.hashCode().toLong()

    override fun getViewHolder(v: View) = ViewHolder(v)

    inner class ViewHolder(
        v: View
    ) : FastAdapter.ViewHolder<TransactionListItem>(v) {

        private var binding: ViewBinding? = null

        override fun bindView(item: TransactionListItem, payloads: List<Any>) {
            if (item.model.exchangeData != null) {
                binding = ItemSwapDetailsBinding.bind(itemView)
                setSwapBuyContent(binding as ItemSwapDetailsBinding, item.model, item.model.exchangeData!!)
            } else {
                binding = TxItemBinding.bind(itemView)
                setTransferContent(binding as TxItemBinding, item.model, item.isCryptoPreferred)
            }
        }

        override fun unbindView(item: TransactionListItem) {
            val binding = this.binding

            // unbind ItemSwapDetailsBinding
            if(binding is ItemSwapDetailsBinding) {
                binding.tvTransactionDate.text = null
                binding.tvTransactionValue.text = null
                binding.tvTransactionTitle.text = null
            }

            // unbind TxItemBinding
            if(binding is TxItemBinding) {
                binding.txTitle.text = null
                binding.txAmount.text = null
                binding.txDescriptionValue.text = null
                binding.txDescriptionLabel.text = null
            }

            this.binding = null
        }

        private fun setSwapBuyContent(
            binding: ItemSwapDetailsBinding, transaction: WalletTransaction, exchangeData: ExchangeData
        ) {
            val context = itemView.context

            with(binding) {
                if (exchangeData.transactionData.exchangeStatus == REFUNDED) {
                    ivIcon.setImageResource(R.drawable.ic_transaction_refunded) }
                else {
                    ivIcon.setImageResource(exchangeData.getIcon())
                }
                tvTransactionDate.text = BRDateUtil.getShortDate(transaction.timeStamp)
                tvTransactionTitle.text = exchangeData.getTransactionTitle(context)
                tvTransactionValue.text = transaction.amount.formatCryptoForUi(transaction.currencyCode)
            }

            when (exchangeData.transactionData.exchangeStatus) {
                COMPLETE, MANUALLY_SETTLED ->
                    setIconColors(binding, R.color.light_success_2, R.color.light_success_1)
                FAILED, REFUNDED ->
                    setIconColors(binding, R.color.light_error_2, R.color.light_error_1)
                PENDING ->
                    setIconColors(binding, R.color.light_pending_2, R.color.light_pending_1)
                else -> Unit
            }
        }

        private fun setIconColors(binding: ItemSwapDetailsBinding, backgroundColor: Int, iconColor: Int) {
            val context = binding.root.context

            binding.ivIconBg.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, backgroundColor))
            binding.ivIcon.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, iconColor))
        }

        @Suppress("LongMethod", "ComplexMethod")
        private fun setTransferContent(
            binding: TxItemBinding,
            transaction: WalletTransaction,
            isCryptoPreferred: Boolean
        ) {
            val context = itemView.context
            val commentString = transaction.memo
            val received = transaction.isReceived

            binding.ivIcon.setImageResource(
                when {
                    transaction.progress < PROGRESS_FULL ->
                        if (received) R.drawable.transfer_receive_in_progress else R.drawable.transfer_send_in_progress
                    transaction.isErrored ->
                        if (received) R.drawable.transfer_receive_failed else R.drawable.transfer_failed
                    transaction.isComplete ->
                        if (received) R.drawable.transfer_receive else R.drawable.transfer_send
                    else -> R.drawable.transfer_send
                }
            )

            val preferredCurrencyCode = when {
                isCryptoPreferred -> transaction.currencyCode
                else -> BRSharedPrefs.getPreferredFiatIso()
            }
            val amount = when {
                isCryptoPreferred -> transaction.amount
                else -> transaction.amountInFiat
            }.run { if (received) this else negate() }

            val formattedAmount = when {
                isCryptoPreferred -> amount.formatCryptoForUi(preferredCurrencyCode)
                else -> amount.formatFiatForUi(preferredCurrencyCode)
            }

            binding.txAmount.text = formattedAmount

           when {
                !transaction.gift?.recipientName.isNullOrBlank() -> {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_toRecipient, "")
                    binding.txDescriptionValue.text = transaction.gift?.recipientName
                }
                transaction.isStaking -> {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_stakingTo, "")
                    binding.txDescriptionValue.text = transaction.toAddress
                }
                commentString == null -> {
                    binding.txDescriptionLabel.text = ""
                    binding.txDescriptionValue.text = ""
                }
                commentString.isNotEmpty() -> {
                    binding.txDescriptionLabel.text = ""
                    binding.txDescriptionValue.text = commentString
                }
                transaction.isFeeForToken -> {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_tokenTransfer, transaction.feeToken)
                    binding.txDescriptionValue.text = commentString
                }
                received -> {
                    if (transaction.isComplete) {
                        if (transaction.currencyCode.isBitcoinLike()) {
                            binding.txDescriptionLabel.text =
                                context.getString(R.string.TransactionDetails_receivedVia, "")
                            binding.txDescriptionValue.text = transaction.toAddress
                        } else {
                            binding.txDescriptionLabel.text =
                                context.getString(R.string.TransactionDetails_receivedFrom, "")
                            binding.txDescriptionValue.text = transaction.fromAddress
                        }
                    } else {
                        if (transaction.currencyCode.isBitcoinLike()) {
                            binding.txDescriptionLabel.text =
                                context.getString(R.string.TransactionDetails_receivingVia, "")
                            binding.txDescriptionValue.text = transaction.toAddress
                        } else {
                            binding.txDescriptionLabel.text =
                                context.getString(R.string.TransactionDetails_receivingFrom, "")
                            binding.txDescriptionValue.text = transaction.fromAddress
                        }
                    }
                }
                else -> if (transaction.isComplete) {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_sentTo, "")
                    binding.txDescriptionValue.text = transaction.toAddress
                } else {
                    binding.txDescriptionLabel.text =
                        context.getString(R.string.Transaction_sendingTo, "")
                    binding.txDescriptionValue.text = transaction.toAddress
                }
            }

            val timeStamp = transaction.timeStamp
            binding.txTitle.text = when {
                timeStamp == 0L || transaction.isPending -> buildString {
                    append(transaction.confirmations)
                    append('/')
                    append(transaction.confirmationsUntilFinal)
                    append(' ')
                    append(context.getString(R.string.TransactionDetails_confirmationsLabel))
                }
                DateUtils.isToday(timeStamp) -> BRDateUtil.getTime(timeStamp)
                else -> BRDateUtil.getShortDate(timeStamp)
            }

            // If this transaction failed, show the "FAILED" indicator in the cell
            if (transaction.isErrored) {
                showTransactionFailed()
            }
        }

        private fun showTransactionFailed() {
            val binding = TxItemBinding.bind(itemView)
            with(binding) {
                txTitle.setText(R.string.Transaction_failed)
            }
        }
    }
}
