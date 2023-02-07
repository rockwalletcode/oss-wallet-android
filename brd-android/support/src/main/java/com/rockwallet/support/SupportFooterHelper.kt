package com.rockwallet.support

import android.content.pm.PackageInfo
import android.util.Xml
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import java.net.URLEncoder


object SupportFooterHelper {
    fun populate(view:View, lifecycleOwner: LifecycleOwner) {
        val context = view.context
        val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val version = pInfo.versionName //Version Name
        val verCode = pInfo.versionCode //Version Code

        textView(view, R.id.versionNumber).text = "$version ($verCode))"

        textView(view, R.id.privacyLink).setOnClickListener {
            val pdfUrl = "https://uploads-ssl.webflow.com/636012218b483e2e5e98b3e4/636919229c53fbb688353ef3_RockWallet-USPrivacy-2022-07.pdf"
//            lifecycleOwner.launchWebsite(getGoogleDocsEmbeddablePDFViewer(pdfUrl))
        }
    }

    fun getGoogleDocsEmbeddablePDFViewer(pdfUrl:String) : String {
        val urlEncoded = URLEncoder.encode(pdfUrl, Xml.Encoding.UTF_8.toString())
        return "https://docs.google.com/gview?embedded=true&url=$urlEncoded"
    }

    private fun textView(view: View, id:Int): TextView {
        return view.findViewById(id)
    }
}