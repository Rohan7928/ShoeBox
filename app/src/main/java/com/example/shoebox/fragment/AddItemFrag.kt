package com.example.shoebox.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.shoebox.R
import com.example.shoebox.model.DataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * A simple [Fragment] subclass.
 * Use the [AddItemFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddItemFrag : Fragment() {
    private var imageView: CircleImageView? = null
    private var filePath: Uri? = null
    var storage: FirebaseStorage? = null
    private val PhotoURL: File? = null
    var mCurrentPhotoPath: String? = null
    var edtName: EditText? = null
    var edtPrice: EditText? = null
    var edtDesc: EditText? = null
    var edtId: EditText? = null
    var storageReference: StorageReference? = null
    var btnUpload: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    var progressbar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageView = view.findViewById(R.id.img_shoe)
        edtName = view.findViewById(R.id.et_shoe_name)
        edtPrice = view.findViewById(R.id.et_price)
        edtDesc = view.findViewById(R.id.et_desc)
        edtId = view.findViewById(R.id.et_shoe_id)
        btnUpload = view.findViewById(R.id.btn_upload)
        progressbar = view.findViewById(R.id.progressbar)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
        imageView?.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Select Picture"), CAMERA_REQUEST)
        }
        btnUpload?.setOnClickListener { view1: View? ->
            val name = edtName?.text.toString().trim { it <= ' ' }
            val price = edtPrice?.text.toString().trim { it <= ' ' }
            val desc = edtDesc?.text.toString().trim { it <= ' ' }
            val id = edtId?.text.toString().trim { it <= ' ' }
            if (name.isEmpty()) {
                edtName?.error = "Please enter name"
            } else if (price.isEmpty()) {
                edtPrice?.error = "Please enter price"
            } else if (desc.isEmpty()) {
                edtDesc?.error = "Please enter description"
            } else if (id.isEmpty()) {
                edtId?.error = "Please enter id"
            } else {
                progressbar?.setVisibility(View.VISIBLE)
                uploadImage(name, price, desc, id)
            }
        }
    }

    private fun uploadItem(name: String, price: String, desc: String, id: Int, image: String) {
        val dataModel = DataModel(name, "$$price", desc, image, id, 1)
        val dbCourses = db?.collection("shoeList")?.document("category")?.collection("men")
        dbCourses?.document(dataModel.name.toString())?.set(dataModel)
            ?.addOnCompleteListener {
                progressbar?.visibility = View.GONE
                clearRecord()
                Toast.makeText(context, "Added to Card", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener { e: Exception ->
                progressbar?.visibility = View.GONE
                Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearRecord() {
        edtId?.setText("")
        edtDesc?.setText("")
        edtPrice?.setText("")
        edtName?.setText("")
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.absolutePath
        println(mCurrentPhotoPath)
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                val selectedImageUri = data?.data
                if (null != selectedImageUri) {
                    val picturePath = savedFileImage(context, selectedImageUri)
                    Log.i("PicturePath", "" + picturePath)
                    filePath = selectedImageUri
                    imageView?.setImageURI(selectedImageUri)
                }
            }
        }
    }

    private fun uploadImage(name: String, price: String, desc: String, id: String) {
        if (filePath != null) {
            // Defining the child of storageReference
            val ref = storageReference?.child("images/" + UUID.randomUUID().toString())
            ref?.putFile(filePath!!)?.addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri: Uri ->
                    val downloadUrl = uri.toString()
                    uploadItem(name, price, desc, Integer.valueOf(id), downloadUrl)
                }
                Toast.makeText(context, "Image Uploaded?", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener { e: Exception ->
                // Error, Image not uploaded
                progressbar?.visibility = View.GONE
                Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }?.addOnProgressListener {
            }
        } else {
            val downloadUrl =
                "https://firebasestorage.googleapis.com/v0/b/shoefy-e7f00.appspot.com/o/five.png?alt=media&token=577fdb2d-8eeb-4cb0-8262-e2bd0f535126"
            uploadItem(name, price, desc, Integer.valueOf(id), downloadUrl)
        }
    }

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val CAMERA_REQUEST = 1888

        fun savedFileImage(context: Context?, uri: Uri?): String {
            val inputStream: InputStream?
            try {
                inputStream = context?.contentResolver?.openInputStream(uri!!)
                val fileName = "shoefy1.png"
                val folder = File(context?.filesDir.toString(), "shoefy")
                if (!folder.exists()) {
                    folder.mkdir()
                }
                val file = File(folder, fileName)
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var read: Int = 0
                while (inputStream?.read(buffer).also {
                        if (it != null) {
                            read = it
                        }
                    } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
                return file.absolutePath
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return ""
        }
    }
}