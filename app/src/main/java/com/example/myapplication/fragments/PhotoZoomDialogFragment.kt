package com.example.myapplication.fragments

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.myapplication.R
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment


class PhotoZoomDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_zoom, container, false)
        val zoomedImageView = view.findViewById<ImageView>(R.id.zoomedImageView)
        val args = arguments
        if (args != null) {
            val photoBitmap = args.getParcelable<Bitmap>(ARG_PHOTO_BITMAP)
            if (photoBitmap != null) {
                zoomedImageView.setImageBitmap(photoBitmap)
            }
        }
        return view
    }

    companion object {
        private const val ARG_PHOTO_BITMAP = "photo_bitmap"
        fun newInstance(photoBitmap: Bitmap?): PhotoZoomDialogFragment {
            val fragment = PhotoZoomDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_PHOTO_BITMAP, photoBitmap)
            fragment.arguments = args
            return fragment
        }
    }
}