package com.rfz.appflotal.presentation.ui.reportes.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.rfz.appflotal.R
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
        var logoBottomY = y

        // Draw Logo
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
            val logoWidth = 80
            val logoHeight = (logoBitmap.height.toFloat() / logoBitmap.width.toFloat() * logoWidth).toInt()
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoWidth, logoHeight, true)
            canvas.drawBitmap(scaledLogo, pageWidth - margin - logoWidth, 20f, null)
            logoBottomY = margin + logoHeight
        } catch (e: Exception) {
            Log.e("PDF_GEN", "Logo could not be loaded", e)
        }

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
            context.getString(R.string.pdf_header_title),
            margin,
            y,
            titlePaint
        )
        
        y += 20f

        canvas.drawText(
            "${context.getString(R.string.pdf_label_date)}: $dateText",
            margin,
            y,
            subtitlePaint
        )

        y += 12f

        canvas.drawText(
            context.getString(R.string.pdf_system_name),
            margin,
            y,
            subtitlePaint
        )

        // Asegurarnos de que la línea esté debajo tanto del texto como del logo
        y = maxOf(y + 20f, logoBottomY + 10f)

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
            context.getString(R.string.pdf_label_position, tirePosition),
            margin,
            y,
            labelPaint
        )

        val idText = context.getString(R.string.pdf_label_id, report.idTire)
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
                title = context.getString(R.string.pdf_section_tire_info),
                x = margin,
                y = y,
                paint = sectionPaint
            )

            y = drawPdfRow(
                canvas,
                context.getString(R.string.pdf_label_serial),
                report.tireNumber,
                margin,
                y,
                labelPaint,
                valuePaint
            )
            y = drawPdfRow(canvas, context.getString(R.string.pdf_label_brand), tire.brand, margin, y, labelPaint, valuePaint)
            y = drawPdfRow(canvas, context.getString(R.string.pdf_label_model), tire.model, margin, y, labelPaint, valuePaint)
            y = drawPdfRow(canvas, context.getString(R.string.pdf_label_size), tire.size, margin, y, labelPaint, valuePaint)

            if (report.renovatedDesign.isNotBlank()) {
                y = drawPdfRow(
                    canvas,
                    context.getString(R.string.pdf_label_retread_design),
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
            title = context.getString(R.string.pdf_section_operation),
            x = margin,
            y = y,
            paint = sectionPaint
        )

        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_odometer),
            "${report.differenceOdometer} km",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_distance),
            "${report.differenceOdometer} km",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_initial_depth),
            "${formatDecimal(tire?.thread ?: 0.0)} mm",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_wear),
            "${formatDecimal(report.differenceInTreadDepth.toDouble())} mm",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_current_depth),
            "${formatDecimal(finalDepth)} mm",
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_lifecycle),
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
            title = context.getString(R.string.pdf_section_efficiency),
            x = margin,
            y = y,
            paint = sectionPaint
        )

        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_performance),
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
            title = context.getString(R.string.pdf_section_costs),
            x = margin,
            y = y,
            paint = sectionPaint
        )

        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_unit_cost),
            formatCurrency(report.unitCost),
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_cost_mm),
            formatCurrency(report.costByMm),
            margin,
            y,
            labelPaint,
            valuePaint
        )
        y = drawPdfRow(
            canvas,
            context.getString(R.string.pdf_label_cpk),
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

        val signatureText = context.getString(R.string.pdf_signature)
        canvas.drawText(
            signatureText,
            (pageWidth - subtitlePaint.measureText(signatureText)) / 2f,
            signatureY + 16f,
            subtitlePaint
        )

        // Footer
        val footerText = context.getString(R.string.pdf_footer)
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