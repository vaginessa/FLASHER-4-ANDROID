package com.victorlapin.flasher.model.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command

@Dao
interface TestsDao {
    @Query("select * from chains where id = :id")
    fun testGetChain(id: Long): Chain

    @Query("select * from commands where id = :id")
    fun testGetCommand(id: Long): Command
}