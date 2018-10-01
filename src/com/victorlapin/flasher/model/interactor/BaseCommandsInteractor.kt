package com.victorlapin.flasher.model.interactor

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.victorlapin.flasher.R
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.repository.CommandsRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

abstract class BaseCommandsInteractor constructor(
        private val mRepo: CommandsRepository,
        private val mGson: Gson
) {
    protected abstract val mChainId: Long

    fun getCommands(): Flowable<List<Command>> = mRepo.getCommands(mChainId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getCommand(id: Long): Maybe<Command> = mRepo.getCommand(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun insertCommand(command: Command) = mRepo.insertCommand(command)

    fun updateCommand(command: Command) = mRepo.updateCommand(command)

    fun deleteCommand(command: Command) = mRepo.deleteCommand(command)

    fun changeOrder(orderedCommands: List<Command>): Completable =
            mRepo.changeOrder(orderedCommands)

    abstract fun addStubCommand()

    fun exportCommands(fileName: String): Single<EventArgs> =
            getCommands()
                    .firstOrError()
                    .map { commands ->
                        commands.filter { c ->
                            c.type == Command.TYPE_DECRYPT_PIN ||
                                    c.type == Command.TYPE_DECRYPT_PATTERN
                        }.forEach {
                            it.arg1 = null
                        }
                        val json = mGson.toJson(commands)
                        mRepo.exportCommands(fileName, json)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun importCommands(fileName: String): Single<EventArgs> =
            Single.create<EventArgs> { emitter ->
                try {
                    val json = mRepo.importJson(fileName)
                    val commands = mGson.fromJson<List<Command>>(json, object : TypeToken<List<Command>>() {}.type)
                    if (commands.isNotEmpty()) {
                        var i = 0
                        commands.forEach {
                            it.chainId = mChainId
                            it.orderNumber = ++i
                        }
                        mRepo.importCommands(commands)
                    }
                    emitter.onSuccess(EventArgs(isSuccess = true, messageId = R.string.success))
                } catch (ex: Exception) {
                    Timber.e(ex)
                    emitter.onSuccess(EventArgs(isSuccess = false, message = ex.message))
                }
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}