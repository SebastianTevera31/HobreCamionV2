package com.rfz.appflotal.domain.route

import com.rfz.appflotal.data.model.route.response.RouteResponse
import com.rfz.appflotal.data.repository.route.RouteRepository
import javax.inject.Inject

class RouteUseCase @Inject constructor(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(token: String): Result<List<RouteResponse>> {
        return routeRepository.doRoute(token)
    }
}
