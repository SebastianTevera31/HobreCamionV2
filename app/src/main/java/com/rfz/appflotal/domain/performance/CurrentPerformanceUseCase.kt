package com.rfz.appflotal.domain.performance

import com.rfz.appflotal.domain.report.GetCO2EmissionsUseCase
import com.rfz.appflotal.domain.report.GetFuelConsumptionUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class CurrentPerformanceUseCase @Inject constructor(
    private val getFuelConsumptionUseCase: GetFuelConsumptionUseCase,
//    private val getCpkReportUseCase: GetCpkReportUseCase,
    private val getCO2EmissionsUseCase: GetCO2EmissionsUseCase
) {

    suspend operator fun invoke(): PerformanceData = coroutineScope {
        // 1. Obtener el formato de mes/año actual (Ej: "2026 / Agosto")
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        val monthName = DateFormatSymbols(Locale("es", "MX"))
            .months[currentMonth - 1]
            .replaceFirstChar { it.uppercaseChar() }
        val currentMonthApiFormat = "$currentYear / $monthName"

        // 2. Obtener los datos de los reportes en paralelo
        val fuelConsumptionDeferred = async { getFuelConsumptionUseCase() }
        val co2EmissionsDeferred = async { getCO2EmissionsUseCase() }

        val fuelConsumptionResult = fuelConsumptionDeferred.await().getOrNull()
        val co2EmissionsResult = co2EmissionsDeferred.await().getOrNull()

        // 3. Extraer el registro más actual para cada uno
        val latestFuel = fuelConsumptionResult?.find { it.month == currentMonthApiFormat }
            ?: fuelConsumptionResult?.firstOrNull()

        val latestCO2 = co2EmissionsResult?.find { it.month == currentMonthApiFormat }
            ?: co2EmissionsResult?.firstOrNull()

        // 4. Retornar datos estructurados
        PerformanceData(
            fuelConsumption = latestFuel?.monthlyPerformance ?: "0",
            co2Emissions = latestCO2?.monthlyCO2Emissions ?: "0"
        )
    }
}
