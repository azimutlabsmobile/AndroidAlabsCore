package com.alabs.core_application.utils.delegates

import android.widget.EditText
import com.alabs.core_application.data.constants.CoreConstant
import java.text.DecimalFormat

interface AmountFormatter {

    fun format(s: String, editText: EditText, formatter: DecimalFormat): String
}

/**
 * Форматирование в формате 0.00
 */
class AmountFormatSpaceZeroDelegate :
    AmountFormatter {

    override fun format(s: String, editText: EditText, formatter: DecimalFormat): String {
        val srt = s.replace(" ", CoreConstant.EMPTY)
        val value = formatter.parse(srt)

        var startCursor = editText.selectionStart
        val afterCursorSymbol = formatter.format(value).length

        val length = formatter.format(value).length

        val selector = if (startCursor >= afterCursorSymbol - 1 && startCursor <= length) {
            startCursor++
        } else {
            formatter.format(value).length - 3
        }

        var text = formatter.format(value)

        if (length <= 3) {
            text = CoreConstant.EMPTY
        }
        editText.setText(text)
        editText.setSelection(selector)
        return text
    }

}


/**
 * Форматирование в формате 0
 */
class AmountFormatSpaceWithoutZeroDelegate :
    AmountFormatter {

    override fun format(s: String, editText: EditText, formatter: DecimalFormat): String {
        val srt = s.replace(" ", CoreConstant.EMPTY)
        val value = formatter.parse(srt)
        val afterCursorSymbol = formatter.format(value).length
        val text = formatter.format(value)
        editText.setText(text)
        editText.setSelection(afterCursorSymbol)
        return text
    }

}