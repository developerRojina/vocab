package com.vocable.data.result


suspend fun <T> safeCall(block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: Exception) {
        AppResult.Error(e)
    }
}

suspend fun <T> safeCall(
    remote: suspend () -> AppResult<T>,
    local: suspend () -> AppResult<T>
): AppResult<T> {
    return when (val remoteResult = remote()) {
        is AppResult.Success -> local()
        is AppResult.Error -> remoteResult
    }
}
