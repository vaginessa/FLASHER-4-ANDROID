package com.victorlapin.flasher.model.database.dao

import androidx.room.*
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface CommandDao {
    @Query("select * from commands where chain_id = :chainId order by order_number")
    fun getCommands(chainId: Long): Flowable<List<Command>>

    @Query("select * from commands where id = :id")
    fun getCommand(id: Long): Maybe<Command>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(command: Command): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(commands: List<Command>)

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(command: Command)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(commands: List<Command>)

    @Transaction
    @Delete
    fun delete(command: Command)

    @Transaction
    @Query("delete from commands where chain_id = :chainId")
    fun clear(chainId: Long)

    @Query("select ifnull(max(order_number),0) + 1 from commands where chain_id = :chainId")
    fun getNextOrderNumber(chainId: Long): Int
}