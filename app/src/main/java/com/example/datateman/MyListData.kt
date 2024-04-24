package com.example.datateman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.datateman.databinding.ActivityMyListDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MyListData : AppCompatActivity(), RecyclerViewAdapter.dataListener {


    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null


    //deklarasi variable database reference & arraylist dengan parameter class model
    val database = FirebaseDatabase.getInstance()
    private var dataTeman = ArrayList<data_teman>()
    private var auth: FirebaseAuth? = null


    private lateinit var binding: ActivityMyListDataBinding


    //kode untuk mengambil data dari database & menampilkan ke dalam adapter
    private fun GetData() {
        Toast.makeText(applicationContext, "Mohon Tunggu Sebentar...", Toast.LENGTH_LONG).show()
        val getUserID: String = auth?.getCurrentUser()?.getUid().toString()
        val getReference = database.getReference()
        getReference.child("Admin").child(getUserID).child("DataTeman")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataTeman.clear()
                        for (snapshot in dataSnapshot.children) {
                            //mappping data pada data snapshot ke dalam objek dataTeman
                            val teman = snapshot.getValue(data_teman::class.java)
                            //mengambil primary key untuk proses update /delete
                            teman?.key = snapshot.key
                            dataTeman.add(teman!!)
                        }


                        //inisialiasasi adapter dan data teman dalam bentuk array
                        adapter = RecyclerViewAdapter(dataTeman, this@MyListData)
                        //memasang adapter pada RecyclerView
                        recyclerView?.adapter = adapter
                        (adapter as RecyclerViewAdapter).notifyDataSetChanged()
                        Toast.makeText(applicationContext, "Data berhasil dimuat", Toast.LENGTH_LONG).show()
                    }
                }


                override fun onCancelled(databaseError: DatabaseError) {
                    //kode ini dijalankan ketika error, simpan ke logcat
                    Toast.makeText(applicationContext, "Data gagal dimuat", Toast.LENGTH_LONG).show()
                    Log.e("MyListActivity", databaseError.details + " " + databaseError.message)
                }
            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = findViewById(R.id.datalist)
        // Menghapus setSupportActionBar() karena tidak menggunakan ActionBar
        // setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar!!.title = "Data Teman"
        auth = FirebaseAuth.getInstance()
        MyRecyclerView()
        GetData()
    }


    //baris kode untuk mengatur recyclerview
    private fun MyRecyclerView() {
        layoutManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.setHasFixedSize(true)


        //buat garis bawah setiap item data
        val itemDecoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.line)!!)
        recyclerView?.addItemDecoration(itemDecoration)
    }


    override fun onDeleteData(dataTeman: data_teman?, position: Int) {
        val getUserID: String = auth?.getCurrentUser()?.getUid().toString()
        val getReference = database.getReference()
        if (getReference !=null) {
            getReference.child("Admin")
                .child(getUserID)
                .child("DataTeman")
                .child(dataTeman?.key.toString())
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this@MyListData, "Data Berhasil dihapus",
                        Toast.LENGTH_SHORT).show();
                }
        } else {
            Toast.makeText(this@MyListData, "Referance Kosong",
                Toast.LENGTH_SHORT).show()
        }
    }
}
