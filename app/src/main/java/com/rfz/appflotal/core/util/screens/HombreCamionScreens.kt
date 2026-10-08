package com.rfz.appflotal.core.util.screens

enum class HombreCamionScreens(screenName: String) {

    MARCAS(screenName = "marcas_screen"),
    DISENIO_ORIGINAL(screenName = "diseno_original_screen"),
    DIMENSIONES(screenName = "medidasLlantasScreen"),
    PRODUCTOS(screenName = "productoScreen"),
    LLANTAS(screenName = "registroLlantasScreen"),
    VEHICULOS(screenName = "registroVehiculoScreen"),
    MONTAJE(screenName = "montajeDesmontajeScreen"),
    MONITOR(screenName = "monitorScreen"),
    MAPA_VIAL(screenName = "mapavial"),
    // Mapa abierto como detalle (p. ej. desde Clima): no es pestaña, sin bottom bar y con back.
    MAPA_VIAL_DETALLE(screenName = "mapavialDetalle"),
    WEATHER(screenName = "weatherScreen"),
    ALERTS(screenName = "alertsScreen"),
    REGISTER_TIRES(screenName = "registerTiresScreen"),

    SERVICES(screenName = "services")
}