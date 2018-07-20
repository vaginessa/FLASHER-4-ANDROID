package com.victorlapin.flasher.model.interactor

import com.google.gson.Gson
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.repository.CommandsRepository

class HomeInteractor constructor(
        repo: CommandsRepository,
        gson: Gson
) : BaseCommandsInteractor(repo, gson) {
    override val mChainId = Chain.DEFAULT_ID

    override fun addStubCommand() = insertCommand(Command(type = Command.TYPE_FLASH_FILE))
}