package com.example.myapplication.fragments

import android.Manifest
import android.graphics.Bitmap
import com.google.android.gms.location.FusedLocationProviderClient
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.myapplication.R
import com.google.android.gms.location.LocationServices
import android.graphics.BitmapFactory
import android.content.Intent
import android.app.Activity
import android.content.pm.PackageManager
import com.example.myapplication.models.PlantRecord
import com.example.myapplication.AppDatabase
import android.app.DatePickerDialog.OnDateSetListener
import android.app.DatePickerDialog
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.provider.MediaStore
import com.example.myapplication.MainActivity
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.net.Uri
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentItemBinding
import java.io.ByteArrayOutputStream
import java.util.*

class ItemFragment : Fragment() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = super.defaultViewModelProviderFactory

    private var editTitle: EditText? = null
    private var editPlace: EditText? = null
    private var btnDate: Button? = null
    private var btnGetLocation: Button? = null
    private var btnShowMap: Button? = null
    private var btnTakePhoto: Button? = null
    private var btnDelete: Button? = null
    private var btnShare: Button? = null
    private var tvLocation: TextView? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var imageView: ImageView? = null
    private var photoBitmap: Bitmap? = null
    private var recordId: Long = 0
    private var binding: FragmentItemBinding? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var cameraFlag = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemBinding.inflate(inflater, container, false)
        editTitle = binding!!.root.findViewById(R.id.editTitle)
        editPlace = binding!!.root.findViewById(R.id.editPlace)
        btnDate = binding!!.root.findViewById(R.id.btnDate)
        btnGetLocation = binding!!.root.findViewById(R.id.btnGetLocation)
        btnShowMap = binding!!.root.findViewById(R.id.btnShowMap)
        tvLocation = binding!!.root.findViewById(R.id.tvLocation)
        imageView = binding!!.root.findViewById(R.id.imageView)

        // Date
        btnDate?.let { button ->
            button.setOnClickListener { showDatePickerDialog() }
        }

        // Map
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        btnGetLocation?.setOnClickListener { requestLocationPermission() }
        btnShowMap?.setOnClickListener { showMap() }

        // Picture
        photoBitmap = BitmapFactory.decodeResource(resources, R.drawable.default_avatar)
        imageView?.setImageBitmap(photoBitmap)
        imageView?.setOnClickListener { showPhotoZoomDialog() }
        btnTakePhoto = binding!!.root.findViewById(R.id.btnTakePhoto)
        btnTakePhoto?.setOnClickListener { takePicture() }

        // Share
        btnShare = binding!!.root.findViewById(R.id.btnShare)
        btnShare?.setOnClickListener { shareRecordSummary() }

        // Delete
        btnDelete = binding!!.root.findViewById(R.id.btnDelete)
        btnDelete?.visibility = View.GONE
        btnDelete?.setOnClickListener { view: View? -> deleteRecord(recordId) }

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            recordId = arguments!!.getLong("recordId")
            loadRecordDetails(recordId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            photoBitmap = data!!.extras!!["data"] as Bitmap?
            imageView!!.setImageBitmap(photoBitmap)
            cameraFlag = true
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        if (!cameraFlag) {
            saveRecordDetails()
        } else {
            cameraFlag = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun loadRecordDetails(recordId: Long) {
        try {
            val record = AppDatabase.getInstance(requireContext())?.plantRecordDao()
                ?.getPlantRecordById(recordId)

            record?.let {
                binding?.editTitle?.setText(it.title)
                binding?.editPlace?.setText(it.place)
                binding?.btnDate?.text = it.date
                photoBitmap = BitmapFactory.decodeByteArray(it.image, 0, it.image.size)
                binding?.imageView?.setImageBitmap(photoBitmap)
                updateLocation(it.latitude, it.longitude)
                btnDelete?.visibility = View.VISIBLE
                btnDelete?.setOnClickListener { view: View? -> deleteRecord(recordId) }
            }
        } catch (e: Exception) {
            // Handle the exception or log it if needed
        }
    }


    private fun showDatePickerDialog() {
        val dateSetListener =
            OnDateSetListener { datePicker: DatePicker?, year: Int, month: Int, day: Int ->
                val btnDate = binding!!.root.findViewById<Button>(R.id.btnDate)
                btnDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            }
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog =
            DatePickerDialog(requireActivity(), dateSetListener, year, month, day)
        datePickerDialog.show()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient!!.lastLocation
                .addOnSuccessListener { location: Location ->
                    updateLocation(
                        location.latitude,
                        location.longitude
                    )
                }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
        tvLocation!!.text = "GPS Location: $latitude, $longitude"
    }

    private fun showMap() {
        if (latitude != 0.0 && longitude != 0.0) {
            val mapUri = "geo:$latitude,$longitude?q=$latitude,$longitude"
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        } else {
            Toast.makeText(requireContext(), "No GPS location data available", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun takePicture() {
        if (hasCameraPermission()) {
            openCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    fun deleteRecord(recordId: Long) {
        try {
            val plantRecordDao = AppDatabase.getInstance(requireContext())?.plantRecordDao()
            plantRecordDao?.delete(plantRecordDao?.getPlantRecordById(recordId))
            val mainActivity = requireActivity() as MainActivity
            mainActivity.switchToListFragment()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Deletion Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPhotoZoomDialog() {
        val photoBitmap = (imageView!!.drawable as BitmapDrawable).bitmap
        val fragmentManager = requireActivity().supportFragmentManager
        val photoZoomDialogFragment: PhotoZoomDialogFragment =
            PhotoZoomDialogFragment.Companion.newInstance(photoBitmap)
        photoZoomDialogFragment.show(fragmentManager, "photo_zoom_dialog")
    }

    private fun shareRecordSummary() {
        val title = editTitle!!.text.toString()
        val place = editPlace!!.text.toString()
        val date = btnDate!!.text.toString()
        val location = tvLocation!!.text.toString()
        val summary = "Title: $title\nPlace: $place\nDate: $date\nLocation: $location"
        val imageUri = getImageUri(photoBitmap)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, summary)
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.type = "image/*"
        startActivity(Intent.createChooser(shareIntent, "Share Record Summary"))
    }

    private fun getImageUri(bitmap: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            bitmap,
            "Image Description",
            null
        )
        return Uri.parse(path)
    }

    private fun saveRecordDetails() {
        val title = editTitle!!.text.toString()
        val place = editPlace!!.text.toString()
        val date = btnDate!!.text.toString()
        if (!title.isEmpty() && recordId == -1L) {
            val stream = ByteArrayOutputStream()
            photoBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageByteArray = stream.toByteArray()
            val newRecord = PlantRecord()
            newRecord.title = title
            newRecord.date = date
            newRecord.place = place
            newRecord.latitude = latitude
            newRecord.longitude = longitude
            newRecord.image = imageByteArray
            AppDatabase.getInstance(requireContext())?.plantRecordDao()?.insert(newRecord)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_CODE = 101
        @kotlin.jvm.JvmStatic
        fun newInstance(recordId: Long): ItemFragment {
            val fragment = ItemFragment()
            val args = Bundle()
            args.putLong("recordId", recordId)
            fragment.arguments = args
            return fragment
        }
    }
}