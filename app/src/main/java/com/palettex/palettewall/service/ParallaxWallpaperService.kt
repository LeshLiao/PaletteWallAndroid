package com.palettex.palettewall.service

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.Choreographer
import android.view.SurfaceHolder
import com.palettex.palettewall.R

class ParallaxWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return ParallaxEngine()
    }

    inner class ParallaxEngine : Engine(), SensorEventListener, Choreographer.FrameCallback {
        private val paint = Paint()
        private var wallpaperBitmap: Bitmap? = null
        private val matrix = Matrix()

        private var offsetX = 0f
        private var offsetY = 0f
        private var width = 0
        private var height = 0

        private lateinit var sensorManager: SensorManager
        private var rotationVectorSensor: Sensor? = null

        // Low-pass filter variables
        private val filteredOrientation = FloatArray(3)
        private val alpha = 0.1f

        private var visible = true

        // Maximum parallax offsets
        private var maxOffsetX = 0f
        private var maxOffsetY = 0f

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            Log.d("GDT","ParallaxWallpaperService (onCreate)")
            paint.isAntiAlias = true
            paint.isFilterBitmap = true

            // Load your wallpaper image
            wallpaperBitmap = BitmapFactory.decodeResource(resources, R.drawable.wallpaper_image)

            // Initialize SensorManager and Rotation Vector Sensor
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            rotationVectorSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            this.width = width
            this.height = height

            // Determine maximum possible offset based on intensity and sensor values
            val intensity = 100f // Same as in onSensorChanged
            val maxSensorValue = Math.PI.toFloat() / 2f // Max value for orientation angles
            maxOffsetX = maxSensorValue * intensity
            maxOffsetY = maxSensorValue * intensity

            // Calculate the extra width and height needed
            val extraWidth = (maxOffsetX * 2).toInt()
            val extraHeight = (maxOffsetY * 2).toInt()

            // Scale the bitmap to cover the screen plus maximum offset
            wallpaperBitmap = wallpaperBitmap?.let {
                Bitmap.createScaledBitmap(
                    it,
                    width + extraWidth,
                    height + extraHeight,
                    true
                )
            }

            drawFrame()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                Choreographer.getInstance().postFrameCallback(this)
            } else {
                Choreographer.getInstance().removeFrameCallback(this)
            }
        }

        override fun doFrame(frameTimeNanos: Long) {
            if (visible) {
                drawFrame()
                Choreographer.getInstance().postFrameCallback(this)
            }
        }

        private fun drawFrame() {
            val surfaceHolder = surfaceHolder
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null) {
                try {
                    Log.d("GDT","drawFrame() $offsetX , $offsetY" )
                    canvas.save()

                    // Apply parallax translation with matrix
                    matrix.reset()
                    // Center the image and apply offset
                    matrix.postTranslate(-maxOffsetX + offsetX, -maxOffsetY + offsetY)
                    canvas.concat(matrix)

                    // Draw the wallpaper image
                    wallpaperBitmap?.let {
                        canvas.drawBitmap(it, 0f, 0f, paint)
                    }

                    canvas.restore()
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                // Convert rotation vector to orientation angles
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                // Apply low-pass filter
                for (i in 0..2) {
                    filteredOrientation[i] =
                        alpha * orientation[i] + (1 - alpha) * filteredOrientation[i]
                }

                // Adjust offsets based on filtered orientation values
                val intensity = 100f // Adjust intensity for desired effect
                offsetX = filteredOrientation[2] * intensity
                offsetY = filteredOrientation[1] * intensity

                // Limit offsets to prevent moving out of bounds
                offsetX = offsetX.coerceIn(-maxOffsetX, maxOffsetX)
                offsetY = offsetY.coerceIn(-maxOffsetY, maxOffsetY)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // No action needed for accuracy changes
        }

        override fun onDestroy() {
            super.onDestroy()
            sensorManager.unregisterListener(this)
            wallpaperBitmap?.recycle()
            wallpaperBitmap = null
            Choreographer.getInstance().removeFrameCallback(this)
        }
    }
}
