package com.vocable.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class CardOffsetState(meaningCount: Int) {

    val xOffsets = mutableStateListOf<Dp>().apply {
        repeat(meaningCount) { index -> add(24.dp * index) }
    }

    val yOffsets = mutableStateListOf<Dp>().apply {
        repeat(meaningCount) { index -> add(12.dp * index) }
    }

    val dragOffsets = mutableStateListOf<Float>().apply {
        repeat(meaningCount) { add(0f) }
    }

    fun updateOffsetsForSelection(topCardIndex: Int, meaningCount: Int) {
        val blockSize = 5

        // Calculate block information
        val remainder = (topCardIndex % blockSize)
        if (remainder == 0 && topCardIndex > 0) {
            val division = (topCardIndex / blockSize) - 1
            val blockIndex = division * 5

            for (k in blockIndex until topCardIndex + 1) {
                yOffsets[k] = 0.dp
                xOffsets[k] = 16.dp
                dragOffsets[k] = -90f + ((division + 1) * 5)
            }
        }

        if (topCardIndex < meaningCount - 1) {
            val division = (topCardIndex / blockSize)
            dragOffsets[topCardIndex] = -90f + (minOf(topCardIndex, remainder + division) * blockSize)
            xOffsets[topCardIndex + 1] = 16.dp
            yOffsets[topCardIndex + 1] = 12.dp

            for (j in (topCardIndex + 2) until meaningCount) {
                xOffsets[j] = 24.dp * (j - (topCardIndex))
                yOffsets[j] = 12.dp * (j - (topCardIndex))
            }
        }
    }


    fun resetOffsets(dataIndex: Int, meaningCount: Int) {
        for (j in dataIndex until meaningCount) {
            dragOffsets[j] = 0f
            xOffsets[j] = 24.dp * (j - dataIndex)
            yOffsets[j] = 12.dp * (j - dataIndex)
        }
    }
}