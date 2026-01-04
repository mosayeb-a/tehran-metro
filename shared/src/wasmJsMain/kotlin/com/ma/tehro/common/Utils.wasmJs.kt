package com.ma.tehro.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val ioCoroutineDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default