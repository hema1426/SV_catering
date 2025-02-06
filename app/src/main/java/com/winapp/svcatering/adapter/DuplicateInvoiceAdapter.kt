package com.winapp.svcatering.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winapp.svcatering.R
import com.winapp.svcatering.model.DuplicateInvoiceDetail
import com.winapp.svcatering.utils.Utils

class DuplicateInvoiceAdapter(
    private val context: Context,
    private var dataList: ArrayList<DuplicateInvoiceDetail>,
    var deleteDupClickListener : DeleteDuplicateClickListener,
    var editDupClickListener : EditDuplicateClickListener,

    ) : RecyclerView.Adapter<DuplicateInvoiceAdapter.MyViewHolder>() {
    lateinit var selectedModel: DuplicateInvoiceDetail
    var istrue: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.create_invoice_item, parent, false)
        )
    }

    fun getList(): ArrayList<DuplicateInvoiceDetail> {
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
        private var product: TextView? = null
        private var productCode: TextView? = null
        private var actualQty: TextView? = null
        private var netQty: TextView? = null
        private var returnQty: TextView? = null
        private var focQty: TextView? = null
        private var priceValue: TextView? = null
        private val totalValue: TextView? = null
        private var subTotalValue: TextView? = null
        private var gstValue: TextView? = null
        private var netTotalValue: TextView? = null
        private val removeItem: ImageView? = null
        private val editProduct: ImageView? = null

        @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
        fun setData(model: DuplicateInvoiceDetail) {
            val pos = adapterPosition
           // snotxt.setText((pos + 1).toString())
            
            product!!.setText(model.productName.trim { it <= ' ' })
            productCode!!.setText(model.productCode.trim { it <= ' ' })
            actualQty!!.setText(getQty(model.quantity).toString())

           netQty!!.setText(getQty(model.netQuantity).toString())
           returnQty!!.setText(getQty(model.returnQty).toString())
           focQty!!.setText(getQty(model.foc_Qty).toString())
            //  priceValue.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getPrice())));
            //  priceValue.setText(Utils.fourDecimalPoint(Double.parseDouble(model.getPrice())));
           priceValue!!.setText(model.price)

           subTotalValue!!.setText(Utils.twoDecimalPoint(model.subTotal.toDouble()))
           gstValue!!.setText(Utils.twoDecimalPoint(model.priceWithGST.toDouble()))
           netTotalValue!!.setText(Utils.twoDecimalPoint(model.netTotal.toDouble()))

           itemView.setOnLongClickListener(OnLongClickListener { v ->
                val popup = PopupMenu(v.context, v)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit_menu -> {

                            val pos = adapterPosition
                            if (pos != -1) {
                                editDupClickListener.editSelected(pos,model)
                            }
                            true
                        }

                        R.id.delete_menu -> {
                            if (pos != -1) {
                                deleteDupClickListener.deleteSelected(pos,model)
                            }
                            true
                        }

                        else -> false
                    }
                }
                popup.inflate(R.menu.activity_main_drawer)
                popup.show()
                false
            })
//            itemView.setOnClickListener {
//                if (pos != -1) {
//                    val intent = Intent(context, NewDashOverdueInvoiceActivity::class.java)
//                    intent.putExtra("custCode", detailItem.customerNo)
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
        fun getQty(qty: String): Int {
            val `val` = qty.toDouble()
            return `val`.toInt()
        }

        init {
            product = itemView.findViewById<TextView>(R.id.product)
            productCode = itemView.findViewById<TextView>(R.id.item_code)
            actualQty = itemView.findViewById<TextView>(R.id.actual_qty)
            netQty = itemView.findViewById<TextView>(R.id.net_qty)
            returnQty = itemView.findViewById<TextView>(R.id.return_qty)
            focQty = itemView.findViewById<TextView>(R.id.foc)
            priceValue = itemView.findViewById<TextView>(R.id.price)
            subTotalValue = itemView.findViewById<TextView>(R.id.sub_total)
            gstValue = itemView.findViewById<TextView>(R.id.tax)
            netTotalValue = itemView.findViewById<TextView>(R.id.net_total)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updatePdt(duplicateInvoiceDetail: DuplicateInvoiceDetail, istrueVal: Boolean) {
        selectedModel = duplicateInvoiceDetail
        istrue = istrueVal
        notifyDataSetChanged()
    }
    fun updateList(list: ArrayList<DuplicateInvoiceDetail>) {
        dataList = list
        notifyDataSetChanged()
    }

    interface DeleteDuplicateClickListener {
        fun deleteSelected(position: Int,model: DuplicateInvoiceDetail)
    }
    interface EditDuplicateClickListener {
        fun editSelected(position: Int, model: DuplicateInvoiceDetail)
    }

}