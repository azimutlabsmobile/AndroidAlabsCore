package com.alabs.core_application.presentation.ui.widget.editText.textWatcher

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.constants.CorePatternConstant.PATTERN_DEFAULT_PHONE_NUMBER


class PhoneNumberTextWatcher(
    private val mEditText: EditText?,
    private val mask: String? = PATTERN_DEFAULT_PHONE_NUMBER,
    private val block : (String) -> Unit?
) :
    TextWatcher {
    private var mMask = CoreConstant.EMPTY
    private var countryCode = CoreConstant.EMPTY
    private var isUpdating = false
    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        if (mEditText == null || mask == null) return
        if (!isUpdating && count == 0) {
            if (countryCode.isNotEmpty() && s.toString() == "+" || s.toString().length < countryCode.length) {
                mMask = CoreConstant.EMPTY
                countryCode = CoreConstant.EMPTY
            }
            block(unmask(s.toString(), countryCode))
            return
        }
        val str = s.toString()
        if (!isUpdating && mMask.isNotEmpty() && countryCode.isNotEmpty() && (str.trim { it <= ' ' } == countryCode || str.trim { it <= ' ' }.length < countryCode.length)) {
            mMask = CoreConstant.EMPTY
            countryCode = CoreConstant.EMPTY
            return
        }
        if (!isUpdating && countryCode == CoreConstant.EMPTY) {
            val text = str.replace(" ", CoreConstant.EMPTY).replace("+", CoreConstant.EMPTY)
            val m: String? = mask
            if (m != null && m.isNotEmpty()) {
                mMask = m
                if (m.contains("+$text")) {
                    countryCode = "+$text"
                } else {
                    countryCode = m.substring(0, m.indexOf(" "))
                    mEditText.setText(countryCode + str)
                }
            }
            return
        }
        if (isUpdating) {
            isUpdating = false
            return
        }
        val masked = mask(mMask, unmask(str, countryCode))
        isUpdating = true
        mEditText.setText(masked)
        mEditText.setSelection(masked.length)

        if (str.length <= mask.length) {
            val unmaskStr = unmask(str, countryCode)
            block(unmaskStr)
        }
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun afterTextChanged(s: Editable) {
        // do nothing
    }

    private fun unmask(s: String, countryCode: String): String {
        var s = s
        s = s.replace("+", CoreConstant.EMPTY)
        s = s.substring(countryCode.replace("+", "").length)
        s = s.replace("\\D".toRegex(), CoreConstant.EMPTY)
        return s
    }

    private fun mask(format: String, text: String): String {
        var maskedText = ""
        var i = 0
        for (character in format.toCharArray()) {
            if (character != '#') {
                maskedText += character
                continue
            }
            maskedText += try {
                text[i]
            } catch (e: Exception) {
                break
            }
            i++
        }
        return maskedText
    }

}