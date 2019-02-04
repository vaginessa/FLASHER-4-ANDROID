package com.victorlapin.flasher

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    DatabaseMigrationTest::class
)
class DatabaseTestSuite