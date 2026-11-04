package com.rfz.appflotal.domain.fueling

import com.rfz.appflotal.data.model.catalog.FuelTypeItem
import com.rfz.appflotal.data.model.fueling.FuelingPage
import com.rfz.appflotal.data.model.fueling.NewFueling
import com.rfz.appflotal.data.repository.catalog.CatalogRepository
import com.rfz.appflotal.data.repository.fueling.FuelingRepository
import javax.inject.Inject

class GetFuelTypesUseCase @Inject constructor(private val catalogRepository: CatalogRepository) {
    suspend operator fun invoke(): Result<List<FuelTypeItem>> = catalogRepository.getFuelTypes()
}

class InsertFuelingUseCase @Inject constructor(private val fuelingRepository: FuelingRepository) {
    suspend operator fun invoke(fueling: NewFueling): Result<Unit> =
        fuelingRepository.insertFueling(fueling)
}

class GetFuelingHistoryUseCase @Inject constructor(
    private val fuelingRepository: FuelingRepository
) {
    suspend operator fun invoke(
        startDate: String?,
        endDate: String?,
        fuelTypeId: Int?,
        page: Int,
        pageSize: Int
    ): Result<FuelingPage> = fuelingRepository.getFuelingHistory(
        startDate = startDate,
        endDate = endDate,
        fuelTypeId = fuelTypeId,
        page = page,
        pageSize = pageSize
    )
}
