package com.example.datateman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.TextUtils.isEmpty
import android.view.View
import android.widget.Toast
import com.example.datateman.databinding.ActivityUpdateDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class UpdateData : AppCompatActivity() {


    private var database: DatabaseReference? = null
    private var auth: FirebaseAuth? = null
    private var cekNama: String? = null
    private var cekAlamat: String? = null
    private var cekNoHP: String? = null
    private lateinit var binding: ActivityUpdateDataBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Update Data"


        //mendapatkan instance autentikasi dan referensi dari database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        data //memanggil method data
        binding.update.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                //mendapatkan data teman yang akan di cek
                cekNama = binding.newNama.getText().toString()
                cekAlamat = binding.newAlamat.getText().toString()
                cekNoHP = binding.newNoHp.getText().toString()


                //mengecek agar tidak ada data yang kosong
                if (isEmpty(cekNama!!) || isEmpty(cekAlamat!!) || isEmpty(cekNoHP!!)) {
                    Toast.makeText(this@UpdateData, "data tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //menjalankan update data
                    val setTeman = data_teman()
                    setTeman.nama = binding.newNama.getText().toString()
                    setTeman.alamat = binding.newAlamat.getText().toString()
                    setTeman.no_hp = binding.newNoHp.getText().toString()
                    updateTeman(setTeman)
                }
            }
        })
    }


    //mengecek apakah ada data kosong, sebelum update
    private fun isEmpty(s: String): Boolean {
        return TextUtils.isEmpty(s)
    }


    //menampilkan data yang akan di update
    private val data: Unit
        private get() {
            //menampilkan data dari item yang sudah di pilih
            val getNama = intent.extras!!.getString("dataNama")
            val getAlamat = intent.extras!!.getString("dataAlamat")
            val getNoHP = intent.extras!!.getString("dataNoHP")
            binding.newNama!!.setText(getNama)
            binding.newAlamat!!.setText(getAlamat)
            binding.newNoHp!!.setText(getNoHP)
        }


    //proses update data yang sudah ditentukan
    private fun updateTeman(teman : data_teman) {
        val userID = auth!!.uid
        val getKey = intent.extras!!.getString("getPrimaryKey")
        database!!.child("Admin")
            .child(userID.toString())
            .child("DataTeman")
            .child(getKey!!)
            .setValue(teman)
            .addOnSuccessListener {
                binding.newNama!!.setText("")
                binding.newAlamat!!.setText("")
                binding.newNoHp!!.setText("")
                Toast.makeText(this@UpdateData, "Data berhasil diUbah",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
