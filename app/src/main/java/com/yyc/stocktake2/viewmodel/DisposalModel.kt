package com.yyc.stocktake2.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.yyc.stocktake2.R
import com.yyc.stocktake2.bean.DataBean
import com.yyc.stocktake2.network.REQUEST_SUCCESS
import com.yyc.stocktake2.network.apiService
import com.yyc.stocktake2.network.stateCallback.ListDataUiState
import com.yyc.stocktake2.util.CacheUtil
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestNoCheck
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/8/8 17:01
 * @Description
 */
class DisposalModel: BaseViewModel() {

    var state = StringObservableField()

    var save = StringObservableField()

    var searchText: MutableLiveData<String> = MutableLiveData()

    var listJsonArray: MutableLiveData<JSONArray> = MutableLiveData()

    var listIsPassArray: MutableLiveData<JSONArray> = MutableLiveData()

    var listClearArray: MutableLiveData<String> = MutableLiveData()

    var submitType: MutableLiveData<Int> = MutableLiveData()
//    val submitType:SingleLiveEvent<Int> = SingleLiveEvent()

    var listData: MutableLiveData<ListDataUiState<DataBean>> = MutableLiveData()

    var listBooKArchivesData: MutableLiveData<ListDataUiState<DataBean>> = MutableLiveData()

    val pagerNumber: Int = 1

    var userRoNo = CacheUtil.getUser()!!.RoNo

    fun onRequest() {
        request({ apiService.GetDisposalorder() }, {
            val isRefresh = true
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isEmpty = it!!.isEmpty(),
                    isRefresh = isRefresh,
                    isFirstEmpty = pagerNumber == 1 && it.isEmpty(),
                    listData = it
                )
            listData.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = true,
                    listData = arrayListOf<DataBean>()
                )
            listData.value = listDataUiState
        })
    }

    fun onRequestDetails(orderId: String?, i: Int) {
        request({ apiService.GetDisposalorderDetails(orderId) }, {
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isEmpty = it!!.isEmpty(),
                    isRefresh = true,
                    isFirstEmpty = pagerNumber == 1 && it.isEmpty(),
                    listData = it
                )
            listBooKArchivesData.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = true,
                    listData = arrayListOf<DataBean>()
                )
            listBooKArchivesData.value = listDataUiState
        })
    }

    fun onGetwhetherBorrowingExistApp(orderId: String?, str: String) {
        request({apiService.GetwhetherBorrowingExistApp(str)},{

        },{
            //请求失败 网络异常回调在这里
            loadingChange.dismissDialog
            ToastUtils.showShort(it.throwable!!.message)
            LogUtils.e(it.throwable, it.throwable!!.message)
        }, true)
    }

    fun UpdateDisposalAPP(list: MutableList<DataBean>, ispass: Int) {
//        var sb = StringBuffer()
        var ja = JSONArray()
        list.filterIndexed { index, bean ->
            val shouldBeIncluded = (bean.type == 1)
            if (shouldBeIncluded){
//                sb.append(bean.RoNo).append(",")
                var jo = JSONObject()
                jo.put("ispass", ispass)
                jo.put("RoNo", bean.RoNo)
                jo.put("userRoNo", userRoNo)
                ja.put(jo)
            }
            shouldBeIncluded
        }
        requestNoCheck({apiService.UpdateDisposalAPP(ja.toString())},{
            if (it.code == REQUEST_SUCCESS){
//                listClearArray.value = sb.toString()
                listIsPassArray.value = ja
            }
        },{
            //请求失败 网络异常回调在这里
            loadingChange.dismissDialog
            ToastUtils.showShort(it.throwable!!.message)
            LogUtils.e(it.throwable, it.throwable!!.message)
        }, true, appContext.getString(R.string.loading))
    }

    fun onRequestText(title: String?, bean: String?) {
        val jo = JSONObject(bean)
        val ja = JSONArray()
        val headerkeys: Iterator<String> = jo.keys()
        while (headerkeys.hasNext()) {
            val headerkey = headerkeys.next()
            val headerValue: String = jo.getString(headerkey)
            var threeData = JSONObject()
            threeData.put("title", headerkey)
            threeData.put("text", headerValue)
            ja.put(threeData)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        jsonObject.put("title", title)
        jsonObject.put("list", ja)
        jsonArray.put(jsonObject)
        listJsonArray.value = jsonArray
    }

}