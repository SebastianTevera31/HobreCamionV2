package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.dto.DisassemblyTireDto
import com.rfz.appflotal.data.repository.tire.DisassemblyTireCrudRepository
import javax.inject.Inject

class DisassemblyTireCrudUseCase @Inject constructor(
    private val disassemblyTireCrudRepository: DisassemblyTireCrudRepository
) {
    suspend operator fun invoke(requestBody: DisassemblyTireDto, token: String): Result<List<GeneralResponse>> {
        return disassemblyTireCrudRepository.doBrandCrud(requestBody, token)
    }
}
