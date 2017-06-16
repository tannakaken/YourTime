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

class TimeConfActivity : AppCompatActivity() {

    private val mSectionPagerAdapter : SectionPagerAdapter by lazy {
        SectionPagerAdapter(supportFragmentManager)
    }

    private val mViewPager : ViewPager by lazy {
        findViewById(R.id.conf_container) as ViewPager
    }

    private val GESTURE_LISTENER = object : GestureDetector.SimpleOnGestureListener() {
        private val MIN_DISTANCE = 50
        private val THRESHOLD_VELOCITY = 200
        private val MAX_OFF_PATH = 200

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            val distance_y = e2!!.y - e1!!.y // 上から下
            val velocity_y = Math.abs(velocityY)
            if (Math.abs(e1.x - e2.x) < MAX_OFF_PATH && distance_y > MIN_DISTANCE && velocity_y > THRESHOLD_VELOCITY) {
                startActivity(Intent(application, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
            }
            return false
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    }

    private val GESTURE_DETECTER by lazy {
        GestureDetector(this, GESTURE_LISTENER)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return GESTURE_DETECTER.onTouchEvent(event)
    }

    private fun easterEgg() {
        val maxim = listOf(
                "我々の常識は速度は相対的だと教えているが、相対性理論によると実は光速度は不変だ。すると逆に我々が絶対的だと考えている「同時」という概念が見方で変わる相対的なものであることが分かる。",
                "マグタガートは「過去・現在・未来」で表される時間と「より前・より後」で表される時間は矛盾すると考え、時間は存在しないと論証しようとした。彼はそれにより永遠なる物の存在を示そうとした。"
        )
        Toast.makeText(this, maxim[(Math.random() * maxim.size).toInt()], Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_conf)
        mViewPager.adapter = mSectionPagerAdapter
        mViewPager.setCurrentItem(ClockList.currentClockIndex, false)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    ClockList.currentClockIndex = mViewPager.currentItem
                }
            }
        })
        findViewById(R.id.conf_clock_button).setOnClickListener {
            startActivity(Intent(application, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
        }
        findViewById(R.id.main_title).setOnClickListener {
            easterEgg()
        }
        findViewById(R.id.conf_list_button).setOnClickListener {
            startActivity(Intent(application, TimeListActivity::class.java))
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
        }
    }

    override fun onResume() {
        super.onResume()
        mViewPager.setCurrentItem(ClockList.currentClockIndex, false)
    }

    override fun onRestart() {
        super.onRestart()
        mSectionPagerAdapter.notifyDataSetChanged()
        finish()
        startActivity(intent)
    }

    class ConfFragment : Fragment() {

        var save : () -> Unit = {}
        var layout: () -> LinearLayout? = { null }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val tIndex = arguments.getInt("index")
            val tLayout = inflater!!.inflate(R.layout.fragment_conf, container, false) as LinearLayout
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
            save = {
                ClockList.set(tIndex, MyClock(
                        tTimeNameEditText.text.toString(),
                        MyClock.Ampm.values()[tAMPMSpinner.selectedItemPosition],
                        tHoursSpinner.selectedItemPosition + 1,
                        tMinuteSpinner.selectedItemPosition + 1,
                        tSecondsSpinner.selectedItemPosition + 1,
                        tDialRadioGroup.checkedRadioButtonId == R.id.dial_from_one_radio
                ))

            }
            return tLayout
        }

        override fun onResume() {
            super.onResume()
            val tIndex = arguments.getInt("index")
            val tLayout = layout()
            if (tLayout == null) {
                return
            }
            val tTimeNameEditText = tLayout.findViewById(R.id.time_name_edit_text) as EditText
            val tAMPMSpinner = tLayout.findViewById(R.id.ampm_spinner) as Spinner
            val tHoursSpinner : Spinner = tLayout.findViewById(R.id.hours_spinner) as Spinner
            val tMinuteSpinner = tLayout.findViewById(R.id.minutes_spinner) as Spinner
            val tSecondsSpinner = tLayout.findViewById(R.id.seconds_spinner) as Spinner
            val tDialRadioGroup = tLayout.findViewById(R.id.dial_radio_group) as RadioGroup
            val tClock = ClockList[tIndex]
            tTimeNameEditText.setText(tClock.name)
            tAMPMSpinner.setSelection(tClock.ampm.rowValue - 1)
            tHoursSpinner.setSelection(tClock.hours - 1)
            tMinuteSpinner.setSelection(tClock.minutes - 1)
            tSecondsSpinner.setSelection(tClock.seconds - 1)
            if (tClock.dialFromOne) tDialRadioGroup.check(R.id.dial_from_one_radio) else tDialRadioGroup.check(R.id.dial_from_zero_radio)
        }

        override fun onPause() {
            super.onPause()
            save()
            ClockList.save(this.context)
        }
    }

    class SectionPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val tFragment = ConfFragment()
            val tBundle = Bundle()
            tBundle.putInt("index", position)
            tFragment.arguments = tBundle
            return tFragment
        }

        override fun getItemPosition(`object`: Any?): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int = ClockList.size

        override fun getPageTitle(position: Int): CharSequence = ClockList.get(position).name
    }
}


