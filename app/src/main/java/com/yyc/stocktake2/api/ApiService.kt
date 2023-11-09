package com.yyc.stocktake2.api

import com.yyc.stocktake2.bean.BaseListBean
import com.yyc.stocktake2.bean.BaseResponseBean
import com.yyc.stocktake2.bean.DataBean
import com.yyc.stocktake2.bean.db.OrderBean
import com.yyc.stocktake2.util.CacheUtil
import okhttp3.ResponseBody
import retrofit2.http.*


/**
 * Created by xuhao on 2017/11/16.
 * Api 接口
 */

interface ApiService{

    companion object {

        private val url =
            "192.168.2.30"
//            "47.243.120.137"

        var SERVLET_URL = "http://" +
                url + "/RFIDInventoryWebService_v2/MobileWebService.asmx/"

    }

    //登录
    @FormUrlEncoded
    @POST("CheckLogin")
    suspend fun CheckLogin(
        @Field("companyID") companyID: String,
        @Field("loginID") loginID: String,
        @Field("userPwd") userPwd: String
    ): BaseResponseBean<DataBean>

    //盘点列表
    @FormUrlEncoded
    @POST("stockTakeList")
    suspend fun stockTakeList(
        @Field("userid") userid: String = CacheUtil.getUser()!!.RoNo,
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<OrderBean>>

    // Rfid列表
    @FormUrlEncoded
    @POST("stockTakeListAsset")
    suspend fun stockTakeListAsset(
        @Field("orderno") orderno: String,
        @Field("userid") userid: String = CacheUtil.getUser()!!.RoNo,
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): ResponseBody

    // 借还审核单
    @FormUrlEncoded
    @POST("SelectBorrow")
    suspend fun GetExteriOrderBorrowingList(
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    // 外部借出订单详情
    @FormUrlEncoded
    @POST("SelectBorrowdetails")
    suspend fun GetExteriOrderBorrowingDetails(
        @Field("OrderNo") order: String?,
        @Field("URoNo") All: String = CacheUtil.getUser()!!.RoNo,
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    // 内部借出订单
    @FormUrlEncoded
    @POST("GetInsideOrderBorrowingDetailsAPP")
    suspend fun GetInsideOrderBorrowingDetailsAPP(
        @Field("type") type: Int,
        @Field("All") All: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    // 注销订单列表
    @FormUrlEncoded
    @POST("SelectDisposal")
    suspend fun GetDisposalorder(
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    // 注销订单详情
    @FormUrlEncoded
    @POST("SelectDisposaldetails")
    suspend fun GetDisposalorderDetails(
        @Field("OrderNo") order: String?,
        @Field("URoNo") All: String = CacheUtil.getUser()!!.RoNo,
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): BaseListBean<ArrayList<DataBean>>

    //上传图片
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("UploadStockTake")
    suspend fun FileToByte(
        @Field("companyID") UnitCode: String?,
        @Field("strJson") strJson: String
    ): BaseResponseBean<DataBean>

    //上传图片
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("FileToByte")
    suspend fun FileToByte(
        @Field("Suffix") Suffix: String,
        @Field("iCode") iCode: String,
        @Field("fileLoc") fileLoc: Int,
        @Field("loginID") loginID: String?,
        @Field("order") order: String,
        @Field("UnitCode") UnitCode: String?,
        @Field("str1") str1: String
        ): BaseResponseBean<DataBean>

    //上传数据
    @POST("UploadStockTake")
    @FormUrlEncoded
    suspend fun UploadStockTake(
        @Field("UnitCode") unitCode: String?,
        @Field("strJson") strJson: String

    ): BaseResponseBean<DataBean>

    //提交在架上
    @POST("GetwhetherBorrowingExistApp")
    @FormUrlEncoded
    suspend fun GetwhetherBorrowingExistApp(
        @Field("str") strJson: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseResponseBean<DataBean>

    //注销确认上传
    @POST("IsDisposal")
    @FormUrlEncoded
    suspend fun UpdateDisposalAPP(
        @Field("strJson") strJson: String,
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): BaseResponseBean<DataBean>

    //外部借用确认上传
    @POST("IsBorrow")
    @FormUrlEncoded
    suspend fun GetUpdateExteriorBorrowingApp(
        @Field("strJson") strJson: String,
        @Field("companyid") companyid: String  = CacheUtil.getCompanyID()
    ): BaseResponseBean<DataBean>

    //内部借用确认上传
    @POST("GetupdateInsideBorrowingApp")
    @FormUrlEncoded
    suspend fun GetupdateInsideBorrowingApp(
        @Field("strJson") strJson: String,
        @Field("UnitCode") companyid: String  = CacheUtil.getCompanyID()
    ): BaseResponseBean<DataBean>

}