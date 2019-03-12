package com.victorlapin.flasher.ui.services

import android.content.Intent
import android.service.quicksettings.TileService
import android.widget.Toast
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import com.victorlapin.flasher.ui.activities.RebootDialogActivity
import com.victorlapin.flasher.view.ScriptTileServiceView
import org.koin.android.ext.android.getKoin

class ScriptTileService : TileService(), ScriptTileServiceView {
    private val mScope = getKoin().getOrCreateScopeWithType<ScriptTileService>("")
    private val mPresenter by mScope.inject<ScriptTileServicePresenter>()

    override fun onClick() {
        unlockAndRun {
            mPresenter.setView(this)
            mPresenter.buildAndDeploy()
        }
    }

    override fun onStopListening() {
        mPresenter.cleanup()
        mScope.close()
        super.onStopListening()
    }

    override fun showInfoToast(args: EventArgs) {
        args.message?.let {
            Toast.makeText(
                baseContext, it.replace("\n", "").trim(),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        args.messageId?.let {
            Toast.makeText(
                baseContext, getString(it),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun showInfoToast(message: String) =
        Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()

    override fun showRebootDialog() {
        val intent = Intent(this, RebootDialogActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}