package com.trainspeech.myapplication;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import net.glxn.qrgen.android.QRCode;

public class QRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        String code = getIntent().getExtras().getString("code");
        Bitmap myBitmap = QRCode.from(code).withSize(800, 800).bitmap();
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(myBitmap);
        Button finishBTN = findViewById(R.id.finishBTN);
        finishBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(getApplicationContext(),"Спасибо! Желаем удачного дня.",Toast.LENGTH_LONG).show();
            }
        });

        setTitle("Оплата заказа");
    }
}
