package com.ma.tehro.common

data class AppException(val userFriendlyMessage: String) : Throwable()