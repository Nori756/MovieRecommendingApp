package com.example.movierecommendingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MoviesListAdapter(context: Context): BaseAdapter() {

    private var mLayoutInflater: LayoutInflater
    private var mMoviesArrayList = ArrayList<Movie>()

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mMoviesArrayList.size
    }

    override fun getItem(position: Int): Any {
        return mMoviesArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_movies, parent, false)
        }

        val titleText = convertView!!.findViewById<View>(R.id.titleTextView) as TextView
        titleText.text = mMoviesArrayList[position].title

        val nameText = convertView.findViewById<View>(R.id.nameTextView) as TextView
        nameText.text = mMoviesArrayList[position].name

        val resText = convertView.findViewById<View>(R.id.resTextView) as TextView
        val resNum = mMoviesArrayList[position].comments.size
        resText.text = resNum.toString()

        val bytes = mMoviesArrayList[position].imageBytes
        if (bytes.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
            val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
            imageView.setImageBitmap(image)
        }

        return convertView
    }

    fun setMovieArrayList(movieArrayList: ArrayList<Movie>) {
        mMoviesArrayList = movieArrayList
    }
}



