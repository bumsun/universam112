package com.trainspeech.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import io.reactivex.disposables.CompositeDisposable
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import com.wavesplatform.sdk.utils.*
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), QRCodeReaderView.OnQRCodeReadListener {
    lateinit var compositeDisposable: CompositeDisposable;
    lateinit var qrCodeReaderView:QRCodeReaderView
    lateinit var resultTextView:TextView
    lateinit var payBTN:Button
    lateinit var resultTV: TextView
    lateinit var orderRL: RelativeLayout
    lateinit var photoIV: ImageView
    lateinit var nameTV: TextView
    lateinit var priceTV: TextView






    var items:ArrayList<String> = ArrayList();
    val MY_CAMERA_REQUEST_CODE = 110;

    private var amount: Float = 0.0f
    private var ids: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = this.getSupportActionBar()
        val colorDrawable = ColorDrawable(getColor(R.color.back))
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(colorDrawable)
        }

        compositeDisposable = CompositeDisposable()

        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        resultTextView = findViewById(R.id.resultTextView);
        payBTN = findViewById(R.id.payBTN);
        orderRL = findViewById(R.id.orderRL);
        photoIV = findViewById(R.id.photoIV);
        nameTV = findViewById(R.id.nameTV);
        priceTV = findViewById(R.id.priceTV);

        payBTN.setOnClickListener({
            val intent = Intent(applicationContext, ItemsActivity::class.java)
            val json = Gson().toJson(items)
            intent.putExtra("items", json)
            startActivity(intent)
        })

        resultTextView.setOnClickListener({
            val intent = Intent(applicationContext, QRCodeActivity::class.java)

            var amount: Float = 0f
            val uri = Uri.parse(resultTextView.text.toString())
            try {
                amount = URLDecoder.decode(uri.getQueryParameter("amount"), "UTF-8").toFloat()
                Log.d("myLogs","amount = " + amount)
            } catch (exception: UnsupportedEncodingException) {
                exception.printStackTrace()
            }

            intent.putExtra("code", transactionsBroadcast(amount));
            startActivity(intent)
//            getWavesBalance()
        });
        calcOrders();

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE);
        else{
            initCamera();
        }
        initCamera();

    }

    private fun calcOrders(){
        amount = 0f;
        ids = ""
        resultTV = findViewById(R.id.resultTV);

        var ids = ""
        for (item: String in items!!) {

            val uri = Uri.parse(item)
            try {
                amount += URLDecoder.decode(uri.getQueryParameter("amount"), "UTF-8").toFloat()
                if(ids.isEmpty() || ids.equals("")){
                    ids += URLDecoder.decode(uri.getQueryParameter("i"), "UTF-8").toInt()
                }else{
                    ids += "," + URLDecoder.decode(uri.getQueryParameter("i"), "UTF-8").toInt()
                }

                Log.d("myLogs","amount = " + amount)
            } catch (exception: UnsupportedEncodingException) {
                exception.printStackTrace()
            }
        }
        resultTV.setText(amount.toString().plus(" \u20BD"));
    }




    private fun initCamera() {
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);


        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showOrder(name:String, price:String, photo:String) {


        nameTV.text = name
        priceTV.text = price


        val uriPhoto = Uri.parse(photo)
        Glide.with(this).load(uriPhoto).centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .crossFade().into(object : GlideDrawableImageViewTarget(photoIV) {
                override fun onResourceReady(
                    resource: GlideDrawable,
                    animation: GlideAnimation<in GlideDrawable>?
                ) {
                    super.onResourceReady(resource, animation)
                    //never called
                    //Bitmap bitmap = drawableToBitmap(imageView.getDrawable());
                    Log.d("myLogs","icon succeess = " + resource)
                    photoIV.setImageDrawable(resource)
                }

                override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                    super.onLoadFailed(e, errorDrawable)
                    Log.d("myLogs","e = " + e + " photo = " + photo)
                    //never called
                }
            })


        orderRL.visibility = View.VISIBLE;

        var handler =  Handler(Looper.getMainLooper());
        handler.postDelayed(Runnable {
            orderRL.visibility = View.GONE;
        },3000)
    }

    private fun transactionsBroadcast(amount:Float) :String {

        // Creation Transfer transaction and fill it with parameters

//        val amountLocal:Long =  (amount*100000000).toLong() ;

        val amountLocal:Long = 100000000L;
        val transferTransaction = TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "3MvgqcdKFrgrNjEbgtsV42Jd3KnkgkywWJY",
            amount = amountLocal,
            attachment = SignUtil.textToBase58("Some comment to transaction"),
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY)

        // Sign transaction with seed
         val sign:String = transferTransaction.sign(seed = Define.newSeed)

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

    private var isFirst = true
    private fun getWavesBalance() {

        compositeDisposable.add(
            WavesSdk.service()
                .getNode() // You can choose different Waves services: node, matcher and data service
                .addressesBalance(Define.address) // Here methods of service
                .compose(RxUtil.applyObservableDefaultSchedulers()) // Rx Asynchron settings
                .subscribe({ wavesBalance ->
                    // Do something on success, now we have wavesBalance.balance in satoshi in Long

                    setTitle("Баланс: "+(wavesBalance.balance/100000) + " \u20BD")

                    if(isFirst && wavesBalance.balance > 0){
                        Toast.makeText(
                            this,
                            "Поздравляем вы получили стартовый капитал : ${(wavesBalance.balance/100000)} \u20BD",
                            Toast.LENGTH_SHORT)
                            .show()
                        isFirst = false
                    }
                }, { error ->
                    // Do something on fail
                    val errorMessage = "Can't get wavesBalance! + ${error.message}"
                    Log.e("MainActivity", errorMessage)
                    error.printStackTrace()
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                })
        )
    }


    var tsb = TimerSearchBreaker(TimerSearchBreaker.ISearchTask{

        items.add(it)
        resultTextView.setText(it + " " + items.size)
        calcOrders();

        var price: String = ""
        var photo: String = ""
        var name: String = ""
        val uri = Uri.parse(it)
        try {
            price = URLDecoder.decode(uri.getQueryParameter("amount"), "UTF-8")
            photo = URLDecoder.decode(uri.getQueryParameter("p"), "UTF-8")
            name = URLDecoder.decode(uri.getQueryParameter("n"), "UTF-8")

            Log.d("myLogs","amount = " + amount)
        } catch (exception: UnsupportedEncodingException) {
            exception.printStackTrace()
        }
        showOrder(name,price,photo);
    })

    override fun onQRCodeRead(text: String, points: Array<PointF>) {
        tsb.runBefore(text,3000)
    }

    override fun onResume() {
        super.onResume()
        if(Define.items != null){
            items = Define.items
            calcOrders()
        }
        qrCodeReaderView.startCamera()
        getWavesBalance();
    }

    override fun onPause() {
        super.onPause()
        qrCodeReaderView.stopCamera()
    }


}
