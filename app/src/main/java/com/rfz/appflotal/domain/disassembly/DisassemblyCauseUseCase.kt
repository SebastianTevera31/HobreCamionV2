package com.rfz.appflotal.domain.disassembly

import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.repository.disassembly.DisassemblyCauseRepository
import javax.inject.Inject

class DisassemblyCauseUseCase @Inject constructor(
    private val disassemblyCauseRepository: DisassemblyCauseRepository
) {
    suspend operator fun invoke(): Result<List<DisassemblyCauseResponse>> {
        return disassemblyCauseRepository.doDisassemblyCause()
    }
}
