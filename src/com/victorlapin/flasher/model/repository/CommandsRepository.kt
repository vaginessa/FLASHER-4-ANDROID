package com.victorlapin.flasher.model.repository

import com.victorlapin.flasher.model.database.dao.CommandDao
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CommandsRepository constructor(
        private val mCommandDao: CommandDao
) {
    fun getCommands(): Flowable<List<Command>> = mCommandDao.getCommands()

    fun getCommand(id: Long): Maybe<Command> = mCommandDao.getCommand(id)

    fun insertCommand(command: Command) {
        Single.just(command)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { c -> mCommandDao.insert(c) }
    }

    fun updateCommand(command: Command) {
        Single.just(command)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { c -> mCommandDao.update(c) }
    }

    fun deleteCommand(command: Command) {
        Single.just(command)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { c -> mCommandDao.delete(c) }
    }
}