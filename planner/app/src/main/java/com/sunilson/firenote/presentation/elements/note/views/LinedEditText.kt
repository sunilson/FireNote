package com.sunilson.firenote.presentation.elements.note.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.widget.EditText
import com.sunilson.firenote.R

class LinedEditText(context: Context, attrs: AttributeSet) : EditText(context, attrs) {

    private val paint: Paint = Paint()
    private val rect: Rect = Rect()
    private var mEnabled: Boolean = false
        set(value) {
            field = value
            super.setEnabled(value)
        }

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            if (!isEnabled) return
            super.setEnabled(false)
            super.setEnabled(mEnabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}