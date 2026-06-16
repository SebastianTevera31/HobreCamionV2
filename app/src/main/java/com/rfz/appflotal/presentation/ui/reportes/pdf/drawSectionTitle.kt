package com.rfz.appflotal.presentation.ui.reportes.pdf

import android.graphics.Canvas
import android.graphics.Paint

fun drawSectionTitle(
    canvas: Canvas,
    title: String,
    x: Float,
    y: Float,
    paint: Paint
): Float {
    canvas.drawText(
        title,
        x,
        y,
        paint
    )

    return y + 20f
}

fun drawPdfRow(
    canvas: Canvas,
    label: String,
    value: String,
    x: Float,
    y: Float,
    labelPaint: Paint,
    valuePaint: Paint,
    rowHeight: Float = 18f
): Float {
    canvas.drawText(
        "$label:",
        x,
        y,
        labelPaint
    )

    canvas.drawText(
        value,
        x + 170f,
        y,
        valuePaint
    )

    return y + rowHeight
}