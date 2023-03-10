package com.rockwallet.support.pages

import android.content.Context
import com.rockwallet.support.Category
import com.rockwallet.support.Page
import com.rockwallet.support.R
import com.rockwallet.support.SupportResponse
import com.google.gson.Gson
import java.lang.IllegalStateException

object SupportPagesLoader {
    lateinit var support: SupportResponse

    operator fun invoke(context: Context): SupportPagesLoader {
        val json = context.resources.openRawResource(R.raw.support_topics).bufferedReader().use { buffer -> buffer.readText() }
        support = Gson().fromJson(json, SupportResponse::class.java)
        return this
    }

    operator fun invoke(): SupportPagesLoader {
        throw IllegalStateException("Empty constructor is not supported, please use " + SupportPagesLoader::class.java.toString() + "(context)")
    }

    fun pages():List<Page> {
        return support.pages
    }

    fun categories():List<Category> {
        return support.categories
    }
}