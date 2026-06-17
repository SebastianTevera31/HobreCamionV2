package com.rfz.appflotal.domain.report

import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.tire.toTire
import com.rfz.appflotal.data.repository.report.ReportRepository
import com.rfz.appflotal.domain.tire.TireListUsecase
import javax.inject.Inject

class GetCpkReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val tireListUsecase: TireListUsecase
) {
    suspend operator fun invoke(): Result<CpkReportData> {
        val tiresResult = tireListUsecase()
        val cpkResult = reportRepository.getCpkReport()

        return if (cpkResult.isSuccess && tiresResult.isSuccess) {
            val reportList = cpkResult.getOrThrow()
            val tiresList = tiresResult.getOrThrow()

            val pairedTires = reportList.mapNotNull { report ->
                tiresList.find { it.idTire == report.idTire }?.let { tireDto ->
                    AssemblyTire(
                        idAxle = 0,
                        idTire = tireDto.idTire,
                        positionTire = report.tireNumber,
                        odometer = tireDto.odometerAssembly,
                        assemblyDate = tireDto.dateEventAssembly,
                        updatedAt = System.currentTimeMillis()
                    )
                }
            }

            Result.success(
                CpkReportData(
                    pairedTires = pairedTires,
                    detailedTires = tiresList.map { it.toTire() },
                    allReports = reportList
                )
            )
        } else {
            Result.failure(cpkResult.exceptionOrNull() ?: tiresResult.exceptionOrNull() ?: Throwable("Unknown error"))
        }
    }
}

data class CpkReportData(
    val pairedTires: List<AssemblyTire>,
    val detailedTires: List<com.rfz.appflotal.data.model.tire.Tire>,
    val allReports: List<CpkReportResponse>
)
