/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 10/10/19.
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
package com.breadwallet.ui.showkey

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import com.breadwallet.R
import com.breadwallet.databinding.ControllerPaperKeyBinding
import com.breadwallet.databinding.FragmentWordItemBinding
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.ui.BaseController
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.navigation.OnCompleteAction
import com.breadwallet.ui.showkey.ShowPaperKey.E
import com.breadwallet.ui.showkey.ShowPaperKey.F
import com.breadwallet.ui.showkey.ShowPaperKey.M
import com.breadwallet.util.DefaultOnPageChangeListener
import com.rockwallet.common.utils.dp
import com.rockwallet.common.utils.underline
import drewcarlson.mobius.flow.subtypeEffectHandler
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.lang.ref.WeakReference

class ShowPaperKeyController(args: Bundle) : BaseMobiusController<M, E, F>(args) {

    companion object {
        private const val EXTRA_PHRASE = "phrase"
        private const val EXTRA_ON_COMPLETE = "on-complete"
    }

    constructor(
        phrase: List<String>,
        onComplete: OnCompleteAction? = null
    ) : this(
        bundleOf(
            EXTRA_PHRASE to phrase,
            EXTRA_ON_COMPLETE to onComplete?.name
        )
    )

    private val phrase: List<String> = arg(EXTRA_PHRASE)
    private val onComplete = argOptional<String>(EXTRA_ON_COMPLETE)
        ?.run(OnCompleteAction::valueOf)

    override val defaultModel = M.createDefault(phrase, onComplete, BRSharedPrefs.phraseWroteDown)
    override val update = ShowPaperKeyUpdate
    override val flowEffectHandler = subtypeEffectHandler<F, E> { }

    private val binding by viewBinding(ControllerPaperKeyBinding::inflate)

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with (binding) {
            btnSkip.underline()

            val paddingHorizontal = getScreenWidth() / 4
            wordsPager.clipToPadding = false
            wordsPager.setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
            wordsPager.setPageTransformer(false, WordPageTransformer(wordsPager))

            merge(
                btnDone.clicks().map { E.OnDoneClicked },
                btnBack.clicks().map { E.OnBackClicked },
                btnSkip.clicks().map { E.OnSkipClicked },
                callbackFlow<E> {
                    val channel = channel
                    val listener = object : DefaultOnPageChangeListener() {
                        override fun onPageSelected(position: Int) {
                            channel.trySend(E.OnPageChanged(position))
                        }
                    }
                    wordsPager.addOnPageChangeListener(listener)
                    awaitClose {
                        wordsPager.removeOnPageChangeListener(listener)
                    }
                }
            )
        }
    }

    override fun M.render() {
        with(binding) {
            ifChanged(M::phrase) {
                wordsPager.adapter = WordPagerAdapter(this@ShowPaperKeyController, phrase)
            }
            ifChanged(M::currentWord) {
                wordsPager.currentItem = currentWord
                tvItemIndex.text = resources?.getString(
                    R.string.WritePaperPhrase_step,
                    currentWord + 1,
                    phrase.size
                )
            }
            ifChanged(M::doneButtonEnabled) {
                btnDone.isEnabled = it
            }
            ifChanged(M::warningMessage) {
                tvWarning.setText(it)
            }
        }
    }

    fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels
}

class WordPagerAdapter(
    host: Controller,
    private val words: List<String>
) : RouterPagerAdapter(host) {
    override fun configureRouter(router: Router, position: Int) {
        router.replaceTopController(RouterTransaction.with(WordController(words[position])))
    }

    override fun getCount() = words.size
}

class WordController(args: Bundle? = null) : BaseController(args) {
    companion object {
        private const val EXT_WORD = "word"
    }

    constructor(word: String) : this(
        bundleOf(EXT_WORD to word)
    )

    private val binding by viewBinding(FragmentWordItemBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        binding.tvWord.text = arg(EXT_WORD)
    }
}

class WordPageTransformer(viewPager: ViewPager) : ViewPager.PageTransformer {

    private val viewPagerReference = WeakReference(viewPager)

    @Override
    override fun transformPage(page: View, position: Float) {
        val viewPager = viewPagerReference.get() ?: return

        val pageWidth = (viewPager.measuredWidth - viewPager.paddingLeft - viewPager.paddingRight).toFloat() / 2
        val paddingLeft = viewPager.paddingLeft
        val transformPos = (page.left.toFloat() - (viewPager.scrollX.toFloat() + paddingLeft)) / pageWidth

        val textView = (page as ViewGroup).findViewById<TextView>(R.id.tv_word)

        when {
            // check if this is current page (Interval -1 -> 1)
            transformPos >= -1 && transformPos <= 1 -> {
                textView.setTextAppearance(R.style.TextStyle_Title2)
                textView.setTextColor(ContextCompat.getColor(page.context, R.color.light_text_01))
            }
            else -> {
                textView.setTextAppearance(R.style.TextStyle_Title6)
                textView.setTextColor(ContextCompat.getColor(page.context, R.color.light_text_02))
            }
        }
    }
}