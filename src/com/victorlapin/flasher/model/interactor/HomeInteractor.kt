package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.repository.CommandsRepository
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeInteractor constructor(
        repo: CommandsRepository
) : BaseCommandsInteractor(repo) {
    override fun getCommands(): Flowable<List<Command>> = mRepo.getCommands(Chain.DEFAULT_ID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun addStubCommand() = insertCommand(Command(type = Command.TYPE_FLASH_FILE))

    override fun exportCommands(fileName: String): Maybe<EventArgs> =
            mRepo.exportCommands(fileName, Chain.DEFAULT_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun importCommands(fileName: String): Maybe<EventArgs> =
            mRepo.importCommands(fileName, Chain.DEFAULT_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}