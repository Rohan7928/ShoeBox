package com.example.shoebox.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.shoebox.R
import com.example.shoebox.fragment.AddItemFrag
import com.example.shoebox.fragment.ItemListFrag
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDashBoard : AppCompatActivity() {
    var bottomNavigation: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        setCurrentFragment(AddItemFrag())
        bottomNavigation = findViewById(R.id.bottomNavigationView)
        bottomNavigation?.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }

    var navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.add_item -> {
                    openFragment(AddItemFrag())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.items_list -> {
                    openFragment(ItemListFrag())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun setCurrentFragment(firstFragment: AddItemFrag) {
        supportFragmentManager.beginTransaction().replace(R.id.flFragment, firstFragment).commit()
    }

    fun openFragment(fragment: Fragment?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flFragment, fragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this).create()
        alert.setTitle("Exit")
        alert.setMessage("Are you sure you want to exit?")
        alert.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        alert.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)
        alert.setButton(
            DialogInterface.BUTTON_POSITIVE,
            "OK"
        ) { _: DialogInterface?, _: Int -> finishAffinity() }
        alert.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            "Cancel"
        ) { _: DialogInterface?, _: Int -> alert.dismiss() }
        alert.show()
    }
}