package com.victorlapin.flasher.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.victorlapin.flasher.model.database.entity.Chain

@Dao
interface ChainDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chain: List<Chain>)
}