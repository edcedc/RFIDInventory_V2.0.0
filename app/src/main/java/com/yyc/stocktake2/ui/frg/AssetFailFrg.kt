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
import com.yyc.stocktake2.base.BaseFragment
import com.yyc.stocktake2.bean.db.AssetBean
import com.yyc.stocktake2.databinding.BNotTitleRecyclerBinding
import com.yyc.stocktake2.ext.INVENTORY_FAIL
import com.yyc.stocktake2.ext.init
import com.yyc.stocktake2.ext.setNbOnItemClickListener
import com.yyc.stocktake2.viewmodel.AssetModel
import com.yyc.stocktake2.weight.recyclerview.SpaceItemDecoration

/**
 * @Author nike
 * @Date 2023/7/27 16:18
 * @Description 异常
 */
class AssetFailFrg: BaseFragment<AssetModel, BNotTitleRecyclerBinding>(){

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
//                LogUtils.e(bean.LabelTag)
            }
        }
        mViewModel.onRequest(orderId, INVENTORY_FAIL)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                fmIsVisible = true
                assetModel.assetTitle.value = getString(R.string.abnormal) + "(" + adapter.data.size + ")"
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
        //更新搜索页面item状态
        assetModel.epcUploadData.observe(viewLifecycleOwner, {
            if (it == null || it.InventoryStatus != INVENTORY_FAIL)return@observe
            adapter.addData(it)
//            mDatabind.recyclerView.scrollToPosition(0)
            //更新搜索页面item状态
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
            if (fmIsVisible)assetModel.assetTitle.value = getString(R.string.abnormal) + "(" + adapter.data.size + ")"
        })

    }
}