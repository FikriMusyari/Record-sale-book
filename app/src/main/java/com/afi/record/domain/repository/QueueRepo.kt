package com.afi.record.domain.repository

import com.afi.record.domain.models.CreateQueueRequest
import com.afi.record.domain.models.QueueResponse
import com.afi.record.domain.models.UpdateQueueRequest

interface QueueRepo {
    suspend fun createQueue(request: CreateQueueRequest): QueueResponse
    suspend fun getAllQueue(): QueueResponse
    suspend fun updateQueue(queueId: Number, request: UpdateQueueRequest)
    suspend fun deleteQueue(queueId: Number)
}
