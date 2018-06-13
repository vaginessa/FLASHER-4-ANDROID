package com.victorlapin.flasher.model.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Transaction
import com.victorlapin.flasher.model.database.entity.Chain

@Dao
interface ChainDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chain: List<Chain>)
}