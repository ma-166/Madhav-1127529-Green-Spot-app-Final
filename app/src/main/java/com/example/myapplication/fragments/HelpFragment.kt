package com.example.myapplication.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.example.myapplication.R
import android.webkit.WebView
import androidx.fragment.app.Fragment

class HelpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_help, container, false)
        val webView = view.findViewById<WebView>(R.id.webView)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Load the dummy content URL
        webView.loadUrl(HELP_URL)
        return view
    }

    companion object {
        private const val HELP_URL = "https://identify.plantnet.org"
        @kotlin.jvm.JvmStatic
        fun newInstance(): HelpFragment {
            return HelpFragment()
        }
    }
}