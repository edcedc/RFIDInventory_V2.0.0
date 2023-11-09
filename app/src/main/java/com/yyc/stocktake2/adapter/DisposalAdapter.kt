package com.yyc.stocktake2.adapter

import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.stocktake2.R
import com.yyc.stocktake2.bean.DataBean
import com.yyc.stocktake2.ext.setAdapterAnimation
import com.yyc.stocktake2.util.SettingUtil

/**
 * @Author nike
 * @Date 2023/7/7 17:05
 * @Description
 */
class DisposalAdapter (data: ArrayList<DataBean>) :
    BaseQuickAdapter<DataBean, BaseViewHolder>(
        R.layout.i_external, data), Filterable {


    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(viewHolder: BaseViewHolder, bean: DataBean) {
        //赋值
        bean.run {
            val bean = mFilterList[viewHolder.layoutPosition]
            viewHolder.setText(R.id.tv_text, bean.OrderNo + " | " + bean.OrderNoName)
            bean.Title = viewHolder.getView<AppCompatTextView>(R.id.tv_text).text.toString()

            viewHolder.setText(R.id.tv_title, context.getText(R.string.start_date))
            viewHolder.setText(R.id.tv_title1, "：" + bean.CreateDate!!.substring(0, bean.CreateDate!!.length - 9))
            viewHolder.setText(R.id.tv_location, context.getText(R.string.statusName))
            viewHolder.setText(R.id.tv_location1, "：" + bean.StatusName)
            viewHolder.setText(R.id.tv_epc, context.getText(R.string.statusClass))
            viewHolder.setText(R.id.tv_epc1, "：" + bean.StatusClass)
            viewHolder.setText(R.id.tv_progress, context.getText(R.string.ratio))
            viewHolder.setText(R.id.tv_progress1, "：" + bean.ratio)

        }
    }

    var mFilterList = ArrayList<DataBean>()

    fun appendList(list: List<DataBean>) {
        data = list as MutableList<DataBean>
        //这里需要初始化filterList
        mFilterList = list as ArrayList<DataBean>
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = data as ArrayList<DataBean>
                } else {
                    val filteredList: MutableList<DataBean> = ArrayList()
                    for (i in data.indices) {
                        val bean = data[i]
                        val labelTag = bean.OrderNo
                        if (!StringUtils.isEmpty(labelTag)) {
                            if (labelTag!!.contains(charString)) {
                                filteredList.add(bean)
                            }
                        }
                    }
                    mFilterList = filteredList as ArrayList<DataBean>
                }
                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilterList = filterResults.values as ArrayList<DataBean>
                notifyDataSetChanged()
            }
        }
    }


    override fun getItemCount(): Int {
        return mFilterList.size
    }

    override fun hashCode(): Int {
        return mFilterList.hashCode()
    }

    fun  getFilterList(): List<DataBean>{
        return mFilterList
    }

}