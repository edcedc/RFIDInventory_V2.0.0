package com.yyc.stocktake2.ui.frg

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.StringUtils
import com.yyc.stocktake2.R
import com.yyc.stocktake2.adapter.AssetAdapter
import com.yyc.stocktake2.api.UIHelper
import com.yyc.stocktake2.base.BaseFragment
import com.yyc.stocktake2.bean.db.AssetBean
import com.yyc.stocktake2.databinding.BNotTitleRecyclerBinding
import com.yyc.stocktake2.ext.INVENTORY_FAIL
import com.yyc.stocktake2.ext.INVENTORY_NOT
import com.yyc.stocktake2.ext.init
import com.yyc.stocktake2.ext.setNbOnItemClickListener
import com.yyc.stocktake2.viewmodel.AssetModel
import com.yyc.stocktake2.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/7/27 16:18
 * @Description 不在库
 */
class AssetNoStockFrg: BaseFragment<AssetModel, BNotTitleRecyclerBinding>(){

    private val assetModel: AssetModel by activityViewModels()

    val adapter: AssetAdapter by lazy { AssetAdapter(arrayListOf()) }

    var orderId: String? = null

    var searchText: String? = null

    var fmIsVisible = false

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            orderId = it.getString("orderId")
        }
        mDatabind.swipeRefresh.isEnabled = false

        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }
        adapter.run {
            setNbOnItemClickListener{adapter, view, position ->
                val bean = adapter.data[position] as AssetBean
                UIHelper.startAssetDetailsFrg(nav(), bean)
            }
        }

        mViewModel.onRequest(orderId, INVENTORY_NOT)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                fmIsVisible = true
                assetModel.assetTitle.value = getString(R.string.missing) + "(" + adapter.data.size + ")"
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                fmIsVisible = false
            }
        })
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            adapter!!.appendList(it)
        })
        //搜索
        assetModel.assetSerch.observe(viewLifecycleOwner, {
            searchText = it
            adapter!!.filter.filter(searchText)
        })
        //识别RFID
        assetModel.epcUploadData.observe(viewLifecycleOwner, {
            if (it == null || it.InventoryStatus == INVENTORY_FAIL)return@observe
            val index = adapter.data.indexOfFirst {bean ->
                (!StringUtils.isEmpty(it.LabelTag) && bean.LabelTag.equals(it.LabelTag)) || bean.AssetNo.equals(it.AssetNo)
            }
            if (index != -1){
                adapter.removeAt(index)
            }
            /*if (it.InventoryStatus == INVENTORY_STOCK){
                adapter.removeAt(index)
            }else{
                //添加不在库
                adapter.addData(0, it)
            }*/
            //更新搜索页面item状态
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
            if (fmIsVisible)assetModel.assetTitle.value = getString(R.string.missing) + "(" + adapter.data.size + ")"
        })
    }

}