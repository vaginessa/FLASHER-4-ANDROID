package com.victorlapin.flasher.ui.services

import android.content.Intent
import android.service.quicksettings.TileService
import android.widget.Toast
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import com.victorlapin.flasher.ui.activities.RebootDialogActivity
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

    override fun showRebootDialog() =
            startActivity(Intent(this, RebootDialogActivity::class.java))
}