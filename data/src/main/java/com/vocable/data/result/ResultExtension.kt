package com.vocable.data.result


inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onError(action: (Throwable) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(exception)
    return this
}