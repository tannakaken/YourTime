package org.tannakaken.yourtime

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tLayout = ConstraintLayout(this)
        setContentView(tLayout)
        val tView = ClockView(this)
        tLayout.addView(tView)
        val tAnimation = ClockAnimation(tView)
        tAnimation.repeatCount = -1
        tView.startAnimation(tAnimation)
    }

    class ClockView(aContext: Context) : View(aContext) {
        private val mPaint = Paint()
        var mSecond: Int
        var mMinute: Int
        var mHour: Float
        init {
            mSecond = 0
            mMinute = 0
            mHour = 0F
        }
        override fun onDraw(aCanvas: Canvas?) {
            val tCenterX = width / 2F
            val tCenterY = height / 2F
            drawDial(aCanvas, tCenterX, tCenterY)
            val tRadius = Math.min(tCenterX, tCenterY) * 0.8F
            mPaint.color = Color.rgb(0,0,0)
            mPaint.strokeWidth = 2F
            aCanvas!!.drawLine(tCenterX, tCenterY, tCenterX + tRadius * Math.cos(sec2Rad(mSecond)).toFloat(), tCenterY + tRadius * Math.sin(sec2Rad(mSecond)).toFloat(), mPaint)
            mPaint.strokeWidth = 4F
            aCanvas.drawLine(tCenterX, tCenterY, tCenterX + tRadius * 0.8F * Math.cos(sec2Rad(mMinute)).toFloat(), tCenterY + tRadius * 0.8F * Math.sin(sec2Rad(mMinute)).toFloat(), mPaint)
            mPaint.strokeWidth = 8F
            aCanvas.drawLine(tCenterX, tCenterY, tCenterX + tRadius / 2 * Math.cos(hour2Rad(mHour)).toFloat(), tCenterY + tRadius / 2 * Math.sin(hour2Rad(mHour)).toFloat(), mPaint)
        }

        private fun sec2Rad(aSecond : Int) : Double {
            return (aSecond * 6 - 90) / 180.0 * Math.PI
        }

        private fun hour2Rad(aHour: Float) : Double {
            return (aHour * 30 - 90) / 180.0 * Math.PI
        }

        private fun drawDial(aCanvas: Canvas?, aCenterX: Float, aCenterY: Float) {
            mPaint.textSize = Math.min(aCenterX, aCenterY) * 0.1F
            val tRadius = Math.min(aCenterX, aCenterY) * 0.9F
            for (i in 0..11) {
                drawNum(aCanvas, i, aCenterX + tRadius * Math.cos(hour2Rad(i.toFloat())).toFloat(), aCenterY + tRadius * Math.sin(hour2Rad(i.toFloat())).toFloat())
            }
        }

        private fun drawNum(aCanvas:Canvas?, aNum: Int, x: Float, y: Float) {
            val tWidth = mPaint.measureText(aNum.toString())
            val tMetrix = mPaint.fontMetrics
            aCanvas!!.drawText(aNum.toString(), x - tWidth/2, y - (tMetrix.ascent + tMetrix.descent) / 2, mPaint)
        }

    }

    class ClockAnimation(val mView: ClockView) : Animation() {
        private var mCurrentPosition : Int
        init {
            mCurrentPosition = mView.mSecond
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val tCal = Calendar.getInstance(TimeZone.getDefault())
            tCal.time = Date()
            mView.mSecond = tCal.get(Calendar.SECOND)
            mView.mMinute = tCal.get(Calendar.MINUTE)
            mView.mHour = tCal.get(Calendar.HOUR) + mView.mMinute / 60F

            mView.requestLayout()
        }
    }
}
