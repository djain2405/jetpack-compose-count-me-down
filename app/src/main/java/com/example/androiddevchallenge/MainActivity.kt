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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.MainViewModel.Companion.TimeOperator
import com.example.androiddevchallenge.MainViewModel.Companion.TimeUnit
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val timerViewModel by viewModels<MainViewModel>()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                TimerApp(timerViewModel)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerApp(mainViewModel: MainViewModel, modifier: Modifier = Modifier) {

    val secs = mainViewModel.seconds.observeAsState()
    val mins = mainViewModel.minutes.observeAsState()
    val hours = mainViewModel.hours.observeAsState()
    val resumed = mainViewModel.isRunning.observeAsState()
    val done = mainViewModel.isFinished.observeAsState()

    Surface(color = MaterialTheme.colors.background) {
        val typography = MaterialTheme.typography

        Column(modifier = Modifier.padding()) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jet, Set, Go ....!!!!",
                    fontSize = 24.sp,
                    style = typography.h4,
                    color = MaterialTheme.colors.primary
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TimeUnit.HOUR.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    style = typography.caption
                )
                Text(
                    text = TimeUnit.MIN.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    style = typography.caption
                )
                Text(
                    text = TimeUnit.SEC.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    style = typography.caption
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(4.dp))
                    .padding(32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TimerComponent(
                    value = hours.value,
                    timeUnit = TimeUnit.HOUR,
                    enabled = resumed.value != true
                ) {
                    mainViewModel.modifyTime(MainViewModel.Companion.TimeUnit.HOUR, it)
                }

                Text(text = " : ", fontSize = 36.sp)
                TimerComponent(
                    value = mins.value,
                    timeUnit = TimeUnit.MIN,
                    enabled = resumed.value != true
                ) { mainViewModel.modifyTime(TimeUnit.MIN, it) }
                Text(text = " : ", fontSize = 36.sp)
                TimerComponent(
                    value = secs.value,
                    timeUnit = TimeUnit.SEC,
                    enabled = resumed.value != true
                ) { mainViewModel.modifyTime(TimeUnit.SEC, it) }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = resumed.value != true) {
                    Button(
                        onClick = { mainViewModel.startCountDown() },
                        enabled = !reachedZeroTime(secs, mins, hours)
                    ) {
                        Text(text = "Count Down!")
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = resumed.value == true,
                ) {
                    Button(onClick = { mainViewModel.cancelTimer() }) {
                        Text(text = "Pause")
                    }
                }
            }
        }
    }
}

@Composable
private fun reachedZeroTime(
    secs: State<Int?>,
    mins: State<Int?>,
    hours: State<Int?>
) = (
    (secs.value ?: 0) == 0 && (mins.value ?: 0) == 0 && (
        hours.value
            ?: 0
        ) == 0
    )

@ExperimentalAnimationApi
@Composable
fun TimerComponent(
    value: Int?,
    timeUnit: TimeUnit,
    enabled: Boolean,
    onClick: (TimeOperator) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val typography = MaterialTheme.typography
        Spacer(modifier = Modifier.height(8.dp))
        OperatorButton(
            timeOperator = TimeOperator.INCREASE,
            isEnabled = enabled,
            onClick = onClick
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = String.format("%02d", value ?: 0),
            fontSize = 32.sp,
            color = if ((value ?: 0) <= 5 && timeUnit == TimeUnit.SEC) Color.Red else Color.Blue
        )
        Spacer(modifier = Modifier.height(8.dp))
        OperatorButton(
            timeOperator = TimeOperator.DECREASE,
            isEnabled = enabled,
            onClick = onClick
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun OperatorButton(
    isEnabled: Boolean,
    timeOperator: TimeOperator,
    onClick: (TimeOperator) -> Unit
) {
    AnimatedVisibility(
        visible = isEnabled
    ) {
        Button(
            onClick = { onClick.invoke(timeOperator) },
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.background,
                disabledBackgroundColor = MaterialTheme.colors.background
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {

            when (timeOperator) {
                TimeOperator.INCREASE -> Icon(
                    Icons.Outlined.ArrowCircleUp,
                    null,
                    Modifier.size(24.dp)
                )
                TimeOperator.DECREASE -> Icon(
                    Icons.Outlined.ArrowCircleDown,
                    null,
                    Modifier.size(24.dp)
                )
            }
        }
    }
}
