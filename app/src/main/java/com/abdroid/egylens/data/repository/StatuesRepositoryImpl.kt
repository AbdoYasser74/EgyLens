package com.abdroid.egylens.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.abdroid.egylens.data.local.StatueDao
import com.abdroid.egylens.domain.model.Statue
import com.abdroid.egylens.domain.repository.StatuesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StatuesRepositoryImpl @Inject constructor(
    private val statueDao: StatueDao
) : StatuesRepository {


    override suspend fun insertStatue(statue: Statue) {
        statueDao.insertStatue(statue)
    }

    override suspend fun deleteStatue(statue: Statue) {
        statueDao.deleteStatue(statue)
    }

    override fun getAllStatues(): Flow<List<Statue>> {
        return statueDao.getAllStatues()
    }

    override suspend fun getStatue(url: String): Statue? {
        return statueDao.getStatue(url = url)
    }
}