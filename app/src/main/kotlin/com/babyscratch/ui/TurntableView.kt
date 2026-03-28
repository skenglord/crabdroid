// app/src/main/kotlin/com/babyscratch/ui/TurntableView.kt
// Purpose: Custom Android View rendering a circular vinyl record that responds to touch gestures for scratching.
// Dependencies: ScratchViewModel.kt, Canvas, Paint, MotionEvent, ValueAnimator

package com.babyscratch.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.atan2
import kotlin.math.min

class TurntableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val recordPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.FILL
    }
    
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    
    private val groovePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private var currentAngle = 0f
    private var lastTouchAngle = 0f
    private var isDragging = false
    private var currentVelocity = 0f
    
    private var centerX = 0f
    private var centerY = 0f
    private var recordRadius = 0f

    private var velocityAnimator: ValueAnimator? = null
    
    // OPTIMIZATION: Pre-allocate
    private val markerRect = RectF()

    // Callback to pass velocity updates to the ViewModel
    var onVelocityChanged: ((Float) -> Unit)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        recordRadius = min(w, h) / 2f * 0.9f
        
        // Pre-calculate marker bounds
        markerRect.set(
            centerX - 10f,
            centerY - recordRadius * 0.8f,
            centerX + 10f,
            centerY - recordRadius * 0.6f
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        canvas.save()
        canvas.rotate(currentAngle, centerX, centerY)

        // Draw main record body
        canvas.drawCircle(centerX, centerY, recordRadius, recordPaint)

        // Draw grooves
        for (i in 1..5) {
            val grooveRadius = recordRadius * (0.4f + i * 0.1f)
            canvas.drawCircle(centerX, centerY, grooveRadius, groovePaint)
        }

        // Draw center label
        canvas.drawCircle(centerX, centerY, recordRadius * 0.3f, labelPaint)

        // Draw a marker to show rotation
        canvas.drawRoundRect(markerRect, 5f, 5f, markerPaint)

        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val touchAngle = Math.toDegrees(atan2((y - centerY).toDouble(), (x - centerX).toDouble())).toFloat()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = true
                lastTouchAngle = touchAngle
                velocityAnimator?.cancel()
                currentVelocity = 0f
                onVelocityChanged?.invoke(0f)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    var deltaAngle = touchAngle - lastTouchAngle
                    
                    // Handle wrap-around at -180/180 degrees
                    if (deltaAngle > 180f) deltaAngle -= 360f
                    if (deltaAngle < -180f) deltaAngle += 360f

                    currentAngle += deltaAngle
                    
                    // Map angular delta to a normalized velocity (-1.0 to 1.0 roughly)
                    // The divisor controls the "sensitivity" of the scratch
                    currentVelocity = deltaAngle / 15f 
                    
                    onVelocityChanged?.invoke(currentVelocity)
                    lastTouchAngle = touchAngle
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                
                // Apply momentum decay (exponential inertia)
                velocityAnimator = ValueAnimator.ofFloat(currentVelocity, 0f).apply {
                    duration = 500 // 500ms decay
                    interpolator = DecelerateInterpolator(2f)
                    addUpdateListener { animator ->
                        val vel = animator.animatedValue as Float
                        currentAngle += vel * 15f // Re-apply to angle for visual spin-down
                        onVelocityChanged?.invoke(vel)
                        invalidate()
                    }
                    start()
                }
            }
        }
        return super.onTouchEvent(event)
    }
}
