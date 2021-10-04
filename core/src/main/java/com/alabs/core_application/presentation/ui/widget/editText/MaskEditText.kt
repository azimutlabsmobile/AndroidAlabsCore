package com.alabs.core_application.presentation.ui.widget.editText

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import br.com.sapereaude.maskedEditText.MaskedEditText


// TODO удалить после обсуждения
class MaskEditText : MaskedEditText, TextWatcher {

    constructor (context: Context?) : super(context) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init()
    }


    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    private fun init() {
        // do nothing
    }


}