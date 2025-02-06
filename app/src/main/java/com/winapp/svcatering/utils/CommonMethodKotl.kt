package com.winapp.svcatering.utils

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.winapp.svcatering.R

object CommonMethodKotl {
    var mDialog: Dialog? = null


    fun cancelProgressDialog() {
        if (mDialog != null
            && mDialog!!.isShowing
        ) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }
    fun showError_dialog(activity: Activity,msg: String) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("Error")
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setPositiveButton(
            "Ok"
        ) { dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }

    fun showProgressDialog(activity: Activity) {
        if (activity != null
            && mDialog == null
        ) {
            mDialog = Dialog(activity)
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setCancelable(false)
            mDialog!!.setCanceledOnTouchOutside(false)
            mDialog!!.setContentView(R.layout.dialog)
            mDialog!!.window!!
                .setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        android.R.color.transparent
                    )
                )
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(mDialog!!.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            mDialog!!.window!!.attributes = lp
            mDialog!!.show()
        }
    }

}
