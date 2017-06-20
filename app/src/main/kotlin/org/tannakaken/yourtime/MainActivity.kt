package org.tannakaken.yourtime

import android.app.Dialog
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
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.Toast
import java.util.*

/**
 * 主画面。
 * １２時間制以外の時計システムでアナログ時計を表示する。左上に時間制に付けた名前が表示される。
 * 左右のスワイプで他の時計システムに移動する。
 * 下から上のスワイプで設定画面[TimeConfActivity]に移動する。
 * ツールバーの右側の文字列からも設定画面に移動。
 * ツールバーの左側の文字列からは一覧画面[TimeListActivity]に移動する。
 * ツールバーの中央の文字列はイースターエッグで時間に関することわざをトーストする。
 */
class MainActivity : AppCompatActivity() {

    /**
     * 左右のスワイプによるページ移動を司るオブジェクト。
     */
    private val mSectionPagerAdapter : SectionPagerAdapter by lazy {
        SectionPagerAdapter(supportFragmentManager)
    }

    /**
     * 複数のページを持ちスワイプで移動できるView
     */
    private val mViewPager : ViewPager by lazy {
        findViewById(R.id.container) as ViewPager
    }

    /**
     * 上下スワイプによるページ移動を司るオブジェクト。
     */
    private val GESTURE_LISTENER = object : GestureDetector.SimpleOnGestureListener() {
        // スワイプを検出ための最低距離
        private val MIN_DISTANCE = 50
        // スワイプを検出するための最低速度
        private val THRESHOLD_VELOCITY = 200
        // この値以上左右の移動があると、上から下のスワイプだと判断しない、
        private val MAX_OFF_PATH = 200

        /**
         * 下から上のフリックを感知して、ページ遷移させる。
         * @param aEvent1 移動開始の位置等のデータ
         * @param aEvent2 移動終了の位置等のデータ
         * @param aVelocityX x方向の移動速度
         * @param aVelocityY y方向の移動速度
         */
        override fun onFling(aEvent1: MotionEvent?, aEvent2: MotionEvent?, aVelocityX: Float, aVelocityY: Float): Boolean {
            val distance_y = aEvent1!!.y - aEvent2!!.y // 下から上
            val velocity_y = Math.abs(aVelocityY)
            if (Math.abs(aEvent1.x - aEvent2.x) < MAX_OFF_PATH && distance_y > MIN_DISTANCE && velocity_y > THRESHOLD_VELOCITY) {
                startActivity(Intent(application, TimeConfActivity::class.java))
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
            }
            return false
        }

        /**
         * イベントは一つずつ起き、前のイベントがtrueを返したとき、次のイベントが発火するので、これがtrueを返す必要がある。
         * @param aEvent 使わない
         * @return [onFling]を発火させるためにtrueを返す。
         */
        override fun onDown(aEvent: MotionEvent?): Boolean {
            return true
        }
    }
    /**
     * ジェスチャーを検知するオブジェクト。
     */
    private val GESTURE_DETECTER by lazy {
        GestureDetector(this, GESTURE_LISTENER)
    }
    /**
     * タッチイベントを[GESTURE_DETECTER]に送る。
     * @param aEvent タッチイベント
     * @return Viewの下にいるViewにイベントを渡すかどうか
     */
    override fun onTouchEvent(aEvent: MotionEvent?): Boolean {
        return GESTURE_DETECTER.onTouchEvent(aEvent)
    }
    /**
     * イースターエッグ。時間に関することわざ
     */
    private fun easterEgg() {
        val maxim = listOf("時は金なり", "光陰矢の如し","歳歳年年人同じからず","少年老い易く学成り難し","明日は明日の風が吹く","今日の後に今日なし","思い立ったが吉日")
        Toast.makeText(this, maxim[(Math.random() * maxim.size).toInt()], Toast.LENGTH_SHORT).show()
    }
    /**
     * @param aSavedInstanceState [AppCompatActivity]が再表示されたときに、以前の状態を復元するための情報
     */
    override fun onCreate(aSavedInstanceState: Bundle?) {
        super.onCreate(aSavedInstanceState)
        setContentView(R.layout.activiy_main)
        // 右上にある文字列で時計システムの設定画面にうつる
        findViewById(R.id.main_conf_button).setOnClickListener {
            startActivity(Intent(application, TimeConfActivity::class.java))
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }
        // イースターエッグ
        findViewById(R.id.main_title).setOnClickListener {
            easterEgg()
        }
        // 左上にある文字列で時計システムの一覧画面にうつる
        findViewById(R.id.main_list_button).setOnClickListener {
            startActivity(Intent(application, TimeListActivity::class.java))
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
        }
        mViewPager.adapter = mSectionPagerAdapter
        // 左右のフリックで時計システムを選択する。
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    ClockList.currentClockIndex = mViewPager.currentItem
                }
            }
        })

        // 右下のフローティングアクションボタンで、アプリの詳細情報（作者、Webページ、連絡先、等）を表示
        findViewById(R.id.fab).setOnClickListener {
            val dialog = Dialog(this)
            dialog.setCancelable(true)
            dialog.setTitle("about this app")
            dialog.setContentView(R.layout.about)
            val tWebView = dialog.findViewById(R.id.about) as WebView
            tWebView.loadUrl("file:///android_asset/about.html")
            dialog.show()
        }
    }

    /**
     * 画面を再表示するときは、現在選択中の時計システムを表示
     */
    override fun onResume() {
        super.onResume()
        mViewPager.setCurrentItem(ClockList.currentClockIndex, false)
    }

    /**
     * ページ数を変更した時は、変更を通知してすべて再構成。
     */
    override fun onRestart() {
        super.onRestart()
        mSectionPagerAdapter.notifyDataSetChanged()
        finish()
        startActivity(intent)
    }

    /**
     * 時計のアニメーションを表示する[View]
     * @property mContext [Context]
     * @property mClock この[MyClock]が表現している時計を表示する
     */
    class ClockView(val mContext: Context, val mClock: MyClock) : View(mContext) {
        private val mPaint = Paint()
        var mSecond: Int = 0
        var mMinute: Int = 0
        var mHour: Float = 0F

        constructor(mContext: Context) : this(mContext, ClockList[0])

        /**
         * タッチイベントは[MainActivity]に伝える。
         */
        override fun onTouchEvent(event: MotionEvent?): Boolean {
            return (mContext as MainActivity).onTouchEvent(event)
        }

        /**
         * 現在時刻を表示。
         * @param aCanvas
         */
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

        /**
         * @param aSecond 現在の秒
         * @return 秒を角度に変換したもの
         */
        private fun sec2Rad(aSecond : Int) : Double = (mClock.sig * (aSecond * 2.0) / mClock.seconds - 0.5) * Math.PI
        /**
         * @param aMinute 現在の分
         * @return 分を角度に変換したもの
         */
        private fun min2Rad(aMinute : Int) : Double = (mClock.sig * (aMinute * 2.0) / mClock.minutes - 0.5) * Math.PI
        /**
         * @param aHour 現在の時刻
         * @return 時刻を角度に変換したもの
         */
        private fun hour2Rad(aHour: Float) : Double = (mClock.sig * (aHour * 2.0) / mClock.hours - 0.5) * Math.PI

        /**
         * 時計システムの名前を表示
         * @param aCanvas
         */
        private fun drawName(aCanvas: Canvas) {
            val tMetrix = mPaint.fontMetrics
            aCanvas.drawText(mClock.name, 0F, tMetrix.bottom - tMetrix.top, mPaint)
        }

        /**
         * 文字盤を表示する。
         * @param aCanvas
         * @param aCenterX x方向の中心
         * @param aCenterY y方向の中心
         */
        private fun drawDial(aCanvas: Canvas, aCenterX: Float, aCenterY: Float) {
            mPaint.textSize = Math.min(aCenterX, aCenterY) * 0.1F
            val tRadius = Math.min(aCenterX, aCenterY) * 0.9F
            for (i in if (mClock.dialFromOne) 1..(mClock.hours) else 0..(mClock.hours-1)) {
                drawNum(aCanvas, i, aCenterX + tRadius * Math.cos(hour2Rad(i.toFloat())).toFloat(), aCenterY + tRadius * Math.sin(hour2Rad(i.toFloat())).toFloat())
            }
        }

        /**
         * @param aCanvas
         * @param aNum 文字盤の数字
         * @param x 数字を表示するx座標
         * @param y 数字を表示するy座標
         */
        private fun drawNum(aCanvas:Canvas, aNum: Int, x: Float, y: Float) {
            val tWidth = mPaint.measureText(aNum.toString())
            val tMetrix = mPaint.fontMetrics
            aCanvas.drawText(aNum.toString(), x - tWidth/2, y - (tMetrix.ascent + tMetrix.descent) / 2, mPaint)
        }

    }

    /**
     * アニメーションを表現するクラス。
     * @property mView アニメーションが表示される[ClockView]
     * @property mClock 時計システム
     */
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

    /**
     * ページの内容を構成するフラグメント。フラグメントは必ずargumentで値の受け渡しをすること。でないと再構成した時にクラッシュする。
     */
    class ClockFragment : Fragment() {
        /**
         *
         * @param inflater レイアウトを取得するためのオブジェクト
         * @param container [View]の親
         * @param savedInstanceState [Fragment]を再構成したときの情報が入る。
         * @return 構成された[View]
         */
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val tIndex = arguments.getInt("index")
            val tLayout = inflater!!.inflate(R.layout.fragment_main, container, false) as LinearLayout
            val tClock = ClockList[tIndex]
            val tView = ClockView(context, tClock)
            tLayout.addView(tView)
            val tAnimation = ClockAnimation(tView, tClock)
            tAnimation.repeatCount = -1
            tView.startAnimation(tAnimation)
            return tLayout
        }
    }
    /**
     * ページをコントロールする。
     * @param fm フラグメントを管理するオブジェクト
     */
    class SectionPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        /**
         * @param aPosition ページ数
         * @return そのページの[Fragment]
         */
        override fun getItem(aPosition: Int): Fragment {
            val tFragment = ClockFragment()
            val tBundle = Bundle()
            tBundle.putInt("index", aPosition)
            tFragment.arguments = tBundle
            return tFragment
        }
        /**
         * ページ数変更があったときにページを再構成するために。本当は意味のある値を返すと、より効率が良くなるらしい。
         */
        override fun getItemPosition(`object`: Any?): Int {
            return PagerAdapter.POSITION_NONE
        }
        /**
         * @return ページ数
         */
        override fun getCount(): Int = ClockList.size
        /**
         * @param aPosition ページ番号
         * @return タイトル
         */
        override fun getPageTitle(aPosition: Int): CharSequence = ClockList[aPosition].name
    }
}

