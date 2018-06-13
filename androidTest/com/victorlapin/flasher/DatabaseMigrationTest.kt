package com.victorlapin.flasher

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.Room
import android.arch.persistence.room.testing.MigrationTestHelper
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.victorlapin.flasher.model.database.AppDatabase
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    @Rule
    @JvmField
    val mTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate_12() {
        val db = mTestHelper.createDatabase(TEST_DB_NAME, 1)
        insertCommand(id = 1L, type = Command.TYPE_BACKUP, arg1 = "Boot, Cache, System, Data", db = db)
        insertCommand(id = 2L, type = Command.TYPE_WIPE, arg1 = "Cache, Dalvik-cache, System", db = db)
        insertCommand(id = 3L, type = Command.TYPE_FLASH_FILE, db = db)
        db.close()

        mTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 2,
                false, AppDatabase.MIGRATION_1_2)
        val dao = getMigratedDatabase().getTestsDao()

        val testChain = Chain(id = Chain.DEFAULT_ID, name = Chain.DEFAULT)
        val chain = dao.testGetChain(testChain.id!!)
        assertEquals(testChain.name, chain.name)

        val testCommand = Command(id = 1L, type = Command.TYPE_BACKUP, arg1 = "Boot, Cache, System, Data")
        val command = dao.testGetCommand(testCommand.id!!)
        assertEquals(testCommand.type, command.type)
        assertEquals(testCommand.arg1, command.arg1)
        assertEquals(command.chainId, Chain.DEFAULT_ID)
    }

    private fun insertCommand(id: Long, type: Int, arg1: String? = null, db: SupportSQLiteDatabase) {
        val cv = ContentValues()
        cv.put("id", id)
        cv.put("type", type)
        cv.put("arg1", arg1)
        db.insert("commands", SQLiteDatabase.CONFLICT_REPLACE, cv)
    }

    private fun getMigratedDatabase(): AppDatabase {
        val database = Room.databaseBuilder(
                InstrumentationRegistry.getTargetContext(),
                AppDatabase::class.java,
                TEST_DB_NAME)
                .allowMainThreadQueries()
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()
        mTestHelper.closeWhenFinished(database)
        return database
    }

    companion object {
        const val TEST_DB_NAME = "flasher_test.db"
    }
}