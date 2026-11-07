package com.rfz.appflotal.domain.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import javax.inject.Inject


class AddAssemblyTireUseCase @Inject constructor(private val assemblyTireRepository: AssemblyTireRepository) {
    suspend operator fun invoke(assemblyTire: AssemblyTire): Result<Unit> =
        runCatching { assemblyTireRepository.addAssemblyTire(assemblyTire) }
}

/** Montaje registrado en una posición (incluye el id de la llanta montada), o null si está vacía. */
class GetAssemblyTireUseCase @Inject constructor(private val assemblyTireRepository: AssemblyTireRepository) {
    suspend operator fun invoke(positionTire: String): AssemblyTire? =
        assemblyTireRepository.getAssemblyTire(positionTire)
}