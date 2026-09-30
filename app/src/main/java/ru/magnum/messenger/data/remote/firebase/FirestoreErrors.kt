package ru.magnum.messenger.data.remote.firebase

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import java.io.IOException

/**
 * true - временная ошибка (нет сети, таймаут, перегрузка сервера) - нужно повторить позже
 * false - постоянная ошибка (нет прав, невалидные данные, баг) - повтор бессмысленен,
 *         сообщение надо пометить как FAILED или оно будет вечно крутиться в очереди
 */
fun Throwable.isTempError(): Boolean = when (this) {
    is FirebaseFirestoreException -> code in CODES
    is FirebaseNetworkException -> true
    is IOException -> true
    else -> false
}

private val CODES = setOf(
    Code.UNAVAILABLE,
    Code.DEADLINE_EXCEEDED,
    Code.ABORTED,
    Code.RESOURCE_EXHAUSTED,
    Code.CANCELLED,
    Code.INTERNAL,
    Code.UNKNOWN,
    Code.UNAUTHENTICATED
)
