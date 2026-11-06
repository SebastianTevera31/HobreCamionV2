package com.rfz.appflotal.data.model.services.dto

import android.net.Uri
import com.google.gson.annotations.SerializedName

/**
 * Alta/edición de un servicio (api/Service/CrudService).
 *
 * Se envía como multipart (ver [toMultipart]); [image] es la imagen local elegida
 * por el usuario. Si es null no se manda la parte de imagen y el backend conserva
 * la que ya tenía el servicio.
 */
data class ServiceDetailDto(
    @SerializedName("id_service") val idService: Int,
    @SerializedName("fld_description") val description: String,
    @SerializedName("id_serviceType") val idServiceType: Int,
    @SerializedName("fld_price") val price: Double,
    @SerializedName("fld_date") val date: String,
    @SerializedName("fld_provider") val provider: String,
    @SerializedName("fld_cant") val quantity: Int,
    @SerializedName("p_vehicle_fk_1") val vehicleId: Int,
    @SerializedName("fld_notes") val notas: String,
    @Transient val image: Uri? = null
)
