package com.winapp.svcatering.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import android.widget.CompoundButton
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.winapp.svcatering.R
import com.winapp.svcatering.model.UomModel
import com.winapp.svcatering.utils.SharedPreferenceUtil


class UomSettingTransferAdapter(
    var context: Context,
    var uomModels: ArrayList<UomModel>,
//    var onPaymentOptionChanged: OnPaymentOptionChanged
) : RecyclerView.Adapter<UomSettingTransferAdapter.ProductsViewHolder>() {
    private var selectedPosition = -1 // 0 selection by default
    var paymodeTextWatcher: TextWatcher? = null
    var slipNoTextWatcher: TextWatcher? = null
    private var sharedPreferenceUtil: SharedPreferenceUtil? = null
    private var settingUOMval: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ProductsViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.new_uom_settings_items, parent, false)

    )

    override fun getItemCount() = uomModels.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(
        holder: ProductsViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
       // holder.bind(uomModels[position], context)
        val uomModel = uomModels[position]
        sharedPreferenceUtil = SharedPreferenceUtil(context)
        settingUOMval = sharedPreferenceUtil!!.getStringPreference(sharedPreferenceUtil!!.KEY_SETTING_TRANS_UOM, "")
        Log.w("settingUOMadpt..",""+settingUOMval)

//        if(settingUOMval!!.isNotEmpty()) {
//            if (uomModel.uomCode == settingUOMval) {
//                holder.uomCheckBox.setChecked(true);
//                Log.w("uomadapter11","$uomModel")
//            }
//            else{
//                holder.uomCheckBox.setChecked(false);
//                Log.w("uomadapter22","$uomModel")
//            }
//
//            }

        holder.uomCheckBox.setOnClickListener { // checkbox.setChecked(true);
            selectedPosition = position
            Log.w(
                "uomadapter_trans", ".." + uomModels.get(selectedPosition).uomName)
            sharedPreferenceUtil!!.setStringPreference(
                sharedPreferenceUtil!!.KEY_SETTING_TRANS_UOM, (uomModels.get(selectedPosition).uomCode))

            notifyDataSetChanged()
        }
        holder.uomCheckBox.setText(uomModel.uomName)

        if (selectedPosition === position) {
           // uomModels.get(position).checked = true
            holder.uomCheckBox.setChecked(true);
        } else {
            holder.uomCheckBox.setChecked(false);
        }

        holder.uomCheckBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(selectedPosition != -1) {
                if (isChecked) {
//                    Log.w(
//                        "uomadapter", ".." + uomModels.get(selectedPosition).uomName
//                    )
//                    sharedPreferenceUtil!!.setStringPreference(
//                        sharedPreferenceUtil!!.KEY_SETTING_TRANS_UOM, (uomModels.get(selectedPosition).uomCode))

                }
            }
//            else{
//                sharedPreferenceUtil!!.setStringPreference(
//                    sharedPreferenceUtil!!.KEY_SETTING_TRANS_UOM, "")
//            }
        })
    }
    class ProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var uomCheckBox = view.findViewById<AppCompatCheckBox>(R.id.uom_setting_checkb)



//        fun bind(uomcode: UomModel, context: Context) {
//            uomCheckBox.text = uomcode.uomName
//            uomCheckBox.setOnCheckedChangeListener({ buttonView: CompoundButton?, isChecked: Boolean ->
//                uomcode.checked = isChecked
//            })
//
//        //    uomCheckBox.isChecked = uomcode.checked
////        }
//
////            payAmount.setText(paymode.amount.toString())
////            slipNo.setText(paymode.slipno.toString())
//        }
    }

    interface OnPaymentOptionChanged {
        fun calculateBalance(payamount: Double)
        fun calculateBalanceWithServiceCharge(
            paymode: String,
            payamount: Double,
            minRange: String,
            maxRange: String,
            percentage: String
        )

        fun setKeyboardConnection(ic: InputConnection, @IdRes id: Int)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position

    }


}