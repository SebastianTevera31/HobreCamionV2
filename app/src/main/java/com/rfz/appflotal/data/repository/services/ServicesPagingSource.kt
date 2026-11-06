package com.rfz.appflotal.data.repository.services

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi

/**
 * Entrega una página de servicios para la lista (Paging 3), que se navega con
 * Anterior / Siguiente: cada fuente carga solo la página [page] y no pide más
 * (prevKey y nextKey en null). Al cambiar de página se crea una fuente nueva.
 *
 * api/Service/GetServices todavía no acepta pageNumber y devuelve todos los
 * servicios de una vez, así que esta fuente recibe la lista ya descargada (y
 * filtrada) y toma la página de ahí. Cuando el backend pagine, basta con que
 * [load] pida la página [page] al API; la pantalla y el ViewModel no cambian.
 */
class ServicesPagingSource(
    private val services: List<ServiceUi>,
    private val page: Int,
    private val pageSize: Int
) : PagingSource<Int, ServiceUi>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ServiceUi> {
        val start = (page * pageSize).coerceIn(0, services.size)
        val end = (start + pageSize).coerceAtMost(services.size)
        return LoadResult.Page(
            data = services.subList(start, end),
            prevKey = null,
            nextKey = null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, ServiceUi>): Int? = null
}
