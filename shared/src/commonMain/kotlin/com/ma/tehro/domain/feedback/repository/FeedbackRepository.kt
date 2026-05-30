package com.ma.tehro.domain.feedback.repository

interface FeedbackRepository {
    suspend fun send(message: String)
}