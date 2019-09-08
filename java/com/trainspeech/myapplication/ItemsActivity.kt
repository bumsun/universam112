package com.trainspeech.myapplication

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.utils.RxUtil
import com.wavesplatform.sdk.utils.SignUtil
import com.wavesplatform.sdk.utils.WavesConstants
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_items.*
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


class ItemsActivity : AppCompatActivity() {
    private var amount: Float = 0.0f
    private var ids: String = ""


    lateinit var compositeDisposable: CompositeDisposable;

    private var myAdapter: MyAdapter? = null
    private var items: ArrayList<String>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var rlItems:RecyclerView
    lateinit var payBTN: Button
    lateinit var resultTV: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        resultTV = findViewById(R.id.resultTV);
        val json:String = intent.getStringExtra("items")
        val token = object : TypeToken<ArrayList<String>>() {

        }
        items = Gson().fromJson(json,Array<String>::class.java).toCollection(ArrayList())

        Log.d("myLogs","items size = " + items!!.size)
        Log.d("myLogs","items = " + items)

        initialize()
        setupList()
        loaddata()



        payBTN = findViewById(R.id.payBTN);
        calcOrders();
        payBTN.setOnClickListener({
            val intent = Intent(applicationContext, QRCodeActivity::class.java)

            intent.putExtra("code", transactionsBroadcast(amount, ids));
            startActivity(intent)
        })
    }

    private fun calcOrders(){
        resultTV = findViewById(R.id.resultTV);

        ids = ""
        for (item: String in items!!) {

            val uri = Uri.parse(item)
            try {
                amount += URLDecoder.decode(uri.getQueryParameter("amount"), "UTF-8").toFloat()
                if(ids.isEmpty() || ids.equals("")){
                    ids += URLDecoder.decode(uri.getQueryParameter("i"), "UTF-8").toInt()
                }else{
                    ids += "," + URLDecoder.decode(uri.getQueryParameter("i"), "UTF-8").toInt()
                }

                Log.d("myLogs","ids = " + ids)
            } catch (exception: UnsupportedEncodingException) {
                exception.printStackTrace()
            }
        }
        resultTV.setText(amount.toString().plus(" \u20BD"));
    }

    private fun loaddata() {
        myAdapter!!.notifyDataSetChanged()

    }

    private fun setupList() {
        rlItems!!.layoutManager = layoutManager
        rlItems!!.adapter = myAdapter
    }

    private fun initialize() {
        rlItems = findViewById(R.id.rlItems) as RecyclerView
        layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        myAdapter = MyAdapter(items, this, R.layout.item, resultTV)
    }

    private fun transactionsBroadcast(amount:Float, ids:String) :String {
        Log.d("myLogs", "transactionsBroadcast amount = " + amount);
        Log.d("myLogs", "transactionsBroadcast ids = " + ids);
        // Creation Transfer transaction and fill it with parameters

//        val amountLocal:Long =  (amount*100000000).toLong() ;
        val amountLocal:Long =  (amount*100000).toLong() ;
//        val amountLocal:Long = 100000000L;
        val transferTransaction = TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "3MvgqcdKFrgrNjEbgtsV42Jd3KnkgkywWJY",
            amount = amountLocal,
            attachment = SignUtil.textToBase58(ids),
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY)

        // Sign transaction with seed
        val sign:String = transferTransaction.sign(Define.newSeed)

        val builder = GsonBuilder()
        val gson = builder.create()
        println(gson.toJson(transferTransaction))

        val json = gson.toJson(transferTransaction)
//        val sign:String = transferTransaction.getSignedStringWithSeed(seed = "aware undo tide video bubble lawn attitude large time soup luggage impulse")

        Log.d("myLogs", "json = " + json);
        return json;


//        compositeDisposable.add(WavesSdk.service()
//            .getNode()
//            .transactionsBroadcast(transferTransaction)
//            .compose(RxUtil.applyObservableDefaultSchedulers())
//            .subscribe({ response ->
//                // Do something on success, now we have wavesBalance.balance in satoshi in Long
//            }, { error ->
//                // Do something on fail
//            }))
    }


    private fun getWavesBalance() {
        compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            WavesSdk.service()
                .getNode() // You can choose different Waves services: node, matcher and data service
                .addressesBalance(Define.address) // Here methods of service
                .compose(RxUtil.applyObservableDefaultSchedulers()) // Rx Asynchron settings
                .subscribe({ wavesBalance ->
                    // Do something on success, now we have wavesBalance.balance in satoshi in Long

                    setTitle("Баланс: "+(wavesBalance.balance/100000) + " \u20BD")

                }, { error ->
                    // Do something on fail
                    val errorMessage = "Can't get wavesBalance! + ${error.message}"
                    Log.e("MainActivity", errorMessage)
                    error.printStackTrace()
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                })
        )
    }
    private var isFirst = true

    override fun onResume() {
        super.onResume()

        getWavesBalance();
    }
}
