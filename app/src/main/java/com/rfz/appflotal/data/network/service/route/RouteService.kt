package com.rfz.appflotal.data.network.service.route

import com.rfz.appflotal.data.model.product.response.ProductResponse
import com.rfz.appflotal.data.model.route.response.RouteResponse
import com.rfz.appflotal.data.network.client.product.ProductListClient
import com.rfz.appflotal.data.network.client.route.RouteClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RouteService @Inject constructor(private val routeClient: RouteClient) {
    suspend fun doRoute(tok:String): Response<List<RouteResponse>> {
        return withContext(Dispatchers.IO) {
            routeClient.doRoute(tok)
        }
    }
}