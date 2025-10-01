package com.rfz.appflotal.data.network.service.fgservice

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

class HombreCamionServiceController @Inject constructor() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var jobUpdateStatus: Job? = null
}