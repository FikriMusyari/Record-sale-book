package com.afi.record.data.repositoryImpl

import com.afi.record.data.remotes.ApiService
import com.afi.record.domain.models.CreateQueueRequest
import com.afi.record.domain.models.QueueResponse
import com.afi.record.domain.models.UpdateQueueRequest
import com.afi.record.domain.repository.QueueRepo
import javax.inject.Inject

class QueueRepoImpl @Inject constructor(
    private val api: ApiService
) : QueueRepo {
    
    override suspend fun createQueue(request: CreateQueueRequest): QueueResponse = 
        api.createQueue(request)
    
    override suspend fun getAllQueue(): QueueResponse = 
        api.getAllQueue()
    
    override suspend fun updateQueue(queueId: Number, request: UpdateQueueRequest) = 
        api.updateQueue(queueId, request)
    
    override suspend fun deleteQueue(queueId: Number) = 
        api.deleteQueue(queueId)
}
