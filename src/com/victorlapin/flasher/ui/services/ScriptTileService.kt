package com.victorlapin.flasher.ui.services

import android.service.quicksettings.TileService
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import com.victorlapin.flasher.view.ScriptTileServiceView
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.releaseContext

class ScriptTileService : TileService(), ScriptTileServiceView {
    private val mPresenter by inject<ScriptTileServicePresenter>()

    override fun onClick() {
        unlockAndRun {
            mPresenter.setView(this)
            mPresenter.buildAndDeploy()
        }
    }

    override fun onStopListening() {
        releaseContext(Screens.SERVICE_SCRIPT)
        super.onStopListening()
    }

    override fun showInfoToast(args: EventArgs) {
        args.message?.let {
            Toast.makeText(baseContext, it.replace("\n", "").trim(),
                    Toast.LENGTH_LONG).show()
            return
        }
        args.messageId?.let {
            Toast.makeText(baseContext, getString(it),
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun showRebootDialog() {
        AlertDialog.Builder(baseContext)
                .setTitle(R.string.app_name)
                .setMessage(R.string.reboot)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, { _, _ -> mPresenter.reboot() })
                .setNegativeButton(android.R.string.no, null)
                .show()
    }
}