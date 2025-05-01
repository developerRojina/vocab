package com.vocable.util

import android.app.TimePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocableTimePicker(modifier: Modifier, onTimePicked: (Long) -> Unit, onCancelled: () -> Unit) {
    val calendar = Calendar.getInstance()
    val context = LocalContext.current
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val date = calendar.time.time // java.util.Date
            onTimePicked.invoke(date)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )
    timePickerDialog.setOnCancelListener {
        onCancelled.invoke()
    }
    timePickerDialog.show()

}