package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.repository.CommandsRepository
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class BaseCommandsInteractor constructor(
        protected val mRepo: CommandsRepository
) {
    abstract fun getCommands(): Flowable<List<Command>>

    fun getCommand(id: Long): Maybe<Command> = mRepo.getCommand(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getMovedCommands(fromId: Long, toId: Long): Single<List<Command>> =
            mRepo.getMovedCommands(fromId, toId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun insertCommand(command: Command) = mRepo.insertCommand(command)

    fun updateCommand(command: Command) = mRepo.updateCommand(command)

    fun updateCommands(commands: List<Command>) = mRepo.updateCommands(commands)

    fun deleteCommand(command: Command) = mRepo.deleteCommand(command)

    abstract fun addStubCommand()

    abstract fun exportCommands(fileName: String): Maybe<EventArgs>

    abstract fun importCommands(fileName: String): Maybe<EventArgs>
}