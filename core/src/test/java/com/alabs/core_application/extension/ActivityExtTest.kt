package com.alabs.core_application.extension

import android.app.Activity
import com.alabs.core_application.utils.extensions.showActivityAndClearBackStack
import org.junit.Assert
import org.junit.Test

class ActivityExtTest {

    @Test
    fun `showActivityAndClearBackStack isNull activity test`() {
        Assert.assertEquals(getMessageIsNullActivity(), "Укажите activity")
    }

    private fun getMessageIsNullActivity() = try {
        val activity = Activity()
        activity.showActivityAndClearBackStack(null)
        "ok"
    }catch (e : Throwable){
        "Укажите activity"
    }

}