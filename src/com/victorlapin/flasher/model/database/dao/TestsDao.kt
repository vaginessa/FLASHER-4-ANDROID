package com.victorlapin.flasher.model.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command

@Dao
interface TestsDao {
    @Query("select * from chains where id = :id")
    fun testGetChain(id: Long): Chain

    @Query("select * from commands where id = :id")
    fun testGetCommand(id: Long): Command
}