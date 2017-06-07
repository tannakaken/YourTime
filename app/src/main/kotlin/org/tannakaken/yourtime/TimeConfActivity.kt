package org.tannakaken.yourtime

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class TimeConfActivity : AppCompatActivity() {

    private val mSectionPagerAdapter : SectionPagerAdapter by lazy {
        SectionPagerAdapter(supportFragmentManager)
    }

    private val mViewPager : ViewPager by lazy {
        findViewById(R.id.conf_container) as ViewPager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_conf)
        mViewPager.adapter = mSectionPagerAdapter
        mViewPager.setCurrentItem(ClockList.currentClockIndex, false)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                ClockList.currentClockIndex = position
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}
        })
        findViewById(R.id.conf_clock_button).setOnClickListener {
            startActivity(Intent(application, MainActivity::class.java))
        }
        findViewById(R.id.conf_list_button).setOnClickListener {
            startActivity(Intent(application, TimeListActivity::class.java))
        }
    }

    override fun onRestart() {
        super.onRestart()
        mSectionPagerAdapter.notifyDataSetChanged()
        recreate()
    }

    class ConfFragment : Fragment() {

        var save : () -> Unit = {}

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val tIndex = arguments.getInt("index")
            val tLayout = inflater!!.inflate(R.layout.fragment_conf, container, false) as LinearLayout
            val tTimeNameEditText = tLayout.findViewById(R.id.time_name_edit_text) as EditText
            val tAMPMSpinner = tLayout.findViewById(R.id.ampm_spinner) as Spinner
            val tHoursSpinner : Spinner = tLayout.findViewById(R.id.hours_spinner) as Spinner
            val tMinuteSpinner = tLayout.findViewById(R.id.minutes_spinner) as Spinner
            val tSecondsSpinner = tLayout.findViewById(R.id.seconds_spinner) as Spinner
            val tDialRadioGroup = tLayout.findViewById(R.id.dial_radio_group) as RadioGroup
            val tClock = ClockList.get(tIndex)
            tTimeNameEditText.setText(tClock.name)
            tAMPMSpinner.setSelection(tClock.ampm.rowValue - 1)
            tHoursSpinner.setSelection(tClock.hours - 1)
            tMinuteSpinner.setSelection(tClock.minutes - 1)
            tSecondsSpinner.setSelection(tClock.seconds - 1)
            if (tClock.dialFromOne) tDialRadioGroup.check(R.id.dial_from_one_radio) else tDialRadioGroup.check(R.id.dial_from_zero_radio)
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

        override fun onPause() {
            super.onPause()
            save()
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
        override fun getCount(): Int = ClockList.size

        override fun getPageTitle(position: Int): CharSequence = ClockList.get(position).name
    }
}


