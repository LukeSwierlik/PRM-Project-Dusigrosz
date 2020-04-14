package com.example.prm_project_dusigrosz.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prm_project_dusigrosz.R
import com.example.prm_project_dusigrosz.enums.StatusSimulation
import com.example.prm_project_dusigrosz.objects.Fields
import kotlinx.android.synthetic.main.activity_simulation.*

class SimulationActivity : AppCompatActivity() {
    private val mMainHandler = Handler(Looper.getMainLooper())

    private var mOriginalDebt: Double = 0.0
    private var mSpeedDebt: Double = 0.0
    private var mPercentOfCommission: Double = 0.0
    private var mValueOfCommission: Double = 0.0

    private var mCurrentDebt: Double = 0.0
    private var mCurrentInterestPayable: Double = 0.0
    private var mStatusSimulation: StatusSimulation =
        StatusSimulation.START

    private var mTask: Runnable = object: Runnable {
        override fun run() {

            // TODO: When mSpeedDebt and mPercentCommission are zero, there is a problem with the button icon being changed.
            if (mCurrentDebt <= 0.0 || mCurrentDebt == mOriginalDebt) {
                mStatusSimulation =
                    StatusSimulation.RESET
                stopTask()
            } else {
                updateValues()
                mMainHandler.postDelayed(this,
                    sDELAY_MILLIS_FOR_SIMULATION
                )
            }
        }
    }

    companion object {
        private const val sDELAY_MILLIS_FOR_SIMULATION: Long = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulation)

        if (intent.hasExtra(Fields.FIRST_NAME)) {
            FIRST_NAME_TEXT_VIEW_SIM.text = intent.getStringExtra(Fields.FIRST_NAME)
        }

        if (intent.hasExtra(Fields.LAST_NAME)) {
            LAST_NAME_TEXT_VIEW_SIM.text = intent.getStringExtra(Fields.LAST_NAME)
        }

        if (intent.hasExtra(Fields.PHONE_NUMBER)) {
            PHONE_NUMBER_TEXT_VIEW_SIM.text = intent.getStringExtra(Fields.PHONE_NUMBER)
        }

        if (intent.hasExtra(Fields.DEBT)) {
            ORIGINAL_DEBT_VALUE_SIM.text = intent.getStringExtra(Fields.DEBT)
        }
    }

    fun onClickStartSimulation(view: View) {
        if (SPEED_DEBT_INPUT_VALUE_SIM.text.toString().isEmpty() ||
            PERCENT_INPUT_VALUE_SIM.text.toString().isEmpty()) {
            Toast
                .makeText(applicationContext, "Please complete the fields!", Toast.LENGTH_SHORT)
                .show()
        } else {
            mSpeedDebt = SPEED_DEBT_INPUT_VALUE_SIM.text.toString().toDouble()
            mPercentOfCommission = PERCENT_INPUT_VALUE_SIM.text.toString().toDouble()

            if (mSpeedDebt < 0.0 || mPercentOfCommission < 0.0) {
                Toast
                    .makeText(applicationContext, "Values must be positive.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                when (mStatusSimulation) {
                    StatusSimulation.START -> {
                        mOriginalDebt = ORIGINAL_DEBT_VALUE_SIM.text.toString().toDouble()
                        mCurrentDebt = mOriginalDebt - mSpeedDebt
                        mValueOfCommission = (mPercentOfCommission * mOriginalDebt) / 100

                        mStatusSimulation =
                            StatusSimulation.IN_PROGRESS
                        startTask()
                    }
                    StatusSimulation.IN_PROGRESS -> {
                        mStatusSimulation =
                            StatusSimulation.PAUSE
                        stopTask()
                    }
                    StatusSimulation.PAUSE -> {
                        mStatusSimulation =
                            StatusSimulation.IN_PROGRESS
                        startTask()
                    }
                    StatusSimulation.DONE -> {
                        mStatusSimulation =
                            StatusSimulation.RESET
                        resetTask()
                    }
                    StatusSimulation.RESET -> {
                        mStatusSimulation =
                            StatusSimulation.IN_PROGRESS
                        resetTask()
                    }
                }
            }
        }
    }

    private fun updateValues() {
        if (mCurrentInterestPayable != 0.0) {
            mCurrentInterestPayable = INTEREST_VALUE_SIM.text.toString().toDouble()
        }

        mCurrentInterestPayable += mValueOfCommission

        INTEREST_VALUE_SIM.text = (mCurrentInterestPayable).toString()
        DEBT_INTEREST_VALUE_SIM.text = (mOriginalDebt + mCurrentInterestPayable).toString()

        mCurrentDebt -= mSpeedDebt
    }

    private fun startTask() {
        mTask.run()
        SIMULATE_BTN.setImageResource(android.R.drawable.ic_media_pause)
    }

    private fun stopTask() {
        mMainHandler.removeCallbacks(mTask)
        SIMULATE_BTN.setImageResource(android.R.drawable.ic_media_play)
    }

    private fun resetTask() {
        INTEREST_VALUE_SIM.text = (0).toString()
        DEBT_INTEREST_VALUE_SIM.text = (0).toString()

        mOriginalDebt = ORIGINAL_DEBT_VALUE_SIM.text.toString().toDouble()
        mSpeedDebt = SPEED_DEBT_INPUT_VALUE_SIM.text.toString().toDouble()
        mPercentOfCommission = PERCENT_INPUT_VALUE_SIM.text.toString().toDouble()

        mCurrentDebt = mOriginalDebt - mSpeedDebt
        mValueOfCommission = (mPercentOfCommission * mOriginalDebt) / 100

        startTask()
    }
}
