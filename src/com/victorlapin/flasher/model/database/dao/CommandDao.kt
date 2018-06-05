package com.victorlapin.flasher.model.database.dao

import android.arch.persistence.room.*
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface CommandDao {
    @Query("select * from commands order by id")
    fun getCommands(): Flowable<List<Command>>

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
    @Delete
    fun delete(command: Command)
}