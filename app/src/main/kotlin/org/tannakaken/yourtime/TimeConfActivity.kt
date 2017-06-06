package org.tannakaken.yourtime

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class TimeConfActivity : AppCompatActivity() {

    val mTimeNameEditText : EditText by lazy {
        findViewById(R.id.time_name_edit_text) as EditText
    }
    val mAMPMSpinner : Spinner by lazy {
        findViewById(R.id.ampm_spinner) as Spinner
    }
    val mHoursSpinner : Spinner by lazy {
        findViewById(R.id.hours_spinner) as Spinner
    }
    val mMinuteSpinner : Spinner by lazy {
        findViewById(R.id.minutes_spinner) as Spinner
    }
    val mSecondsSpinner : Spinner by lazy {
        findViewById(R.id.seconds_spinner) as Spinner
    }
    val mDialRadioGroup : RadioGroup by lazy {
        findViewById(R.id.dial_radio_group) as RadioGroup
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_conf)
    }

    override fun onResume() {
        super.onResume()
        val i = intent.getIntExtra("index", 0)
        mTimeNameEditText.setText(ClockList.get(i).name)
        mAMPMSpinner.setSelection(ClockList.get(i).ampm.rowValue - 1)
        mHoursSpinner.setSelection(ClockList.get(i).hours - 1)
        mMinuteSpinner.setSelection(ClockList.get(i).minutes - 1)
        mSecondsSpinner.setSelection(ClockList.get(i).seconds - 1)
        if (ClockList.get(i).dialFromOne) mDialRadioGroup.check(R.id.dial_from_one_radio) else mDialRadioGroup.check(R.id.dial_from_zero_radio)
        findViewById(R.id.conf_clock_button).setOnClickListener {
            save(i)
            ClockList.currentClockIndex = i
            startActivity(Intent(application, MainActivity::class.java))
        }
        findViewById(R.id.conf_list_button).setOnClickListener {
            save(i)
            startActivity(Intent(application, TimeListActivity::class.java))
        }
    }

    private fun save(i : Int) {
        ClockList.set(i, MyClock(
                mTimeNameEditText.text.toString(),
                MyClock.Ampm.values()[mAMPMSpinner.selectedItemPosition],
                mHoursSpinner.selectedItemPosition + 1,
                mMinuteSpinner.selectedItemPosition + 1,
                mSecondsSpinner.selectedItemPosition + 1,
                mDialRadioGroup.checkedRadioButtonId == R.id.dial_from_one_radio
            )
        )
    }
}

