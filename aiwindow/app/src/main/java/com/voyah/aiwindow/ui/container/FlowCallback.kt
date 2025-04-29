package com.voyah.aiwindow.ui.container

import android.view.View

abstract class FlowCallback {

    open fun onAttach(v: View?) {
    }

    open fun onClick() {
    }

    open fun onVisible(v: View, vis: Int) {
    }

    open fun onDismiss() {
    }

    open fun onUpdate() {
    }
}