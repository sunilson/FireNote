package com.sunilson.firenote.presentation.elements.note.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.res.ResourcesCompat
import android.util.AttributeSet
import android.widget.TextView
import com.sunilson.firenote.R

class LinedTextView(context: Context, attrs: AttributeSet) : TextView(context, attrs) {

    private val paint: Paint = Paint()
    private val rect: Rect = Rect()

    init {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = ResourcesCompat.getColor(resources, R.color.primary_text_color, null)
    }

    override fun onDraw(canvas: Canvas?) {

        //Calculate how many lines the EditText currently has
        var count = height / lineHeight
        if (lineCount > count) count = lineCount

        var baseline = getLineBounds(0, rect)

        //Draw a line for each visible line
        for (i in 0 until count) {
            canvas?.drawLine(rect.left.toFloat(), (baseline + 1).toFloat(), rect.right.toFloat(), (baseline + 1).toFloat(), paint)
            baseline += lineHeight
        }

        super.onDraw(canvas)
    }
}