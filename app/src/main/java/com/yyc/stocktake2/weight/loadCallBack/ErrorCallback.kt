package com.yyc.stocktake2.weight.loadCallBack

import com.yyc.stocktake2.R
import com.kingja.loadsir.callback.Callback


class ErrorCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_error
    }

}