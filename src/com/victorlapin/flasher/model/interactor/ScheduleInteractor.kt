package com.victorlapin.flasher.model.interactor

import com.google.gson.Gson
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.repository.CommandsRepository

class ScheduleInteractor constructor(
    repo: CommandsRepository,
    gson: Gson
) : BaseCommandsInteractor(repo, gson) {
    override val mChainId = Chain.SCHEDULE_ID

    override fun addStubCommand() = insertCommand(
        Command(
            type = Command.TYPE_BACKUP,
            chainId = Chain.SCHEDULE_ID,
            arg1 = "Boot, Cache, System, Data"
        )
    )
}