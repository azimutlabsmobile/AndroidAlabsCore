package com.alabs.core_application.utils.os

import com.alabs.core_application.data.constants.CoreConstant
import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsKtTest {

    @Test
    fun `test formatWithSpaces with numbers`() {
        assertEquals(formatWithSpaces("0"), "0")
        assertEquals(formatWithSpaces("100"), "100")
        assertEquals(formatWithSpaces("1000.22"), "1 000.22")
        assertEquals(formatWithSpaces("100000.0"), "100 000")
        assertEquals(formatWithSpaces("1000000.010"), "1 000 000.01")
        assertEquals(formatWithSpaces("-100"), "-100")
        assertEquals(formatWithSpaces("-1000.50"), "-1 000.5")
        assertEquals(formatWithSpaces("-10000.666"), "-10 000.666")
        assertEquals(formatWithSpaces("-10000.666"), "-10 000.666")
    }

    @Test
    fun `test formatWithSpaces with null`() {
        assertEquals(formatWithSpaces(null), "")
    }

    @Test
    fun `format with zero amount valid`(){
        assertEquals(formatWithZeroAmount("12345.67", "KZT"),"12 345.67 KZT")
        assertEquals(formatWithZeroAmount("12236525345.67", "KZT"),"12 236 525 345.67 KZT")
        assertEquals(formatWithZeroAmount("1.67", "KZT"),"1.67 KZT")
        assertEquals(formatWithZeroAmount("1", "KZT"),"1.00 KZT")
        assertEquals(formatWithZeroAmount("1.00000", "KZT"),"1.00 KZT")
        assertEquals(formatWithZeroAmount("1.00", "KZT"),"1.00 KZT")
        assertEquals(formatWithZeroAmount("1,0", "KZT"),"1.00 KZT")
        assertEquals(formatWithZeroAmount("109666556", "KZT"),"109 666 556.00 KZT")
        assertEquals(formatWithZeroAmount(",001", "KZT"),"0.01 KZT")
    }


    @Test
    fun `format with zero amount invalid`(){
        assertEquals(formatWithZeroAmount(null, "KZT"),"0.00 KZT")
        assertEquals(formatWithZeroAmount("2abc", "KZT"),"2.00 KZT")
        assertEquals(formatWithZeroAmount("abc", "KZT"),"0.00 KZT")
    }

    @Test
    fun `test unmask valid number`() {
        assertEquals("7777777777", unmaskPhoneNumber("8777 777 77 77"))
        assertEquals("77777777777", unmaskPhoneNumber("8777 777 77 777"))
        assertEquals("7777777777", unmaskPhoneNumber("777 777 77 77"))
        assertEquals("7777777777", unmaskPhoneNumber("7777777777"))
        assertEquals("7777777777", unmaskPhoneNumber("777-777-77-77"))
        assertEquals("7777777777", unmaskPhoneNumber("+7777-777-77-77"))
        assertEquals("7777777777", unmaskPhoneNumber("8777-777-77-77"))
        assertEquals("7477066766", unmaskPhoneNumber("77477066766"))
        assertEquals("7477066766", unmaskPhoneNumber("7477066766"))
    }


    @Test
    fun `test unmask empty number`() {
        assertEquals("", unmaskPhoneNumber(null))
        assertEquals("", unmaskPhoneNumber(""))
    }

    @Test
    fun `test unmask invalid number`() {
        assertEquals("8777", unmaskPhoneNumber("8777"))
        assertEquals("87777", unmaskPhoneNumber("87777"))
        assertEquals("877777", unmaskPhoneNumber("877777"))

    }


    @Test
    fun `makeShorterWord valid`() {
        assertEquals("которая представляет", makeShorterWord("которая представляет", 0))
        assertEquals("которая представляе...", makeShorterWord("которая представляет", 1))
        assertEquals("которая п...", makeShorterWord("которая представляет", 11))
        assertEquals("которая ...", makeShorterWord("которая представляет", 12))
        assertEquals("которая...", makeShorterWord("которая представляет", 13))
        assertEquals("к...", makeShorterWord("которая представляет", 19))
    }

    @Test
    fun `makeShorterWord invalid`() {
        assertEquals(CoreConstant.EMPTY, makeShorterWord("которая представляет", -1))
        assertEquals(CoreConstant.EMPTY, makeShorterWord("которая представляет", -20))
        assertEquals(CoreConstant.EMPTY, makeShorterWord("которая представляет", Int.MAX_VALUE))
        assertEquals(CoreConstant.EMPTY, makeShorterWord("которая представляет", Int.MIN_VALUE))
    }
}