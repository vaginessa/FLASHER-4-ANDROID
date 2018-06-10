package com.victorlapin.flasher.model.repository

import com.google.gson.Gson
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.dao.CommandDao
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class CommandsRepository constructor(
        private val mCommandDao: CommandDao,
        private val mGson: Gson
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

    fun exportCommands(fileName: String): Maybe<EventArgs> = Maybe.create { emitter ->
        var disposable: Disposable? = null
        disposable = getCommands().subscribe {
            val json = mGson.toJson(it)
            val folder = File(Const.APP_FOLDER)
            folder.mkdirs()
            File(folder, fileName).writeText(json)
            emitter.onSuccess(EventArgs(isSuccess = true, messageId = R.string.success))
            disposable?.dispose()
        }
    }
}