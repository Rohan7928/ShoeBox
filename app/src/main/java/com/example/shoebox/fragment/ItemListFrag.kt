package com.example.shoebox.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shoebox.R
import com.example.shoebox.adapter.CartListAdapter
import com.example.shoebox.interfaces.OnCartItemClick
import com.example.shoebox.model.DataModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

/**
 * A simple [Fragment] subclass.
 * Use the [ItemListFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemListFrag : Fragment(), OnCartItemClick {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var recyclerViewList: RecyclerView? = null
    var cartListAdapter: CartListAdapter? = null
    var progressBar: ProgressBar? = null
    var txtNoItem: TextView? = null
    var onCartItemClick: OnCartItemClick? = null
    var db: FirebaseFirestore? = null
    var dataModels: ArrayList<DataModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        onCartItemClick = this
        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewList = view.findViewById(R.id.recyclerList)
        progressBar = view.findViewById(R.id.progrss_bar)
        txtNoItem = view.findViewById(R.id.txt_no_item)
        db = FirebaseFirestore.getInstance()
        dataModels = ArrayList()
        itemList
    }

    //for (DocumentSnapshot d : list) {
    private val itemList: Unit
        // Add all to your list
        //}
        private get() {
            dataModels!!.clear()
            if (isNetworkAvailable) {
                progressBar!!.visibility = View.VISIBLE
                db!!.collection("shoeList").document("category").collection("men").get()
                    .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                        if (!queryDocumentSnapshots.isEmpty) {
                            progressBar!!.visibility = View.GONE
                            //for (DocumentSnapshot d : list) {
                            val types = queryDocumentSnapshots.toObjects(
                                DataModel::class.java
                            )
                            // Add all to your list
                            dataModels!!.addAll(types)
                            Log.d("FireBaseFireStore", "onSuccess: " + dataModels!![0].image)
                            cartListAdapter = CartListAdapter(
                                activity,
                                dataModels,
                                "ItemsListFrag",
                                db!!,
                                onCartItemClick!!
                            )
                            recyclerViewList!!.layoutManager = LinearLayoutManager(context)
                            recyclerViewList!!.adapter = cartListAdapter
                            //}
                        } else {
                            progressBar!!.visibility = View.GONE
                            txtNoItem!!.visibility = View.VISIBLE
                            recyclerViewList!!.visibility = View.GONE
                        }
                    }.addOnFailureListener { e: Exception ->
                    progressBar!!.visibility = View.GONE
                    txtNoItem!!.visibility = View.VISIBLE
                    recyclerViewList!!.visibility = View.GONE
                    Toast.makeText(requireContext(), "" + e.message, Toast.LENGTH_SHORT).show()
                    Log.i("FireBaseFireStoreFailed", "" + e)
                }
            } else {
                txtNoItem!!.visibility = View.VISIBLE
                recyclerViewList!!.visibility = View.GONE
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    private val isNetworkAvailable: Boolean
        private get() {
            val connectivityManager =
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    override fun addItemValue(dataModel: DataModel?) {}
    override fun deleteItemValue(dataModel: DataModel?) {
        db!!.collection("shoeList").document("category").collection("men").document(dataModel?.name.toString())
            .delete().addOnSuccessListener { unused: Void? ->
            dataModels!!.remove(dataModel)
            cartListAdapter!!.notifyDataSetChanged()
            //getItemList();
            Toast.makeText(context, "Item Deleted Successfully", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e: Exception? ->
                Toast.makeText(
                    context,
                    "Sorry, Unable to process",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): ItemListFrag {
            val fragment = ItemListFrag()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}