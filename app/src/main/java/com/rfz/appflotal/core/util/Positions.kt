package com.rfz.appflotal.core.util

object Positions {
    val positionMap = mapOf(
        "P1" to "P2",
        "P2" to "P1",
        "P3" to "P6",
        "P4" to "P5",
        "P5" to "P4",
        "P6" to "P3",
        "P7" to "P10",
        "P8" to "P9",
        "P9" to "P8",
        "P10" to "P7",
        "P11" to "P14",
        "P12" to "P13",
        "P13" to "P12",
        "P14" to "P11",
        "P15" to "P18",
        "P16" to "P17",
        "P17" to "P16",
        "P18" to "P15",
        "P19" to "P22",
        "P20" to "P21",
        "P21" to "P20",
        "P22" to "P19",
        "P23" to "P26",
        "P24" to "P25",
        "P25" to "P24",
        "P26" to "P23",
        "P27" to "P30",
        "P28" to "P29",
        "P29" to "P28",
        "P30" to "P27",
        "P31" to "P34",
        "P32" to "P33",
        "P33" to "P32",
        "P34" to "P31",
        "P35" to "P38",
        "P36" to "P37",
        "P37" to "P36",
        "P38" to "P35",
    )

    fun findOutPosition(position: String): String {
        return positionMap[position] ?: ""
    }
}