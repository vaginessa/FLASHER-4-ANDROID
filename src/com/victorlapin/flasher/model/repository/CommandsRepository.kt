package com.victorlapin.flasher.model.repository

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.dao.CommandDao
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

class CommandsRepository constructor(
        private val mCommandDao: CommandDao,
        private val mGson: Gson
) {
    fun getCommands(chainId: Long): Flowable<List<Command>> = mCommandDao.getCommands(chainId)

    fun getCommand(id: Long): Maybe<Command> = mCommandDao.getCommand(id)

    @SuppressLint("CheckResult")
    fun insertCommand(command: Command) {
        Single.just(command)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { c ->
                    if (c.orderNumber == 0) {
                        c.orderNumber = mCommandDao.getNextOrderNumber(c.chainId)
                    }
                    mCommandDao.insert(c)
                }
    }

    fun changeOrder(orderedCommands: List<Command>): Observable<Any> =
            Observable.create<Any> { emitter ->
                mCommandDao.update(orderedCommands)
                emitter.onNext(Any())
                emitter.onComplete()
            }

    @SuppressLint("CheckResult")
    fun updateCommand(command: Command) {
        Single.just(command)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { c -> mCommandDao.update(c) }
    }

    @SuppressLint("CheckResult")
    fun deleteCommand(command: Command) {
        Single.just(command)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { c -> mCommandDao.delete(c) }
    }

    fun exportCommands(fileName: String, chainId: Long): Maybe<EventArgs> = Maybe.create { emitter ->
        Timber.i("Exporting command chain to $fileName")
        var disposable: Disposable? = null
        disposable = getCommands(chainId).subscribe {
            val json = mGson.toJson(it)
            val folder = File(Const.APP_FOLDER)
            folder.mkdirs()
            File(folder, fileName).writeText(json)
            emitter.onSuccess(EventArgs(isSuccess = true, messageId = R.string.success))
            disposable?.dispose()
        }
    }

    fun importCommands(fileName: String, chainId: Long): Maybe<EventArgs> = Maybe.create { emitter ->
        try {
            Timber.i("Importing command chain from $fileName")
            val json = File(fileName).readText()
            val commands = mGson.fromJson<List<Command>>(json, object : TypeToken<List<Command>>() {}.type)
            var i = 0
            commands.forEach {
                it.chainId = chainId
                it.orderNumber = ++i
            }
            mCommandDao.clear(chainId)
            mCommandDao.insert(commands)
            emitter.onSuccess(EventArgs(isSuccess = true, messageId = R.string.success))
        } catch (ex: Exception) {
            ex.printStackTrace()
            emitter.onSuccess(EventArgs(isSuccess = false, message = ex.message))
        }
    }
}