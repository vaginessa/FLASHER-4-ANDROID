package com.victorlapin.flasher.model.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import com.victorlapin.flasher.model.database.dao.ChainDao
import com.victorlapin.flasher.model.database.dao.CommandDao
import com.victorlapin.flasher.model.database.dao.TestsDao
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command

@Database(entities = [
    Command::class,
    Chain::class
], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getCommandDao(): CommandDao
    abstract fun getChainDao(): ChainDao
    abstract fun getTestsDao(): TestsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("create table chains (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)")
                db.execSQL("insert into chains (id, name) values (${Chain.DEFAULT_ID}, \"${Chain.DEFAULT}\")")
                db.execSQL("insert into chains (id, name) values (${Chain.SCHEDULE_ID}, \"${Chain.SCHEDULE}\")")

                db.execSQL("create table commands_temp (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "type INTEGER NOT NULL, arg1 TEXT, arg2 TEXT, chain_id INTEGER NOT NULL, " +
                        "foreign key (chain_id) references chains(id))")
                db.execSQL("insert into commands_temp (id, type, arg1, arg2, chain_id) " +
                        "select id, type, arg1, arg2, ${Chain.DEFAULT_ID} from commands")
                db.execSQL("drop table commands")
                db.execSQL("alter table commands_temp rename to commands")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("create table commands_temp (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "type INTEGER NOT NULL, arg1 TEXT, arg2 TEXT, chain_id INTEGER NOT NULL, " +
                        "order_number INTEGER NOT NULL, " +
                        "foreign key (chain_id) references chains(id))")
                db.execSQL("insert into commands_temp (id, type, arg1, arg2, chain_id, order_number) " +
                        "select id, type, arg1, arg2, chain_id, rowid from commands")
                db.execSQL("drop table commands")
                db.execSQL("alter table commands_temp rename to commands")
            }
        }
    }
}