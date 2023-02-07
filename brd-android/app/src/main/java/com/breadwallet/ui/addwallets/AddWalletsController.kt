/**
 * BreadWallet
 *
 * Created by Ahsan Butt <ahsan.butt@breadwallet.com> on 10/11/19.
 * Copyright (c) 2019 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.ui.addwallets

import android.graphics.Rect
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadwallet.R
import com.breadwallet.databinding.ControllerAddWalletsBinding
import com.breadwallet.tools.util.Utils
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.ViewEffect
import com.breadwallet.ui.addwallets.AddWallets.E
import com.breadwallet.ui.addwallets.AddWallets.F
import com.breadwallet.ui.addwallets.AddWallets.M
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.flowbind.textChanges
import com.rockwallet.common.ui.dialog.RockWalletGenericDialog
import com.rockwallet.common.ui.dialog.RockWalletGenericDialogArgs
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.kodein.di.direct
import org.kodein.di.erased.instance

class AddWalletsController : BaseMobiusController<M, E, F>() {

    private val binding by viewBinding(ControllerAddWalletsBinding::inflate)

    override val defaultModel = M.createDefault()
    override val init = AddWalletsInit
    override val update = AddWalletsUpdate
    override val flowEffectHandler
        get() = createAddWalletsHandler(
            checkNotNull(applicationContext),
            direct.instance(),
            direct.instance()
        )

    override fun onDetach(view: View) {
        super.onDetach(view)
        Utils.hideKeyboard(activity)
    }

    override fun handleViewEffect(effect: ViewEffect) {
        when (effect) {
            is F.ShowLimitedAssetsDialog -> showLimitedAssetsDialog()
        }
    }

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with(binding) {
            tokenList.layoutManager = LinearLayoutManager(checkNotNull(activity))
            searchEdit.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    Utils.hideKeyboard(activity)
                }
            }

            merge(
                searchEdit.textChanges().map { E.OnSearchQueryChanged(it) },
                closeButton.clicks().map { E.OnBackClicked },
                tvFooter.clicks().map { E.OnFooterClicked },
                bindTokenList(modelFlow)
            )
        }
    }

    private fun bindTokenList(
        modelFlow: Flow<M>
    ) = callbackFlow<E> {
        val adapter = AddTokenListAdapter(
            tokensFlow = modelFlow
                .map { model -> model.tokens }
                .distinctUntilChanged(),
            sendChannel = channel
        )
        binding.tokenList.adapter = adapter

        val marginBetweenItems = binding.tokenList.context.resources.getDimensionPixelOffset(
            R.dimen.add_wallet_item_margin
        )

        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.top = marginBetweenItems
            }
        }

        binding.tokenList.addItemDecoration(itemDecoration)

        awaitClose {
            binding.tokenList.adapter = null
            binding.tokenList.removeItemDecoration(itemDecoration)
        }
    }

    private val LIMITED_ASSETS_DIALOG_POSITIVE = "limited_assets_dialog"
    private val LIMITED_ASSETS_DIALOG_REQUEST = "limited_assets_dialog"

    private fun showLimitedAssetsDialog() {
        val act = checkNotNull(activity)
        val args = RockWalletGenericDialogArgs(
            titleRes = R.string.AddWallet_limitedAssetsDialogTitle,
            descriptionRes = R.string.AddWallet_limitedAssetsDialogMessage,
            positive = RockWalletGenericDialogArgs.ButtonData(
                titleRes = R.string.Button_ok,
                resultKey = LIMITED_ASSETS_DIALOG_POSITIVE
            ),
            requestKey = LIMITED_ASSETS_DIALOG_REQUEST
        )

        RockWalletGenericDialog.newInstance(args)
            .show((act as FragmentActivity).supportFragmentManager)
    }
}
