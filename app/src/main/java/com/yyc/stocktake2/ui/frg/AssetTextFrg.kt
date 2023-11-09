package com.yyc.stocktake2.ui.frg

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.yyc.stocktake2.adapter.AssetDetailsAdapter
import com.yyc.stocktake2.base.BaseFragment
import com.yyc.stocktake2.databinding.BNotTitleRecyclerBinding
import com.yyc.stocktake2.ext.init
import com.yyc.stocktake2.viewmodel.AssetModel
import com.yyc.stocktake2.weight.recyclerview.SpaceItemDecoration
import org.json.JSONArray

/**
 * @Author nike
 * @Date 2023/8/2 14:18
 * @Description rfid 展示详情
 */
class AssetTextFrg: BaseFragment<AssetModel, BNotTitleRecyclerBinding>() {

    var jsonArray = JSONArray()

    val adapter by lazy { activity?.let { AssetDetailsAdapter(it, jsonArray) } }

    var bean: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            bean = it.getString("bean")
        }
        mDatabind.swipeRefresh.isEnabled = false
        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter!!).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(0f), ConvertUtils.dp2px(0f), true))
        }
        adapter.run {

        }
        mViewModel.onRequestText(bean)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listJsonArray.observe(viewLifecycleOwner, {
            var list = it as JSONArray
            list.remove(0)
            for (i in 0 until list.length()) {
                val obj = list.optJSONObject(i)
                if (i == 0) {
                    continue
                }
                jsonArray.put(obj)
            }
            val obj = list.optJSONObject(0)
            jsonArray.put(obj)
            adapter!!.notifyDataSetChanged()
        })
    }

}