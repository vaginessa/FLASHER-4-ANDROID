package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.repository.CommandsRepository
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ScheduleInteractor constructor(
        repo: CommandsRepository
) : BaseCommandsInteractor(repo) {
    override fun getCommands(): Flowable<List<Command>> = mRepo.getCommands(Chain.SCHEDULE_ID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun addStubCommand() = insertCommand(Command(
            type = Command.TYPE_BACKUP,
            chainId = Chain.SCHEDULE_ID,
            arg1 = "Boot, Cache, System, Data"))

    override fun exportCommands(fileName: String): Maybe<EventArgs> =
            mRepo.exportCommands(fileName, Chain.SCHEDULE_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun importCommands(fileName: String): Maybe<EventArgs> =
            mRepo.importCommands(fileName, Chain.SCHEDULE_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun changeOrder(orderedCommands: List<Command>): Observable<Any> =
            mRepo.changeOrder(orderedCommands, Chain.SCHEDULE_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}