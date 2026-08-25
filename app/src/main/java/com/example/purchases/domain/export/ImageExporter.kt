package com.example.purchases.domain.export

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.example.purchases.data.database.entity.ShoppingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

class ImageExporter {
    suspend fun exportToImage(
        context: Context,
        items: List<ShoppingItem>,
        listName: String,
        style: ExportStyle
    ): File = withContext(Dispatchers.IO) {

        val imageWidth = style.imageWidth
        val totalHeight = style.headerHeightPx + items.size * style.itemHeightPx + style.paddingPx * 2

        val bitmap = createBitmap(imageWidth, totalHeight)
        val canvas = Canvas(bitmap)
        canvas.drawColor(style.backgroundColor)

        val titlePaint = Paint().apply {
            color = style.titleColor
            textSize = style.titleTextSize
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val itemPaint = Paint().apply {
            color = style.itemColor
            textSize = style.itemTextSize
            isAntiAlias = true
        }
        val checkedPaint = Paint().apply {
            color = style.checkedColor
            textSize = style.itemTextSize
            isAntiAlias = true
        }

        val dividerPaint = Paint().apply {
            color = style.dividerColor
            strokeWidth = style.dividerStrokeWidth
        }

        val strikePaint = Paint().apply {
            color = style.strikeColor
            isAntiAlias = true
        }

        val paddingX = style.paddingPx.toFloat()
        var y = style.paddingPx.toFloat()

        // Заголовок
        canvas.drawText(listName, style.paddingPx.toFloat(), y + style.titleTextSize, titlePaint)
        y += style.titleTextSize + 20f
        canvas.drawLine(style.paddingPx.toFloat(), y, (imageWidth - style.paddingPx).toFloat(), y,  dividerPaint)
        y += 40f

        items.forEachIndexed { index, item ->
            val text = "${index + 1}. ${item.name}"
            if (item.isChecked) {
                canvas.drawText(text, paddingX, y, checkedPaint)
                canvas.drawText("✓", (imageWidth - style.paddingPx - 60).toFloat(), y, checkedPaint)

                val right = (imageWidth - style.paddingPx).toFloat()
                val top = y - style.itemTextSize / 2 - style.strikeHeightPx / 2
                val bottom = y - style.itemTextSize / 2 + style.strikeHeightPx / 2
                canvas.drawRect(paddingX, top, right, bottom, strikePaint)
            } else {
                canvas.drawText(text, style.paddingPx.toFloat(), y, itemPaint)
            }
            y += style.itemHeightPx
        }

        val file = File(context.filesDir, "$listName.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        file
    }

    fun shareImageFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Поделиться изображением"))
    }

    fun openImageForPreview(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/png")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }
}