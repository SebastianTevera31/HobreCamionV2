package com.rfz.appflotal.data.network.service.services

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.dto.SERVICE_IMAGE_PART
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.model.services.dto.toMultipart
import com.rfz.appflotal.data.model.services.response.ServiceResponseDto
import com.rfz.appflotal.data.model.services.response.TypeServiceDto
import com.rfz.appflotal.data.network.client.services.ServiceClient
import com.rfz.appflotal.data.network.multipart.ImagePartFactory
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteServiceDataSource @Inject constructor(
    private val serviceClient: ServiceClient,
    private val imagePartFactory: ImagePartFactory
) {
    suspend fun getServices(token: String): Result<List<ServiceResponseDto>> {
        return networkRequestHelper {
            serviceClient.getServices("Bearer $token")
        }
    }

    suspend fun doCrudServiceDetail(
        requestBody: ServiceDetailDto,
        token: String
    ): Result<List<GeneralResponse>> {
        // El backend exige fld_image siempre: sin imagen nueva (o si no se pudo leer)
        // se manda un archivo vacío y el servicio se guarda sin imagen.
        val image = requestBody.image
            ?.let { imagePartFactory.fromUri(SERVICE_IMAGE_PART, it, filePrefix = "service") }
            ?: imagePartFactory.empty(SERVICE_IMAGE_PART)
        val request = requestBody.toMultipart(image = image)
        return networkRequestHelper {
            serviceClient.doCrudServiceDetail(
                token = "Bearer $token",
                idService = request.idService,
                description = request.description,
                idServiceType = request.idServiceType,
                price = request.price,
                date = request.date,
                provider = request.provider,
                quantity = request.quantity,
                vehicleId = request.vehicleId,
                odometer = request.odometer,
                notes = request.notes,
                image = request.image
            )
        }
    }

    suspend fun deleteService(
        idService: Int,
        token: String
    ): Result<List<GeneralResponse>> {
        return networkRequestHelper {
            serviceClient.deleteService("Bearer $token", idService)
        }
    }

    suspend fun doGetServiceType(
        token: String
    ): Result<List<TypeServiceDto>> {
        return networkRequestHelper {
            serviceClient.doGetServiceType("Bearer $token")
        }
    }
}
