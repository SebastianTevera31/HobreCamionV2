package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.rfz.appflotal.data.model.catalogo.CatalogoEntity
import com.rfz.appflotal.data.model.catalogo.DescargaCatalogoEntity
import com.rfz.appflotal.data.model.catalogo.LlantaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogoDao {
    @Query("SELECT * FROM catalogo WHERE tipo = :tipo ORDER BY id")
    suspend fun obtener(tipo: String): List<CatalogoEntity>

    @Query("DELETE FROM catalogo WHERE tipo = :tipo")
    suspend fun borrar(tipo: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(elementos: List<CatalogoEntity>)

    /** Reemplaza la lista completa de un tipo: lo que el servidor ya no envía deja de existir. */
    @Transaction
    suspend fun reemplazar(tipo: String, elementos: List<CatalogoEntity>, descarga: DescargaCatalogoEntity) {
        borrar(tipo)
        insertar(elementos)
        registrarDescarga(descarga)
    }

    @Query("SELECT * FROM llanta ORDER BY idLlanta")
    suspend fun obtenerLlantas(): List<LlantaEntity>

    @Query("DELETE FROM llanta")
    suspend fun borrarLlantas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLlantas(llantas: List<LlantaEntity>)

    @Transaction
    suspend fun reemplazarLlantas(llantas: List<LlantaEntity>, descarga: DescargaCatalogoEntity) {
        borrarLlantas()
        insertarLlantas(llantas)
        registrarDescarga(descarga)
    }

    @Upsert
    suspend fun registrarDescarga(descarga: DescargaCatalogoEntity)

    @Query("SELECT * FROM descarga_catalogo")
    fun observarDescargas(): Flow<List<DescargaCatalogoEntity>>
}
