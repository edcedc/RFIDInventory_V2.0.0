package com.yyc.stocktake2.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.yyc.stocktake2.R
import com.yyc.stocktake2.bean.RfidStateBean
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

/**
 * @Author nike
 * @Date 2023/8/23 11:11
 * @Description
 */
class AssetSearchmModel: BaseViewModel() {

    var rssi = StringObservableField("0")

    val isOpen = ObservableBoolean()

    val openStatus = StringObservableField(appContext.getString(R.string.start))

    var epcData: MutableLiveData<RfidStateBean> = MutableLiveData()

}