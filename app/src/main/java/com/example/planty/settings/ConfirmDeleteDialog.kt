package com.example.planty.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.planty.R
import com.example.planty.MainActivity

class ConfirmDeleteDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm)

        findViewById<TextView>(R.id.tvDialogMessage).text = context.getString(R.string.dialog_delete_account_confirm)

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }

        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            // TODO: 계정 삭제 처리
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            dismiss()
        }
    }
} 