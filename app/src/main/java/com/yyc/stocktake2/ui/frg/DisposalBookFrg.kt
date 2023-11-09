package com.yyc.stocktake2.ui.frg

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.StringUtils
import com.kingja.loadsir.core.LoadService
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnCancelListener
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.yyc.stocktake2.R
import com.yyc.stocktake2.adapter.DisposalListAdapter
import com.yyc.stocktake2.base.BaseFragment
import com.yyc.stocktake2.bean.DataBean
import com.yyc.stocktake2.databinding.BNotTitleRecyclerBinding
import com.yyc.stocktake2.ext.DISPOSAL_BOOK_TYPE
import com.yyc.stocktake2.ext.DISPOSAL_NO_PASS
import com.yyc.stocktake2.ext.DISPOSAL_PASS
import com.yyc.stocktake2.ext.RFID_BOOK
import com.yyc.stocktake2.ext.init
import com.yyc.stocktake2.ext.loadListData
import com.yyc.stocktake2.ext.loadServiceInit
import com.yyc.stocktake2.ext.setNbOnItemClickListener
import com.yyc.stocktake2.ext.showLoading
import com.yyc.stocktake2.ext.showToast
import com.yyc.stocktake2.mar.eventViewModel
import com.yyc.stocktake2.viewmodel.DisposalModel
import com.yyc.stocktake2.weight.recyclerview.SpaceItemDecoration

/**
 * @Author nike
 * @Date 2023/9/8 10:57
 * @Description 图书
 */
class DisposalBookFrg: BaseFragment<DisposalModel, BNotTitleRecyclerBinding>() {

    private val disposalModel: DisposalModel by activityViewModels()

    val adapter: DisposalListAdapter by lazy { DisposalListAdapter(
        arrayListOf(),
        DISPOSAL_BOOK_TYPE
    ) }

    var orderId: String? = null

    var title: String? = null

    var searchText: String? = null

    var isVisibility: Boolean = false

    //界面状态管理者
    lateinit var loadsir: LoadService<Any>

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            orderId = it.getString("orderId")
            title = it.getString("title")
        }

        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }
        adapter.run {
            setNbOnItemClickListener { adapter, view, position ->
                val bean = adapter.data[position] as DataBean
                if (bean.Show == 0)return@setNbOnItemClickListener
                bean.type = if (bean.type == 1) 0 else 1
                setData(position, bean)
//                UIHelper.startDisposalDetailsFrg(nav(), bean, title)
            }
        }

        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {
            //点击重试时触发的操作
            loadsir.showLoading()
            mViewModel.onRequestDetails(orderId, RFID_BOOK)
        }

        //初始化 SwipeRefreshLayout  刷新
        mDatabind.swipeRefresh.init {
            mViewModel.onRequestDetails(orderId, RFID_BOOK)
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                isVisibility = false
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                isVisibility = true
            }

        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBooKArchivesData.observe(viewLifecycleOwner, {
            loadListData(it, adapter, loadsir, mDatabind.recyclerView, mDatabind.swipeRefresh, it.pageSize)
            adapter.appendList(it.listData)
        })
        //搜索
        disposalModel.searchText.observe(viewLifecycleOwner, {
            if (isVisibility){
                searchText = it
                adapter!!.filter.filter(searchText)
            }
        })
        //扫码
        eventViewModel.zkingType.observeInFragment(this, Observer {
            if (it.type == DISPOSAL_BOOK_TYPE) {
                val indexList = mutableListOf<Int>()
                adapter.data.filterIndexed { index, bean ->
                    val shouldBeIncluded = (!StringUtils.isEmpty(bean.LabelTag) && bean.LabelTag.equals(it.text)) || bean.AssetNo.equals(it.text)
                    // 将满足条件的索引添加到 indexList
                    if (shouldBeIncluded) {
                        indexList.add(index)
                    }
                    shouldBeIncluded
                }
                if (indexList.size == 0) {
                    showToast(getString(R.string.text5))
                    return@Observer
                }
                //更新item状态
                indexList.forEachIndexed() { index, i ->
                    val bean = adapter.data[i]
                    bean.type = if (bean.type == 1) 0 else 1
                    adapter.setData(i, bean)
                }
            }
        })

        //提交
        disposalModel.submitType.observe(viewLifecycleOwner, {
            if (it == DISPOSAL_BOOK_TYPE){
                if (adapter.data.size == 0){
                    showToast(getString(R.string.text6))
                    return@observe
                }
                val predicate = adapter.data.all { it.type == 0}
                if (predicate){
                    showToast(getString(R.string.text5))
                    return@observe
                }
                XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true)
                    .asConfirm(null, getText(R.string.review) ,
                        getText(R.string.review_cancel), getText(R.string.review_pass),
                        OnConfirmListener {
                            mViewModel.UpdateDisposalAPP(adapter.data, DISPOSAL_PASS)
                        }, OnCancelListener {
                            mViewModel.UpdateDisposalAPP(adapter.data, DISPOSAL_NO_PASS)
                        }, false)
                    .show();
            }
        })

        mViewModel.listClearArray.observe(viewLifecycleOwner, {
            val split = it.split(",")
            for (i in split.indices) {
                val s = split[i]
                val index = adapter.data.indexOfFirst { it.RoNo.equals(s)}
                if (index != -1)adapter.removeAt(index)
            }
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
        })

        mViewModel.listIsPassArray.observe(viewLifecycleOwner, {
            for (i in 0 until it.length()) {
                val obj = it.optJSONObject(i)
                val index = adapter.data.indexOfFirst { it.RoNo.equals(obj.optString("RoNo"))}
                if (index != -1){
                    val bean = adapter.data[index]
                    if (obj.optInt("ispass") == DISPOSAL_PASS){
                        bean.Show = 0
                        bean.type = 0
                        adapter.setData(index, bean)
                    }else{
                        adapter.removeAt(index)
                    }
                }
            }
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
        })
    }


    override fun lazyLoadData() {
        //设置界面 加载中
        loadsir.showLoading()
        mViewModel.onRequestDetails(orderId, RFID_BOOK)
    }
}