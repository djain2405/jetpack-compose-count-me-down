/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var countDownTimer: CountDownTimer? = null

    val isRunning: LiveData<Boolean>
        get() = _isRunning
    private val _isFinished = MutableLiveData(false)
    val isFinished: LiveData<Boolean>
        get() = _isFinished

    private val _seconds = MutableLiveData(10)
    val seconds: LiveData<Int>
        get() = _seconds
    private val _minutes = MutableLiveData(0)
    val minutes: LiveData<Int>
        get() = _minutes
    private val _hours = MutableLiveData(0)
    val hours: LiveData<Int>
        get() = _hours
    private val _isRunning = MutableLiveData(false)

    fun startCountDown() {

        // cancel existing Timer
        if (countDownTimer != null) {
            cancelTimer()
        }

        countDownTimer = object : CountDownTimer((getSeconds() * 1000).toLong(), 1000) {
            override fun onTick(millisecs: Long) {

                // Seconds
                val secs = (millisecs / MSECS_IN_SEC % SECS_IN_MINS).toInt()
                if (secs != seconds.value) {
                    _seconds.postValue(secs)
                }

                // Mins
                val mins = (millisecs / MSECS_IN_SEC / SECS_IN_MINS % SECS_IN_MINS).toInt()
                if (mins != minutes.value) {
                    _minutes.postValue(mins)
                }

                // Hours
                val hrs = (millisecs / MSECS_IN_SEC / MINS_IN_HOUR / SECS_IN_MINS).toInt()
                if (hrs != hours.value) {
                    _hours.postValue(hrs)
                }
            }

            override fun onFinish() {
                viewModelScope.launch {
                    _isFinished.postValue(true)
                    delay(2000)
                    _isRunning.postValue(false)
                    _isFinished.postValue(false)
                }
            }
        }
        countDownTimer?.start()
        _isRunning.postValue(true)
    }

    fun modifyTime(timeUnit: TimeUnit, timeOperator: TimeOperator) {
        when (timeUnit) {
            TimeUnit.SEC -> _seconds.postValue(
                operation(seconds.value ?: 0, timeOperator).coerceIn(0, 59)
            )
            TimeUnit.MIN -> _minutes.postValue(
                operation(minutes.value ?: 0, timeOperator).coerceIn(0, 59)
            )
            TimeUnit.HOUR -> _hours.postValue(
                operation(hours.value ?: 0, timeOperator).coerceIn(0, 99)
            )
        }
    }

    fun cancelTimer() {
        countDownTimer?.cancel()
        _isRunning.postValue(false)
    }

    private fun getSeconds() = ((hours.value ?: 0) * MINS_IN_HOUR * SECS_IN_MINS) + ((minutes.value ?: 0) * SECS_IN_MINS) + (seconds.value ?: 0)

    private fun operation(currentValue: Int, timeOperator: TimeOperator): Int {
        return when (timeOperator) {
            TimeOperator.INCREASE -> currentValue + 1
            TimeOperator.DECREASE -> currentValue - 1
        }
    }

    companion object {
        enum class TimeOperator {
            INCREASE, DECREASE
        }

        enum class TimeUnit {
            SEC, MIN, HOUR
        }

        const val MINS_IN_HOUR = 60
        const val SECS_IN_MINS = 60
        const val MSECS_IN_SEC = 1000
    }
}
