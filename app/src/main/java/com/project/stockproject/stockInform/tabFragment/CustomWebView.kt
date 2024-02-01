package com.project.stockproject.stockInform.tabFragment

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.webkit.WebView

class CustomWebView : WebView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    init {
        setOnKeyListener { _, i, _ ->
            if (i == KeyEvent.KEYCODE_BACK && canGoBack()) {
                // WebView에서 뒤로가기 실행
                goBack()
                true // 이벤트 소비
            } else {
                false // 이벤트 전파
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // WebView에서 터치 이벤트를 소비하지 않도록 설정
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // ViewPager 내에서 WebView가 꽉 차게 표시되도록 설정
        val width = measuredWidth
        val height = measuredHeight
        setMeasuredDimension(width, height)
    }



}