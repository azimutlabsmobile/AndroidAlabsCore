package com.alabs.core_application.presentation.ui.widget.editText.textWatcher.amount

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.alabs.core_application.data.constants.CorePatternConstant.PATTERN_FORMAT_AMOUNT_SPACE
import com.alabs.core_application.data.constants.CorePatternConstant.PATTERN_FORMAT_AMOUNT_SPACE_ZERO
import com.alabs.core_application.utils.delegates.AmountFormatSpaceWithoutZeroDelegate
import com.alabs.core_application.utils.delegates.AmountFormatSpaceZeroDelegate
import com.alabs.core_application.utils.delegates.AmountFormatter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


//
//    ====== ZERO =================================================
//    en_US                  0.0         #.# =                    0
//    en_US                  0.0         0.# =                    0
//    en_US                  0.0         #.0 =                   .0
//    en_US                  0.0         0.0 =                  0.0
//    en_US                  0.0        ##.# =                    0
//    en_US                  0.0        00.# =                   00
//    ====== TEN  =================================================
//    en_US                 10.0         #.# =                   10
//    en_US                 10.0         0.# =                   10
//    en_US                 10.0         #.0 =                 10.0
//    en_US                 10.0         0.0 =                 10.0
//    en_US                 10.0        ##.# =                   10
//    en_US                 10.0        00.# =                   10
//    ====== PI   =================================================
//    en_US    3.141592653589793         #.# =                  3.1
//    en_US    3.141592653589793         0.# =                  3.1
//    en_US    3.141592653589793         #.0 =                  3.1
//    en_US    3.141592653589793         0.0 =                  3.1
//    en_US    3.141592653589793        ##.# =                  3.1
//    en_US    3.141592653589793        00.# =                 03.1
//    ====== MILLIONS =============================================
//    en_US    2718281.828459045 ###,###.### =        2,718,281.828
//    de_DE    2718281.828459045 ###,###.### =        2.718.281,828
//    fr_FR    2718281.828459045 ###,###.### =        2 718 281,828
//    nl_BE    2718281.828459045 ###,###.### =        2.718.281,828
//    ====== NINERS   =============================================
//    en_US        9999999.99999         #.# =             10000000
//    en_US        9999999.99999         0.# =             10000000
//    en_US        9999999.99999         #.0 =           10000000.0
//    en_US        9999999.99999         0.0 =           10000000.0
//    en_US        9999999.99999        ##.# =             10000000
//    en_US        9999999.99999        00.# =             10000000
//    en_US        9999999.99999 #.######### =        9999999.99999
//    =============================================================
//


class AmountWithZeroTextWatcher(
    private val pattern: String,
    private val et: EditText,
    private val block: (String) -> Unit
) : TextWatcher {
    private val decimalFormatSymbol = DecimalFormatSymbols().apply {
        groupingSeparator = ' '
    }

    private val formatter: DecimalFormat =
        DecimalFormat(pattern, decimalFormatSymbol).apply {
            maximumFractionDigits = 2
            maximumIntegerDigits = 10
        }

    private var amountFormatter: AmountFormatter? = when (pattern) {
        PATTERN_FORMAT_AMOUNT_SPACE_ZERO -> AmountFormatSpaceZeroDelegate()
        PATTERN_FORMAT_AMOUNT_SPACE -> AmountFormatSpaceWithoutZeroDelegate()
        else -> null
    }


    override fun afterTextChanged(s: Editable) {
        et.removeTextChangedListener(this)
        try {
            val text = amountFormatter?.format(s.toString(), et, formatter).orEmpty()
            block(text)
        } catch (ex: Exception) {
            // do nothing
        }
        et.addTextChangedListener(this)
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
        // do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // do nothing
    }
}


