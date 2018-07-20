package com.victorlapin.flasher.model.repository

import com.google.gson.Gson
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.dao.CommandDao
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

class CommandsRepository constructor(
        private val mCommandDao: CommandDao,
        private val mGson: Gson
) {
    fun getCommands(chainId: Long): Flowable<List<Command>> = mCommandDao.getCommands(chainId)

    fun getCommand(id: Long): Maybe<Command> = mCommandDao.getCommand(id)

    fun insertCommand(command: Command) {
        Single.just(command)
                .map { c ->
                    if (c.orderNumber == 0) {
                        c.orderNumber = mCommandDao.getNextOrderNumber(c.chainId)
                    }
                    mCommandDao.insert(c)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    fun changeOrder(orderedCommands: List<Command>): Observable<Any> =
            Observable.create<Any> { emitter ->
                mCommandDao.update(orderedCommands)
                emitter.onNext(Any())
                emitter.onComplete()
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun updateCommand(command: Command) {
        Single.just(command)
                .map { c -> mCommandDao.update(c) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    fun deleteCommand(command: Command) {
        Single.just(command)
                .map { c -> mCommandDao.delete(c) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    fun exportCommands(fileName: String, json: String): EventArgs {
        val folder = File(Const.APP_FOLDER)
        folder.mkdirs()
        File(folder, fileName).writeText(json)
        return EventArgs(isSuccess = true, messageId = R.string.success)
    }

    fun importJson(fileName: String): String? = try {
        File(fileName).readText()
    } catch (ex: Exception) {
        null
    }

    fun importCommands(commands: List<Command>) {
        mCommandDao.clear(commands.first().chainId)
        mCommandDao.insert(commands)
    }
}