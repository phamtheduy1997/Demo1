package com.rikkei.pets.adapter

import android.content.Context
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.rikkei.pets.R
import java.util.ArrayList

class GalleryAdapter(private val ctx: Context, internal var mArrayUri: ArrayList<Uri>) : BaseAdapter() {
    private var pos: Int = 0
    private var inflater: LayoutInflater? = null
    private var ivGallery: ImageView? = null

    override fun getCount(): Int {
        return mArrayUri.size
    }

    override fun getItem(position: Int): Any {
        return mArrayUri[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {

        pos = position
        inflater = ctx
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView = inflater!!.inflate(R.layout.gv_item, parent, false)

        ivGallery = itemView.findViewById<View>(R.id.im_dog) as ImageView

        ivGallery!!.setImageURI(mArrayUri[position])

        return itemView
    }
}
