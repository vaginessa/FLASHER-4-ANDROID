package com.victorlapin.flasher.model

import com.andrognito.patternlockview.PatternLockView

object PatternUtils {
    private val mPattern3 = arrayOf(
            arrayOf('1', '2', '3'),
            arrayOf('4', '5', '6'),
            arrayOf('7', '8', '9')
    )

    private val mPattern4 = arrayOf(
            arrayOf('1', '2', '3', '4'),
            arrayOf('5', '6', '7', '8'),
            arrayOf('9', ':', ';', '<'),
            arrayOf('=', '>', '?', '@')
    )

    private val mPattern5 = arrayOf(
            arrayOf('1', '2', '3', '4', '5'),
            arrayOf('6', '7', '8', '9', ':'),
            arrayOf(';', '<', '=', '>', '?'),
            arrayOf('@', 'A', 'B', 'C', 'D'),
            arrayOf('E', 'F', 'G', 'H', 'I')
    )

    fun stringToPattern(password: String, dimension: Int): MutableList<PatternLockView.Dot> {
        val result = ArrayList<PatternLockView.Dot>()
        val currentMatrix = when (dimension) {
            3 -> mPattern3
            4 -> mPattern4
            else -> mPattern5
        }
        password.forEach {
            (0 until currentMatrix.size).forEach outer@ { i ->
                (0 until currentMatrix.size).forEach inner@{ j ->
                    if (currentMatrix[i][j] == it) {
                        result.add(PatternLockView.Dot.of(i, j))
                        return@outer
                    }
                }
            }
        }
        return result
    }

    fun patternToString(pattern: MutableList<PatternLockView.Dot>?, dimension: Int): String {
        val sb = StringBuilder()
        val currentMatrix = when (dimension) {
            3 -> mPattern3
            4 -> mPattern4
            else -> mPattern5
        }
        pattern?.forEach {
            sb.append(currentMatrix[it.row][it.column])
        }
        return sb.toString()
    }
}