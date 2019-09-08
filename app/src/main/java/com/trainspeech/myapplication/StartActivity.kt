package com.trainspeech.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.crypto.WavesCrypto
import net.glxn.qrgen.android.QRCode

class StartActivity : AppCompatActivity() {

    lateinit var enterBTN: TextView
    lateinit var qcIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        enterBTN = findViewById(R.id.enterBTN);
        qcIV = findViewById(R.id.qcIV);

        enterBTN.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        })
        seedPhraseGeneration()


    }

    fun seedPhraseGeneration() {
        // Generate or add your seed-phrase
        val newSeed: String = WavesCrypto.randomSeed()
        // Get address by seed-phrase
        var chainId = WavesSdk.getEnvironment().chainId.toString()
        val address: String = WavesCrypto.addressBySeed(newSeed,chainId)


        Define.address = address
        Define.newSeed = newSeed;


        val code = Define.address
        val myBitmap = QRCode.from(code).withSize(800, 800).bitmap()
        qcIV.setImageBitmap(myBitmap)
    }
}
