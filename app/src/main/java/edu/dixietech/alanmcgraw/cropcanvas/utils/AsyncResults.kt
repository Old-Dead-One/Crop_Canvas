package edu.dixietech.alanmcgraw.cropcanvas.utils

sealed class AsyncResult<Result> {
    class Loading<Result> : AsyncResult<Result>()
    class Success<Result>(val result: Result) : AsyncResult<Result>()
    class Error<Result>(val message: String) : AsyncResult<Result>()
}