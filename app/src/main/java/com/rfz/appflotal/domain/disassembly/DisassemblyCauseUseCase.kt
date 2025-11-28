package com.rfz.appflotal.domain.disassembly

import com.rfz.appflotal.data.model.disassembly.DisassemblyCause
import com.rfz.appflotal.data.model.disassembly.toDisassemblyCause
import com.rfz.appflotal.data.repository.disassembly.DisassemblyTireRepository
import javax.inject.Inject

class DisassemblyCauseUseCase @Inject constructor(
    private val disassemblyCauseRepository: DisassemblyTireRepository
) {
    suspend operator fun invoke(): Result<List<DisassemblyCause>> {
        return disassemblyCauseRepository.doDisassemblyCause()
            .map { it.map { disassemblyResponse -> disassemblyResponse.toDisassemblyCause() } }
    }
}
