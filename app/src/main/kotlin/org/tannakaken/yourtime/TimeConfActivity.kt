package org.tannakaken.yourtime

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
        findViewById(R.id.cancel_button).setOnClickListener {
            finish()
        }
        findViewById(R.id.save_button).setOnClickListener {
            ClockList.currentClock = MyClock(
                    mTimeNameEditText.text.toString(),
                    Ampm.values()[mAMPMSpinner.selectedItemPosition],
                    mHoursSpinner.selectedItemPosition + 1,
                    mMinuteSpinner.selectedItemPosition + 1,
                    mSecondsSpinner.selectedItemPosition + 1,
                    mDialRadioGroup.checkedRadioButtonId == R.id.dial_from_one_radio
            )
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        mTimeNameEditText.setText(ClockList.currentClock.name)
        mAMPMSpinner.setSelection(ClockList.currentClock.ampm.rowValue - 1)
        mHoursSpinner.setSelection(ClockList.currentClock.hours - 1)
        mMinuteSpinner.setSelection(ClockList.currentClock.minutes - 1)
        mSecondsSpinner.setSelection(ClockList.currentClock.seconds - 1)
        if (ClockList.currentClock.dialFromOne) mDialRadioGroup.check(R.id.dial_from_one_radio) else mDialRadioGroup.check(R.id.dial_from_zero_radio)
    }
}

