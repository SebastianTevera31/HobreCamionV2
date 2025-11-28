package com.rfz.appflotal.data.repository.disassembly

import com.rfz.appflotal.data.model.disassembly.tire.DisassemblyTire
import javax.inject.Inject

class SetDisassemblyTireUseCase @Inject constructor(
    private val disassemblyTireRepository: DisassemblyTireRepository
) {
    suspend operator fun invoke(request: DisassemblyTire) = runCatching {
        disassemblyTireRepository.pushDisassemblyTire(request)
    }
}