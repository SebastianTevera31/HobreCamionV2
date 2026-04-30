package com.rfz.appflotal.data.repository.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.rfz.appflotal.data.model.location.LocationData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedClient: FusedLocationProviderClient
) {

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    // Obtener última ubicación conocida: rápido, pero puede estar desactualizada.
    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    suspend fun getLastLocation(): LocationData? {
        if (!hasLocationPermission()) return null

        val location = fusedClient.lastLocation.await() ?: return null

        return reverseGeocode(
            lat = location.latitude,
            lng = location.longitude
        )
    }

    // Solicitar ubicación actual. Puede devolver una ubicación cacheada de hasta 30 s.
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    suspend fun getFreshLocation(): LocationData? {
        if (!hasLocationPermission()) return null

        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setDurationMillis(5_000)
            .setMaxUpdateAgeMillis(30_000)
            .build()

        val cancellationTokenSource = CancellationTokenSource()

        val location = fusedClient
            .getCurrentLocation(request, cancellationTokenSource.token)
            .await(cancellationTokenSource)
            ?: return null

        return reverseGeocode(
            lat = location.latitude,
            lng = location.longitude
        )
    }

    // Coordenadas -> dirección.
    private suspend fun reverseGeocode(
        lat: Double,
        lng: Double
    ): LocationData? {
        if (!Geocoder.isPresent()) return null

        val geocoder = Geocoder(context, Locale.US)

        val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationAsync(lat, lng).firstOrNull()
        } else {
            withContext(Dispatchers.IO) {
                try {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lng, 1)
                        ?.firstOrNull()
                } catch (_: IOException) {
                    null
                } catch (_: IllegalArgumentException) {
                    null
                }
            }
        }

        return address?.toLocationData(lat, lng)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun Geocoder.getFromLocationAsync(
        lat: Double,
        lng: Double
    ): List<Address> = suspendCancellableCoroutine { cont ->
        getFromLocation(
            lat,
            lng,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (cont.isActive) {
                        cont.resume(addresses)
                    }
                }

                override fun onError(errorMessage: String?) {
                    if (cont.isActive) {
                        cont.resume(emptyList())
                    }
                }
            }
        )
    }

    private fun Address.toLocationData(
        lat: Double,
        lng: Double
    ) = LocationData(
        lat = lat,
        lng = lng,
        pais = countryName,
        estado = adminArea,
        municipio = subAdminArea,
        ciudad = locality,
        colonia = subLocality,
        codigoPostal = postalCode
    )
}