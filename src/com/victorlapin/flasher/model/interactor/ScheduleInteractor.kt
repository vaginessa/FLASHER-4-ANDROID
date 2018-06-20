package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.repository.CommandsRepository
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ScheduleInteractor constructor(
        private val mRepo: CommandsRepository
) {
    fun getSchedule(): Flowable<List<Command>> = mRepo.getCommands(Chain.SCHEDULE_ID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getCommand(id: Long): Maybe<Command> = mRepo.getCommand(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun insertCommand(command: Command) = mRepo.insertCommand(command)

    fun updateCommand(command: Command) = mRepo.updateCommand(command)

    fun deleteCommand(command: Command) = mRepo.deleteCommand(command)

    fun addStubCommand() = insertCommand(Command(
            type = Command.TYPE_BACKUP,
            chainId = Chain.SCHEDULE_ID,
            arg1 = "Boot, Cache, System, Data"))

    fun exportSchedule(fileName: String): Maybe<EventArgs> =
            mRepo.exportCommands(fileName, Chain.SCHEDULE_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun importSchedule(fileName: String): Maybe<EventArgs> =
            mRepo.importCommands(fileName, Chain.SCHEDULE_ID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}