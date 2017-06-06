package org.tannakaken.yourtime

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mSectionPagerAdapter : SectionPagerAdapter by lazy {
        SectionPagerAdapter(supportFragmentManager)
    }

    private val mViewPager : ViewPager by lazy {
        findViewById(R.id.container) as ViewPager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activiy_main)
        findViewById(R.id.main_conf_button).setOnClickListener {
            val tIntent = Intent(application, TimeConfActivity::class.java)
            tIntent.putExtra("index", ClockList.currentClockIndex)
            startActivity(tIntent)
        }
        findViewById(R.id.main_list_button).setOnClickListener {
            startActivity(Intent(application, TimeListActivity::class.java))
        }
        mViewPager.adapter = mSectionPagerAdapter
        mViewPager.setCurrentItem(ClockList.currentClockIndex, false)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                ClockList.currentClockIndex = position
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    class ClockView(aContext: Context, val mClock: MyClock) : View(aContext) {
        private val mPaint = Paint()
        var mSecond: Int = 0
        var mMinute: Int = 0
        var mHour: Float = 0F

        override fun onDraw(aCanvas: Canvas?) {
            if (aCanvas == null) {
                return
            }
            val tCenterX = width / 2F
            val tCenterY = height / 2F
            drawName(aCanvas)
            drawDial(aCanvas, tCenterX, tCenterY)
            val tRadius = Math.min(tCenterX, tCenterY) * 0.8F
            mPaint.color = Color.rgb(0,0,0)
            mPaint.strokeWidth = 2F
            aCanvas.drawLine(tCenterX, tCenterY, tCenterX + tRadius * Math.cos(sec2Rad(mSecond)).toFloat(), tCenterY + tRadius * Math.sin(sec2Rad(mSecond)).toFloat(), mPaint)
            mPaint.strokeWidth = 4F
            aCanvas.drawLine(tCenterX, tCenterY, tCenterX + tRadius * 0.8F * Math.cos(min2Rad(mMinute)).toFloat(), tCenterY + tRadius * 0.8F * Math.sin(min2Rad(mMinute)).toFloat(), mPaint)
            mPaint.strokeWidth = 8F
            aCanvas.drawLine(tCenterX, tCenterY, tCenterX + tRadius / 2 * Math.cos(hour2Rad(mHour)).toFloat(), tCenterY + tRadius / 2 * Math.sin(hour2Rad(mHour)).toFloat(), mPaint)
        }

        private fun sec2Rad(aSecond : Int) : Double = ((aSecond * 2.0) / mClock.seconds - 0.5) * Math.PI

        private fun min2Rad(aMinute : Int) : Double = ((aMinute * 2.0) / mClock.minutes - 0.5) * Math.PI


        private fun hour2Rad(aHour: Float) : Double = ((aHour * 2.0) / mClock.hours - 0.5) * Math.PI


        private fun drawName(aCanvas: Canvas) {
            val tMetrix = mPaint.fontMetrics
            aCanvas.drawText(mClock.name, 0F, tMetrix.bottom - tMetrix.top, mPaint)
        }

        private fun drawDial(aCanvas: Canvas, aCenterX: Float, aCenterY: Float) {
            mPaint.textSize = Math.min(aCenterX, aCenterY) * 0.1F
            val tRadius = Math.min(aCenterX, aCenterY) * 0.9F
            for (i in if (mClock.dialFromOne) 1..(mClock.hours) else 0..(mClock.hours-1)) {
                drawNum(aCanvas, i, aCenterX + tRadius * Math.cos(hour2Rad(i.toFloat())).toFloat(), aCenterY + tRadius * Math.sin(hour2Rad(i.toFloat())).toFloat())
            }
        }

        private fun drawNum(aCanvas:Canvas, aNum: Int, x: Float, y: Float) {
            val tWidth = mPaint.measureText(aNum.toString())
            val tMetrix = mPaint.fontMetrics
            aCanvas.drawText(aNum.toString(), x - tWidth/2, y - (tMetrix.ascent + tMetrix.descent) / 2, mPaint)
        }

    }

    class ClockAnimation(val mView: ClockView, val mClock: MyClock) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val tCal = Calendar.getInstance(TimeZone.getDefault())
            tCal.timeInMillis = System.currentTimeMillis()
            val millisecond = ((tCal.get(Calendar.HOUR) * 60 + tCal.get(Calendar.MINUTE)) * 60 + tCal.get(Calendar.SECOND)) * 1000L + tCal.get(Calendar.MILLISECOND)
            val tNow = mClock.calcNow(millisecond)
            mView.mSecond = tNow.second
            mView.mMinute = tNow.minute
            mView.mHour = tNow.hour + mView.mMinute / mClock.minutes.toFloat()

            mView.requestLayout()
        }
    }

    class ClockFragment(val mIndex: Int) : Fragment() {
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val tLayout = inflater!!.inflate(R.layout.fragment_main, container, false) as LinearLayout
            val tClock = ClockList.get(mIndex)
            val tView = ClockView(context, tClock)
            tLayout.addView(tView)
            val tAnimation = ClockAnimation(tView, tClock)
            tAnimation.repeatCount = -1
            tView.startAnimation(tAnimation)
            return tLayout
        }
    }

    class SectionPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = ClockFragment(position)

        override fun getCount(): Int = ClockList.size

        override fun getPageTitle(position: Int): CharSequence = ClockList.get(position).name
    }
}
