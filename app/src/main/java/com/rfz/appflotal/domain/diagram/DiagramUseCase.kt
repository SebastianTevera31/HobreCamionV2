package com.rfz.appflotal.domain.diagram

import com.rfz.appflotal.data.model.diagram.response.DiagramResponse
import com.rfz.appflotal.data.repository.diagram.DiagramRepository
import javax.inject.Inject

class DiagramUseCase @Inject constructor(
    private val diagramRepository: DiagramRepository
) {
    suspend operator fun invoke(token: String): Result<List<DiagramResponse>> {
        return diagramRepository.doDiagram(token)
    }
}
