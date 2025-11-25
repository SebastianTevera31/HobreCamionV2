package com.rfz.appflotal.domain.axle

import com.rfz.appflotal.data.repository.axle.AxleRepositoryImpl
import javax.inject.Inject

class GetAxleDomain @Inject constructor(private val axleRepository: AxleRepositoryImpl) {
    suspend operator fun invoke() = runCatching { axleRepository.getAxles() }
}