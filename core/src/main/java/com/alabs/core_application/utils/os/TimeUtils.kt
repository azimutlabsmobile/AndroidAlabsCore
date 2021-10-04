package com.alabs.core_application.utils.os

/**
 * Получаем минуты с миллисекунд
 *  @param second
 */
fun convertMillisecondToMinute(time: Long) = time / 1000 / 60

/**
 * Получаем секунды с миллисекунд
 *  @param second
 */
fun convertMillisecondToSecond(time: Long) = (time / 1000 % 60)

/**
 * Получаем с секунд в секунды временного формата
 *  @param second
 */
fun convertSecondToSecond(second: Long) = second % 60

/**
 * Получаем с секунд минуты во врменной формат
 *  @param second
 */
fun convertSecondToMinute(second: Long) = (second % 3600) / 60

/**
 * Получаем с секунд часы во временной формат
 * @param second
 */
fun convertSecondToHour(second: Long) = second / 3600
