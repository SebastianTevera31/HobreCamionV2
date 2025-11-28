package com.rfz.appflotal.data.model.disassembly

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse

data class DisassemblyCause(
    override val id: Int,
    override val description: String
) : CatalogItem

fun DisassemblyCauseResponse.toDisassemblyCause(): DisassemblyCause {
    return DisassemblyCause(
        id = idDisassemblyCause,
        description = description
    )
}