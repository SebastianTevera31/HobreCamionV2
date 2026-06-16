package com.rfz.appflotal.presentation.ui.reportes.pdf

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.rfz.appflotal.data.model.report.CpkReportResponse
import com.rfz.appflotal.data.model.tire.Tire
import com.rfz.appflotal.presentation.ui.reportes.viewmodel.formatCurrency
import com.rfz.appflotal.presentation.ui.reportes.viewmodel.formatDecimal
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createRendimientoPdf(
    context: Context,
    tirePosition: String,
    report: CpkReportResponse,
    tire: Tire?,
    fileName: String = "reporte_rendimiento_${System.currentTimeMillis()}.pdf"
): Uri? {
    val pdfDocument = PdfDocument()

    return try {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        val pageInfo = PdfDocument.PageInfo.Builder(
            pageWidth,
            pageHeight,
            1
        ).create()

        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        canvas.drawColor(android.graphics.Color.WHITE)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(26, 35, 126)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.GRAY
            textSize = 9f
        }

        val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(26, 35, 126)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.DKGRAY
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.BLACK
            textSize = 10f
        }

        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(26, 35, 126)
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(26, 35, 126)
            strokeWidth = 2f
        }

        val grayLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 1f
        }

        var y = margin

        val finalDepth = maxOf(
            0.0,
            (tire?.thread ?: 0.0) - report.differenceInTreadDepth.toDouble()
        )

        val dateText = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())

        // Header
        canvas.drawText(
            "REPORTE DE RENDIMIENTO",
            margin,
            y,
            titlePaint
        )

        canvas.drawText(
            dateText,
            pageWidth - margin - valuePaint.measureText(dateText),
            y,
            subtitlePaint
        )

        y += 16f

        canvas.drawText(
            "Sistema Hombre Camión - Flotal",
            margin,
            y,
            subtitlePaint
        )

        y += 18f

        canvas.drawLine(
            margin,
            y,
            pageWidth - margin,
            y,
            linePaint
        )

        y += 28f

        // Posición e ID
        canvas.drawText(
            "Posición: Rueda $tirePosition",
            margin,
            y,
            labelPaint
        )

        val idText = "ID Llanta: ${report.idTire}"
        canvas.drawText(
            idText,
            pageWidth - margin - valuePaint.measureText(idText),
            y,
            valuePaint
        )

        y += 26f

        // Información del neumático
        if (tire != null) {
            y = drawSectionTitle(
                canvas = canvas,
                title = "Información del Neumático",
                x = margin,
                y = y,
                paint = sectionPaint
            )

            y = drawPdfRow(
                canvas,
                "Número de Serie",
                report.tireNumber,
                margin,
                y,
                labelPaint,
                valuePaint
            )
            y = drawPdfRow(canvas, "Marca", tire.brand, margin, y, labelPaint, valuePaint)
            y = drawPdfRow(canvas, "Modelo", tire.model, margin, y, labelPaint, valuePaint)
            y = drawPdfRow(canvas, "Medida", tire.size, margin, y, labelPaint, valuePaint)

            if (report.renovatedDesign.isNotBlank()) {
                y = drawPdfRow(
                    canvas,
                    "Diseño Renovado",
                    report.renovatedDesign,
                    margin,
                    y,
                    labelPaint,
                    valuePaint
                )
            }

            y += 10f
        }

        // Resumen de operación
        y = drawSectionTitle(
            canvas = canvas,
            title = "Resumen de Operación",
            x = margin,
            y = y,
            paint = sectionPaint
        )

        y = drawPdfRow(
            canvas,
            "Odómetro Actual",
            "${report.differenceOdometer} km",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            "Distancia Recorrida",
            "${report.differenceOdometer} km",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            "Profundidad Inicial",
            "${formatDecimal(tire?.thread ?: 0.0)} mm",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            "Desgaste de Piso",
            "${formatDecimal(report.differenceInTreadDepth.toDouble())} mm",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            "Profundidad Actual",
            "${formatDecimal(finalDepth)} mm",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            "Ciclo de Vida",
            report.lifeCycle.toString(),
            margin,
            y,
            labelPaint,
            valuePaint
        )

        y += 10f

        // Eficiencia
        y = drawSectionTitle(
            canvas = canvas,
            title = "Eficiencia",
            x = margin,
            y = y,
            paint = sectionPaint
        )

        y = drawPdfRow(
            canvas,
            "Rendimiento (km/mm)",
            "${formatDecimal(report.kmPerMm)} km/mm",
            margin,
            y,
            labelPaint,
            valuePaint
        )

        y += 10f

        // Costos
        y = drawSectionTitle(
            canvas = canvas,
            title = "Análisis de Costos",
            x = margin,
            y = y,
            paint = sectionPaint
        )

        y = drawPdfRow(
            canvas,
            "Costo Unitario",
            formatCurrency(report.unitCost),
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            "Costo por mm",
            formatCurrency(report.costByMm),
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            "Costo por km (CPK)",
            formatCurrency(report.costPerKm),
            margin,
            y,
            labelPaint,
            highlightPaint
        )

        // Firma
        val signatureY = pageHeight - 95f
        val signatureWidth = 200f
        val signatureStartX = (pageWidth - signatureWidth) / 2f

        canvas.drawLine(
            signatureStartX,
            signatureY,
            signatureStartX + signatureWidth,
            signatureY,
            grayLinePaint
        )

        val signatureText = "Firma Responsable"
        canvas.drawText(
            signatureText,
            (pageWidth - subtitlePaint.measureText(signatureText)) / 2f,
            signatureY + 16f,
            subtitlePaint
        )

        // Footer
        val footerText =
            "Este reporte es un documento informativo generado por la aplicación Hombre Camión."
        canvas.drawText(
            footerText,
            (pageWidth - subtitlePaint.measureText(footerText)) / 2f,
            pageHeight - 32f,
            subtitlePaint
        )

        pdfDocument.finishPage(page)

        val folder = File(
            context.cacheDir,
            "shared_pdfs"
        ).apply {
            mkdirs()
        }

        val file = File(folder, fileName)
        Log.d("PDF_GEN", "Generating PDF at: ${file.absolutePath}")

        FileOutputStream(file).use { outputStream ->
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
        }

        Log.d("PDF_GEN", "File size after write: ${file.length()} bytes")

        if (!file.exists() || file.length() == 0L) {
            Log.e("PDF_GEN", "File does not exist or is empty")
            return null
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: Exception) {
        Log.e("PDF_GEN", "Error generating PDF", e)
        e.printStackTrace()
        null
    } finally {
        pdfDocument.close()
    }
}