package com.winapp.svcatering.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winapp.svcatering.CommonMethods
import com.winapp.svcatering.R
import com.winapp.svcatering.model.ExpenseModuleAddModel
import com.winapp.svcatering.utils.Utils

class ExpenseModuleAddAdapter(
    private val context: Context,
    private var dataList: ArrayList<ExpenseModuleAddModel>,
    private var editClickListener: EditClickListener,
    private var deleteClickListener: DeleteClickListener


) : RecyclerView.Adapter<ExpenseModuleAddAdapter.MyViewHolder>() {
    lateinit var selectedModel: ExpenseModuleAddModel
    var istrue: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.expense_module_add_items, parent, false)
        )
    }

    fun getList(): ArrayList<ExpenseModuleAddModel> {
        return dataList
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var serive_nametxt : TextView
        var serive_amttxt : TextView
        var edit_item : ImageView
        var delete_item : ImageView
        var expensItem_layl : LinearLayout

        @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
        fun setData(detailItem: ExpenseModuleAddModel) {
            val pos = adapterPosition
            serive_nametxt.text = detailItem.expenseName.toString()
            serive_amttxt.text = Utils.twoDecimalPoint(detailItem.expenseAmt!!.toDouble())

            if (::selectedModel.isInitialized
                && selectedModel.expenseName == detailItem.expenseName
                && istrue
            ) {

                object : CountDownTimer(1000, 500) {
                    override fun onTick(millisUntilFinished: Long) {
                        CommonMethods.setBlinkingLay(expensItem_layl)
//                        CommonMethods.setBlinkingText(serive_amttxt)
                    }

                    override fun onFinish() {
                        expensItem_layl.clearAnimation()
//                        serive_amttxt.clearAnimation()

                    }
                }.start()

            } else {
                expensItem_layl.clearAnimation()
//                serive_amttxt.clearAnimation()
            }
            edit_item.setOnClickListener(View.OnClickListener {
                if (editClickListener != null) {
                    val pos = adapterPosition
                    if (pos != -1) {
                        editClickListener.editSelected(detailItem)
                    }
                }
            })
            delete_item.setOnClickListener(View.OnClickListener {
                    val pos = adapterPosition
                    if (pos != -1) {
                        istrue = false
                        deleteClickListener.deleteSelected(pos)
                    }
                })
//            itemView.setOnClickListener {
//                if (pos != -1) {
//                    val intent = Intent(context, NewDashOverdueInvoiceActivity::class.java)
//                    intent.putExtra("custCodeOver", detailItem.customerNo)
//                    intent.putExtra("custNameOver", detailItem.customerName)
//                    context.startActivity(intent)
//                }
////                if (movementClickListener != null) {
////                    val pos = adapterPosition
////                    if (pos != -1) {
////                        movementClickListener!!.movementSelected(pos, detailItem)
////                    }
////                }
//            }

        }



        init {
            val pos = adapterPosition
            serive_nametxt = itemView.findViewById<View>(R.id.serviceName_expen_item) as TextView
            serive_amttxt = itemView.findViewById<View>(R.id.amount_expen_item) as TextView
            edit_item = itemView.findViewById<View>(R.id.edit_itemExpens) as ImageView
            delete_item = itemView.findViewById<View>(R.id.delete_itemExpens) as ImageView
            expensItem_layl = itemView.findViewById<View>(R.id.expensItem_lay) as LinearLayout

        }
    }

    fun updateList(list: ArrayList<ExpenseModuleAddModel>) {
        dataList = list
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updatePdt(expenseModuleAddModel: ExpenseModuleAddModel, istrueVal: Boolean) {
        selectedModel = expenseModuleAddModel
        istrue = istrueVal
        notifyDataSetChanged()
    }

    interface EditClickListener {
        fun editSelected(expenseModuleAddModel: ExpenseModuleAddModel)
    }


    interface DeleteClickListener {
        fun deleteSelected(position: Int)
    }
}