package org.tannakaken.yourtime

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.*

/**
 * 時計システムの設定をするための[AppCompatActivity]。
 * 左右のスワイプで[ClockList]に登録された時計システム[MyClock]を選択する。
 * [MyClock]のプロパティに対応する値を設定するための[View]を持ち、
 * 左上に[TimeListActivity]へ移動するためのボタン、
 * 右上に[MainActivity]へ移動するためのボタンがあり、
 * 上から下のスワイプで[MainActivity]へ移動する。
 *
 */
class TimeConfActivity : AppCompatActivity() {

    /**
     * スワイプで別の時計システムに映るためのアダプター
     */
    private val mSectionPagerAdapter : SectionPagerAdapter by lazy {
        SectionPagerAdapter(supportFragmentManager)
    }

    /**
     * スワイプで移動可能なページャ
     */
    private val mViewPager : ViewPager by lazy {
        findViewById(R.id.conf_container) as ViewPager
    }

    /**
     * 上から下のスワイプで[MainActivity]に移動するためのリスナー
     */
    private val GESTURE_LISTENER = object : GestureDetector.SimpleOnGestureListener() {
        // スワイプを検出ための最低距離
        private val MIN_DISTANCE = 50
        // スワイプを検出するための最低速度
        private val THRESHOLD_VELOCITY = 200
        // この値以上左右の移動があると、上から下のスワイプだと判断しない、
        private val MAX_OFF_PATH = 200

        /**
         * 上から下のフリックを感知して、ページ遷移させる。
         * @param aEvent1 移動開始の位置等のデータ
         * @param aEvent2 移動終了の位置等のデータ
         * @param aVelocityX x方向の移動速度
         * @param aVelocityY y方向の移動速度
         */
        override fun onFling(aEvent1: MotionEvent?, aEvent2: MotionEvent?, aVelocityX: Float, aVelocityY: Float): Boolean {
            if (aEvent1 == null || aEvent2 == null) {
                return false
            }
            val tDistance_y = aEvent2.y - aEvent1.y // 上から下の移動距離
            val tVelocity_y = Math.abs(aVelocityY) // 移動スピード
            if (Math.abs(aEvent1.x - aEvent2.x) < MAX_OFF_PATH && tDistance_y > MIN_DISTANCE && tVelocity_y > THRESHOLD_VELOCITY) {
                startActivity(Intent(application, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
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
     * イースターエッグ。時間に関する学説
     */
    private fun easterEgg() {
        val maxim = listOf(
                "我々の常識は速度は相対的だと教えているが、相対性理論によると実は光速度は不変だ。すると逆に我々が絶対的だと考えている「同時」という概念が見方で変わる相対的なものであることが分かる。",
                "マグタガートは「過去・現在・未来」で表される時間と「より前・より後」で表される時間は矛盾すると考え、時間は存在しないと論証しようとした。彼はそれにより永遠なる物の存在を示そうとした。"
        )
        Toast.makeText(this, maxim[(Math.random() * maxim.size).toInt()], Toast.LENGTH_LONG).show()
    }

    /**
     * @param aSavedInstanceState [AppCompatActivity]が再表示されたときに、以前の状態を復元するための情報
     */
    override fun onCreate(aSavedInstanceState: Bundle?) {
        super.onCreate(aSavedInstanceState)
        setContentView(R.layout.activity_time_conf)
        mViewPager.adapter = mSectionPagerAdapter
        // 今現在選択中のページをまず表示する。
        mViewPager.setCurrentItem(ClockList.currentClockIndex, false)
        // 左右のスワイプでページ移動
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    ClockList.currentClockIndex = mViewPager.currentItem
                }
            }
        })
        // 右上の文字列でMainActivityに移動
        findViewById(R.id.conf_clock_button).setOnClickListener {
            startActivity(Intent(application, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
        }
        //　イースターエッグ
        findViewById(R.id.main_title).setOnClickListener {
            easterEgg()
        }
        // 左上の文字列でTimeListActivityに移動
        findViewById(R.id.conf_list_button).setOnClickListener {
            startActivity(Intent(application, TimeListActivity::class.java))
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
        }
    }

    // ページ復帰時に現在選択中のページを表示
    override fun onResume() {
        super.onResume()
        mViewPager.setCurrentItem(ClockList.currentClockIndex, false)
    }

    // ページ復帰時にTimeListに削除されたり追加された時計システムを反映するために、変更を通知してページを再構成する。
    override fun onRestart() {
        super.onRestart()
        mSectionPagerAdapter.notifyDataSetChanged()
        finish()
        startActivity(intent)
    }

    /**
     * ページの内容を構成するフラグメント。フラグメントは必ずargumentで値の受け渡しをすること。でないと再構成した時にクラッシュする。
     */
    class ConfFragment : Fragment() {

        /**
         * 設定を保存するためのメソッド。
         */
        var save : () -> Unit = {}
        /**
         * @return フラグメントのレイアウト
         */
        var layout: () -> LinearLayout? = { null }

        /**
         *
         * @param inflater レイアウトを取得するためのオブジェクト
         * @param container [View]の親
         * @param savedInstanceState [Fragment]を再構成したときの情報が入る。
         * @return 構成された[View]
         */
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val tIndex = arguments.getInt("index")
            val tLayout = inflater?.inflate(R.layout.fragment_conf, container, false) as LinearLayout
            tLayout.setOnTouchListener { _, event ->
                activity.onTouchEvent(event)
            }
            layout = {tLayout}
            val tTimeNameEditText = tLayout.findViewById(R.id.time_name_edit_text) as EditText
            val tAMPMSpinner = tLayout.findViewById(R.id.ampm_spinner) as Spinner
            val tHoursSpinner : Spinner = tLayout.findViewById(R.id.hours_spinner) as Spinner
            val tMinuteSpinner = tLayout.findViewById(R.id.minutes_spinner) as Spinner
            val tSecondsSpinner = tLayout.findViewById(R.id.seconds_spinner) as Spinner
            val tDialRadioGroup = tLayout.findViewById(R.id.dial_radio_group) as RadioGroup
            val tWiseRadioGroup = tLayout.findViewById(R.id.wise_radio_group) as RadioGroup
            save = {
                ClockList[tIndex] = MyClock(
                        tTimeNameEditText.text.toString(),
                        MyClock.Ampm.values()[tAMPMSpinner.selectedItemPosition],
                        tHoursSpinner.selectedItemPosition + 1,
                        tMinuteSpinner.selectedItemPosition + 1,
                        tSecondsSpinner.selectedItemPosition + 1,
                        tDialRadioGroup.checkedRadioButtonId == R.id.dial_from_one_radio,
                        tWiseRadioGroup.checkedRadioButtonId == R.id.clockwise_radio
                )

            }
            return tLayout
        }

        /**
         * [TimeConfActivity]が[TimeConfActivity.onResume]したときのタイミングで呼ばれる。
         * 内容を設定に合わせて構成する
         */
        override fun onResume() {
            super.onResume()
            val tIndex = arguments.getInt("index")
            val tLayout = layout()
            tLayout ?: return
            val tTimeNameEditText = tLayout.findViewById(R.id.time_name_edit_text) as EditText
            val tAMPMSpinner = tLayout.findViewById(R.id.ampm_spinner) as Spinner
            val tHoursSpinner : Spinner = tLayout.findViewById(R.id.hours_spinner) as Spinner
            val tMinuteSpinner = tLayout.findViewById(R.id.minutes_spinner) as Spinner
            val tSecondsSpinner = tLayout.findViewById(R.id.seconds_spinner) as Spinner
            val tDialRadioGroup = tLayout.findViewById(R.id.dial_radio_group) as RadioGroup
            val tWiseRadioGroup = tLayout.findViewById(R.id.wise_radio_group) as RadioGroup
            val tClock = ClockList[tIndex]
            tTimeNameEditText.setText(tClock.name)
            tAMPMSpinner.setSelection(tClock.ampm.division - 1)
            tHoursSpinner.setSelection(tClock.hours - 1)
            tMinuteSpinner.setSelection(tClock.minutes - 1)
            tSecondsSpinner.setSelection(tClock.seconds - 1)
            if (tClock.dialFromOne) tDialRadioGroup.check(R.id.dial_from_one_radio) else tDialRadioGroup.check(R.id.dial_from_zero_radio)
            if (tClock.clockwise) tWiseRadioGroup.check(R.id.clockwise_radio) else tWiseRadioGroup.check(R.id.counterclockwise_radio)
        }

        /**
         * 内容を保存する。
         */
        override fun onPause() {
            super.onPause()
            save()
            ClockList.save(this.context)
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
            val tFragment = ConfFragment()
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


