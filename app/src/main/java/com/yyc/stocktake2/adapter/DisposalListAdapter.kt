package com.yyc.stocktake2.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Filter
import android.widget.Filterable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.stocktake2.R
import com.yyc.stocktake2.bean.DataBean
import com.yyc.stocktake2.ext.DISPOSAL_ARCHIVES_TYPE
import com.yyc.stocktake2.ext.DISPOSAL_BOOK_TYPE
import com.yyc.stocktake2.ext.DISPOSAL_PASS
import com.yyc.stocktake2.ext.EXTERNAL_ARCHIVES_TYPE
import com.yyc.stocktake2.ext.EXTERNAL_BOOK_TYPE
import com.yyc.stocktake2.ext.INTERNAL_ARCHIVES_TYPE
import com.yyc.stocktake2.ext.INTERNAL_BOOK_TYPE
import com.yyc.stocktake2.ext.setAdapterAnimation
import com.yyc.stocktake2.util.SettingUtil

/**
 * @Author nike
 * @Date 2023/7/7 17:05
 * @Description
 */
class DisposalListAdapter(data: ArrayList<DataBean>, mType: Int) :BaseQuickAdapter<DataBean, BaseViewHolder>(R.layout.i_asset1, data), Filterable {

    var mType: Int = 0

    init {
        setAdapterAnimation(SettingUtil.getListMode())
        this.mType = mType
    }

    override fun convert(viewHolder: BaseViewHolder, bean: DataBean) {
        //赋值
        bean.run {
            val bean = mFilterList[viewHolder.layoutPosition]

            if (mType == DISPOSAL_BOOK_TYPE){
                viewHolder.setText(R.id.tv_title, context.getText(R.string.cancelMethod))
                viewHolder.setText(R.id.tv_title1, "：" + bean.CancelMethod)
                viewHolder.setText(R.id.tv_location, context.getText(R.string.cancelReason))
                viewHolder.setText(R.id.tv_location1, "：" + bean.CancelReason)


            }else{
                viewHolder.setText(R.id.tv_title, context.getText(R.string.label))
                viewHolder.setText(R.id.tv_title1, "：" + if (bean.LabelMode == null) "" else bean.LabelMode)

                viewHolder.setText(R.id.tv_location, context.getText(R.string.start_date))
                var createDate = bean.createdate
                if (createDate!!.contains("00:00:00")){
                    createDate = bean.createdate!!.substring(0, bean.createdate!!.length - 9)
                }
                viewHolder.setText(R.id.tv_location1, "：" + createDate)

            }

            viewHolder.setText(R.id.tv_epc, context.getText(R.string.needApproveGRoNo))
            viewHolder.setText(R.id.tv_epc1, "：" + bean.NeedApproveGRoNo)
            viewHolder.setText(R.id.tv_type, context.getText(R.string.approvedGRoNo))
            viewHolder.setText(R.id.tv_type1, "：" + bean.ApprovedGRoNo)

            if (bean.Show == 0){
                val passText = "Pass"
                val spannableString = SpannableString(bean.AssetNo + " | " + passText)
                spannableString.setSpan(ForegroundColorSpan(Color.RED), spannableString.length - passText.length, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                viewHolder.setText(R.id.tv_text, spannableString)
            }else{
                viewHolder.setText(R.id.tv_text, bean.AssetNo)
            }

            viewHolder.setGone(R.id.iv_image, if (bean.Show == 0) true else false)
            viewHolder.setImageResource(R.id.iv_image, if (bean.type == 0) R.mipmap.icon_31 else R.mipmap.icon_30)

            /*when(mType){
                EXTERNAL_BOOK_TYPE, INTERNAL_BOOK_TYPE, DISPOSAL_BOOK_TYPE ->{
                    viewHolder.setText(R.id.tv_text, bean.AssetNo + " | " + bean.LibraryCallNo)
                    viewHolder.setGone(R.id.tv_epc, true)
                    viewHolder.setGone(R.id.tv_epc1, true)
                    viewHolder.setGone(R.id.tv_type, true)
                    viewHolder.setGone(R.id.tv_type1, true)
                }
                EXTERNAL_ARCHIVES_TYPE, INTERNAL_ARCHIVES_TYPE, DISPOSAL_ARCHIVES_TYPE ->{
                    viewHolder.setText(R.id.tv_text, bean.AssetNo + " | " + bean.ArchivesType)
                    viewHolder.setText(R.id.tv_epc, context.getText(R.string.level))
                    viewHolder.setText(R.id.tv_epc1, "：" + bean.LevelType)
                    viewHolder.setText(R.id.tv_type, context.getText(R.string.type))
                    viewHolder.setText(R.id.tv_type1, "：" + bean.ArchivesNo)
                    viewHolder.setGone(R.id.tv_epc, false)
                    viewHolder.setGone(R.id.tv_epc1, false)
                    viewHolder.setGone(R.id.tv_type, false)
                    viewHolder.setGone(R.id.tv_type1, false)
                }

                else -> {

                }
            }*/
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
                        val assetNo = bean.AssetNo
                        if (assetNo!!.contains(charString)) {
                            filteredList.add(bean)
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

    private fun  getFilterList(): List<DataBean>{
        return mFilterList
    }

}