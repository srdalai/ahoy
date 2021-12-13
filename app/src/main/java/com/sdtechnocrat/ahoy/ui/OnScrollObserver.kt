package com.sdtechnocrat.ahoy.ui

import android.widget.AbsListView

abstract class OnScrollObserver : AbsListView.OnScrollListener {

    var last : Int = 0
    var control : Boolean = true

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
    }

    override fun onScroll(view: AbsListView?, current: Int, visibles: Int, total: Int) {
        if (current < last && !control) {
            onScrollUp();
            control = true;
        } else if (current > last && control) {
            onScrollDown();
            control = false;
        }

        last = current;
    }

    abstract fun onScrollUp()

    abstract fun onScrollDown()
}