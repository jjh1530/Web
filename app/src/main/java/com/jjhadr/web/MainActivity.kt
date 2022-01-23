package com.jjhadr.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {
    private val goHomeButton :ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }
    private val goBackButton :ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }
    private val goForwardButton :ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }

    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }
    private val webView : WebView by lazy {
        findViewById(R.id.webView)
    }
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }
    private val progressbar : ProgressBar by lazy {
        findViewById(R.id.progreebar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindView()
    }

    override fun onBackPressed() {
        //웹 뷰내에 뒤로갈 페이지 없으면 종료한다.
        if (webView.canGoBack()) {
            webView.goBack()
        }else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        //앱 내에서 실행
        webView.apply {
            webViewClient = WebViewClient()
            //자바스크립트 기능 가능하게
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindView() {
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        addressBar.setOnEditorActionListener { textView, actionId, keyEvent ->
            //에디트 텍스트 입력 후 체크시 입력키보드 내려가고 주소창 이동
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = textView.text.toString()
                if (URLUtil.isNetworkUrl(loadingUrl)) {
                    webView.loadUrl(loadingUrl)
                }else {
                    webView.loadUrl("http://${loadingUrl}")
                }
            }
            return@setOnEditorActionListener false
        }

        goBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }
        //밑으로 당겨서 새로고침
        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    inner class WebViewClient: android.webkit.WebViewClient() {
        //페이지가 다 로딩되었을 때
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
            addressBar.setText(url)

        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient() {
        //로딩 시각적으로 표시
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressbar.progress = newProgress
        }
    }
    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }

}