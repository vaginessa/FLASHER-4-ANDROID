package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.repository.CommandsRepository
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class BaseCommandsInteractor constructor(
        protected val mRepo: CommandsRepository
) {
    abstract fun getCommands(): Flowable<List<Command>>

    fun getCommand(id: Long): Maybe<Command> = mRepo.getCommand(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun insertCommand(command: Command) = mRepo.insertCommand(command)

    fun updateCommand(command: Command) = mRepo.updateCommand(command)

    fun deleteCommand(command: Command) = mRepo.deleteCommand(command)

    fun changeOrder(orderedCommands: List<Command>): Observable<Any> =
            mRepo.changeOrder(orderedCommands)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    abstract fun addStubCommand()

    abstract fun exportCommands(fileName: String): Maybe<EventArgs>

    abstract fun importCommands(fileName: String): Maybe<EventArgs>
}