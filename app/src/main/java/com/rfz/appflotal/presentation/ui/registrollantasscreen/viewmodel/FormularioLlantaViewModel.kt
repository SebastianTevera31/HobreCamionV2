package com.rfz.appflotal.presentation.ui.registrollantasscreen.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rfz.appflotal.data.model.tire.response.TireIdResponse
import com.rfz.appflotal.domain.acquisitiontype.AcquisitionTypeUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.product.ProductListUseCase
import com.rfz.appflotal.domain.tire.TireCrudUseCase
import com.rfz.appflotal.domain.tire.TireEditPolicy
import com.rfz.appflotal.domain.tire.TireGetUseCase
import com.rfz.appflotal.presentation.navigation.RutaFormularioLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.CampoLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.CamposLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ProductoUi
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.TipoAdquisicionUi
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.ValidadorFormularioLlanta
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.construirTireCrudDto
import com.rfz.appflotal.presentation.ui.registrollantasscreen.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FormularioLlantaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTasksUseCase: GetTasksUseCase,
    private val acquisitionTypeUseCase: AcquisitionTypeUseCase,
    private val productListUseCase: ProductListUseCase,
    private val tireGetUseCase: TireGetUseCase,
    private val tireCrudUseCase: TireCrudUseCase,
) : ViewModel() {

    private val idLlanta: Int? = savedStateHandle.toRoute<RutaFormularioLlanta>().idLlanta

    private val _uiState = MutableStateFlow(FormularioLlantaUiState(esEdicion = idLlanta != null))
    val uiState: StateFlow<FormularioLlantaUiState> = _uiState.asStateFlow()

    private val _eventos = Channel<EventoFormularioLlanta>(Channel.BUFFERED)
    val eventos: Flow<EventoFormularioLlanta> = _eventos.receiveAsFlow()

    private var token: String = ""
    private var camposIniciales = CamposLlanta()

    /** Ficha original en edición: con adquisición bloqueada se reenvían sus datos. */
    private var fichaOriginal: TireIdResponse? = null

    init {
        cargar()
    }

    fun onReintentar() = cargar()

    /** Al volver de crear un producto, el catálogo vacío se vuelve a pedir. */
    fun recargarProductosSiFaltan() {
        val estado = _uiState.value
        if (estado.estadoCarga != EstadoCarga.LISTO || estado.productos.isNotEmpty()) return
        viewModelScope.launch {
            val productos = runCatching { productListUseCase(token).getOrThrow() }.getOrNull()
                ?: return@launch
            _uiState.update { it.copy(productos = productos.map { p -> p.toUi() }) }
        }
    }

    fun onTipoAdquisicionSeleccionado(tipo: TipoAdquisicionUi) =
        actualizar(CampoLlanta.TIPO_ADQUISICION) { it.copy(tipoAdquisicion = tipo) }

    // La profundidad se precarga con la del producto y luego se ajusta por llanta.
    fun onProductoSeleccionado(producto: ProductoUi) =
        actualizar(CampoLlanta.PRODUCTO, CampoLlanta.PROFUNDIDAD) {
            it.copy(producto = producto, profundidad = producto.profundidad.toString())
        }

    fun onFechaSeleccionada(fechaIso: String) =
        actualizar(CampoLlanta.FECHA) { it.copy(fechaAdquisicion = fechaIso) }

    fun onCostoCambiado(valor: String) {
        if (NUMERO_DECIMAL.matches(valor)) actualizar(CampoLlanta.COSTO) { it.copy(costo = valor) }
    }

    fun onProfundidadCambiada(valor: String) {
        if (NUMERO_DECIMAL.matches(valor)) {
            actualizar(CampoLlanta.PROFUNDIDAD) { it.copy(profundidad = valor) }
        }
    }

    fun onFolioCambiado(valor: String) = actualizar { it.copy(folioFactura = valor) }

    fun onNumeroLlantaCambiado(valor: String) {
        val actual = _uiState.value.campos.numeroLlanta
        if (cabe(valor, actual, ValidadorFormularioLlanta.MAX_NUMERO_LLANTA)) {
            actualizar(CampoLlanta.NUMERO_LLANTA) { it.copy(numeroLlanta = valor) }
        }
    }

    fun onDotCambiado(valor: String) {
        if (cabe(valor, _uiState.value.campos.dot, ValidadorFormularioLlanta.MAX_DOT)) {
            actualizar(CampoLlanta.DOT) { it.copy(dot = valor) }
        }
    }

    fun onGuardar() {
        val estado = _uiState.value
        if (estado.guardando || estado.estadoCarga != EstadoCarga.LISTO) return

        val errores = ValidadorFormularioLlanta.validar(estado.campos, estado.adquisicionEditable)
        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(errores = errores) }
            _eventos.trySend(EventoFormularioLlanta.CamposIncompletos)
            return
        }

        val dto = construirTireCrudDto(
            idLlanta = idLlanta ?: 0,
            campos = estado.campos,
            fichaBloqueada = fichaOriginal.takeIf { !estado.adquisicionEditable },
            fechaRegistro = LocalDateTime.now(),
        )
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }
            val resultado = runCatching { tireCrudUseCase(token, dto).getOrThrow() }
            _uiState.update { it.copy(guardando = false) }
            resultado
                .onSuccess {
                    _uiState.update { it.copy(hayCambios = false) }
                    _eventos.send(EventoFormularioLlanta.Guardada)
                }
                .onFailure { _eventos.send(EventoFormularioLlanta.ErrorAlGuardar(it.message)) }
        }
    }

    private fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(estadoCarga = EstadoCarga.CARGANDO) }
            val usuario = getTasksUseCase().first().firstOrNull()
            token = "Bearer ${usuario?.fld_token.orEmpty()}"
            val wizardActivo = usuario?.passWizard == false

            val tiposPendientes = async { runCatching { acquisitionTypeUseCase(token).getOrThrow() } }
            val productosPendientes = async { runCatching { productListUseCase(token).getOrThrow() } }
            val tipos = tiposPendientes.await().getOrNull()?.map { it.toUi() }
            val productos = productosPendientes.await().getOrNull()?.map { it.toUi() }
            if (tipos == null || productos == null) {
                _uiState.update { it.copy(estadoCarga = EstadoCarga.ERROR) }
                return@launch
            }

            // Sin la ficha no se permite editar: guardar pisaría los datos reales.
            val ficha = idLlanta?.let { id ->
                runCatching { tireGetUseCase(id).getOrThrow().firstOrNull() }.getOrNull()
                    ?: run {
                        _uiState.update { it.copy(estadoCarga = EstadoCarga.ERROR) }
                        return@launch
                    }
            }
            fichaOriginal = ficha
            val campos = ficha?.toCampos(tipos, productos)
                ?: CamposLlanta(fechaAdquisicion = LocalDate.now().toString())
            camposIniciales = campos

            _uiState.update {
                it.copy(
                    estadoCarga = EstadoCarga.LISTO,
                    tiposAdquisicion = tipos,
                    productos = productos,
                    campos = campos,
                    adquisicionEditable = ficha == null || TireEditPolicy.canEditAcquisitionData(
                        isWizardActive = wizardActivo,
                        hasInspection = ficha.hasInspection
                    ),
                    errores = emptyMap(),
                    hayCambios = false,
                )
            }
        }
    }

    private fun actualizar(vararg limpiar: CampoLlanta, cambio: (CamposLlanta) -> CamposLlanta) {
        _uiState.update {
            val campos = cambio(it.campos)
            it.copy(
                campos = campos,
                errores = it.errores - limpiar.toSet(),
                hayCambios = campos != camposIniciales,
            )
        }
    }

    // Permite borrar aunque un valor guardado ya exceda el máximo.
    private fun cabe(nuevo: String, actual: String, maximo: Int) =
        nuevo.length <= maximo || nuevo.length < actual.length

    private fun TireIdResponse.toCampos(
        tipos: List<TipoAdquisicionUi>,
        productos: List<ProductoUi>,
    ) = CamposLlanta(
        tipoAdquisicion = tipos.find { it.id == typeAcquisitionId },
        producto = productos.find { it.id == productId },
        fechaAdquisicion = acquisitionDate,
        costo = unitCost.toString(),
        folioFactura = document,
        // "16.0" se muestra como "16" para que el usuario edite el número que conoce.
        profundidad = treadDepth.toBigDecimal().stripTrailingZeros().toPlainString(),
        numeroLlanta = tireNumber,
        dot = dot,
    )

    private companion object {
        val NUMERO_DECIMAL = Regex("""\d*(\.\d{0,2})?""")
    }
}
