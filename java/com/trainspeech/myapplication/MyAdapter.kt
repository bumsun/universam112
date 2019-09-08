package com.trainspeech.myapplication

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


/**
 * Created by siddhartha on 23/5/17.
 */

internal class MyAdapter(private val arrayList: ArrayList<String>?,
                         private val context: Context,
                         private val layout: Int, private val resultTV: TextView) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    private var amount: Float = 0f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
        arrayList?.get(position)?.let { holder.bindItems(it) }
    }

    override fun getItemCount(): Int {
        return arrayList?.size!!
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(text: String) {
            val uri = Uri.parse(text.toString())

            var amount:String = ""
            var name:String = ""
            var photo:String = ""
            var id:String = ""
            var iconIV = itemView.findViewById<ImageView>(R.id.iconIV);
            var nameTV = itemView.findViewById<TextView>(R.id.nameTV);
            var priceTV = itemView.findViewById<TextView>(R.id.priceTV);
            var minusTV: TextView = itemView.findViewById(R.id.minusTV);
            var counterTV: TextView = itemView.findViewById(R.id.counterTV);
            var plusTV: TextView = itemView.findViewById(R.id.plusTV);

            try {

                amount = URLDecoder.decode(uri.getQueryParameter("amount"), "UTF-8")
                photo = URLDecoder.decode(uri.getQueryParameter("p"), "UTF-8")
                name = URLDecoder.decode(uri.getQueryParameter("n"), "UTF-8")
                id = URLDecoder.decode(uri.getQueryParameter("i"), "UTF-8")
//                photo = "https://raw.githubusercontent.com/mjstest/orgb3/bb18324ff3a593b137708256bfd1e6b7/Screenshot%202019-09-07%20at%2021.51.11.png"


                Log.d("myLogs","amount = " + amount)
            } catch (exception: UnsupportedEncodingException) {
                exception.printStackTrace()
            }
            nameTV.text = name
            priceTV.text = amount
            val uriPhoto = Uri.parse(photo)
            Glide.with(context).load(uriPhoto).centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .crossFade().into(object : GlideDrawableImageViewTarget(iconIV) {
                    override fun onResourceReady(
                        resource: GlideDrawable,
                        animation: GlideAnimation<in GlideDrawable>?
                    ) {
                        super.onResourceReady(resource, animation)
                        //never called
                        //Bitmap bitmap = drawableToBitmap(imageView.getDrawable());
                        Log.d("myLogs","icon succeess = " + resource)
                        iconIV.setImageDrawable(resource)
                    }

                    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                        Log.d("myLogs","e = " + e)
                        //never called
                    }
                })
            minusTV.setOnClickListener(View.OnClickListener {
                if (arrayList != null) {
                    arrayList.remove(text)
                    notifyDataSetChanged()
                    calcOrders(resultTV)
                    Define.items = arrayList;
                }
//                for (item2: String in arrayList!!) {
//                    val uri = Uri.parse(item2)
//                    try {
//                        if(id.equals(URLDecoder.decode(uri.getQueryParameter("i"), "UTF-8").toInt())){
//                            arrayList.remove(item2)
//                        }
//
//                    } catch (exception: UnsupportedEncodingException) {
//                        exception.printStackTrace()
//                    }
//
//                }
            })


        }
    }

    private fun calcOrders(resultTV:TextView){

        for (item: String in arrayList!!) {

            val uri = Uri.parse(item)
            try {
                amount += URLDecoder.decode(uri.getQueryParameter("amount"), "UTF-8").toFloat()

            } catch (exception: UnsupportedEncodingException) {
                exception.printStackTrace()
            }
        }
        resultTV.setText(amount.toString().plus(" \u20BD"));
    }
    fun replacer(data: String): String {
        var data2 = ""
        try {
            val tempBuffer = StringBuffer()
            var incrementor = 0
            val dataLength = data.length
            while (incrementor < dataLength) {
                val charecterAt = data[incrementor]
                if (charecterAt == '%') {
                    tempBuffer.append("<percentage>")
                } else if (charecterAt == '+') {
                    tempBuffer.append("<plus>")
                } else {
                    tempBuffer.append(charecterAt)
                }
                incrementor++
            }
            var data2 = tempBuffer.toString()
            data2 = URLDecoder.decode(data, "utf-8")
            data2 = data.replace("<percentage>".toRegex(), "%")
            data2 = data.replace("<plus>".toRegex(), "+")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return data2
    }
}