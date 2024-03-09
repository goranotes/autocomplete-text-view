package com.goranotes.autocompletetextview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.NonNull
import com.goranotes.autocompletetextview.databinding.ItemDataAutocompleteBinding
import com.goranotes.autocompletetextview.model.DataItemCustomerResponse
import java.util.Locale

class AutoCompleteAdapter(
    context: Context,
    itemsList: MutableList<DataItemCustomerResponse>
): ArrayAdapter<DataItemCustomerResponse>(context, 0, itemsList) {

    private var itemsListFull: MutableList<DataItemCustomerResponse> = itemsList
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val binding: ItemDataAutocompleteBinding
        val view: View

        if (convertView == null) {
            binding = ItemDataAutocompleteBinding.inflate(inflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemDataAutocompleteBinding
            view = convertView
        }

        val name = binding.tvName
        val phone = binding.tvPhone

        val item = getItem(position)

        if (item != null) {
            name.text = item.name
            phone.text = item.phone
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val suggestions: MutableList<DataItemCustomerResponse> = mutableListOf()
                if (constraint == null || constraint.length == 0) {
                    suggestions.addAll(itemsListFull)
                } else {
                    val filterPatern = constraint.toString().toLowerCase().trim { it <= ' ' }
                    for (item in itemsListFull) {
                        item.name?.let {
                            if (it.toLowerCase().contains(filterPatern)) {
                                suggestions.add(item)
                            }
                        }
                    }
                }
                results.values = suggestions
                results.count = suggestions.size
                return results
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults
            ) {
                clear()
                addAll(results.values as MutableList<DataItemCustomerResponse>)
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any): CharSequence {
                return (resultValue as DataItemCustomerResponse).name?:""
            }
        }
    }
}