package com.yyc.stocktake2.bean

data class RfidStateBean(

    val tagId: String? = null,

    val scanStatus: Int = 0,

    val rssi: String? = null

)