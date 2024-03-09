package com.goranotes.autocompletetextview.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.goranotes.autocompletetextview.adapter.AutoCompleteAdapter
import com.goranotes.autocompletetextview.databinding.ActivityMainBinding
import com.goranotes.autocompletetextview.model.DataItemCustomerResponse
import java.io.InputStream
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var adapterAutoComplete: AutoCompleteAdapter
    private var itemsCustomerList: MutableList<DataItemCustomerResponse> = mutableListOf()

    private lateinit var timer: Timer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterAutoComplete = AutoCompleteAdapter(this, itemsCustomerList)
        binding.actvSearch.setAdapter(adapterAutoComplete)
        binding.actvSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (binding.actvSearch.isPerformingCompletion()) {
                    return
                }else{

                    binding.pbSearch.visibility = View.VISIBLE

                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            if(!s.isNullOrBlank()){
                                runOnUiThread {
                                    getDataCustomerList(s.toString())
                                }
                            }else{
                                runOnUiThread {
                                    binding.pbSearch.visibility = View.GONE
                                }
                            }
                        }
                    }, 500)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(::timer.isInitialized){
                    timer.cancel();
                }
            }
        })


    }

    private fun getDataCustomerList(keyword: String){

        val inputStream: InputStream = this.assets.open("data_customer_list.json")
        val inputString = inputStream.bufferedReader().use{it.readText()}
        val data: List<DataItemCustomerResponse> = Gson().fromJson(
            inputString,
            object : TypeToken<List<DataItemCustomerResponse?>?>() {}.type
        )
        val filteredList = data.filter {
            it.name?.contains(keyword, ignoreCase = true) ?: false
        }

        itemsCustomerList.clear()
        itemsCustomerList.addAll(filteredList)
        adapterAutoComplete.notifyDataSetChanged()
        binding.pbSearch.visibility = View.GONE
    }
}