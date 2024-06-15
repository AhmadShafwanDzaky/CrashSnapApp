package com.capstone.crashsnap.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.crashsnap.R
import com.capstone.crashsnap.SectionsPageAdapter
import com.capstone.crashsnap.ViewModelFactory
import com.capstone.crashsnap.data.NetResult
import com.capstone.crashsnap.data.remote.response.DataItem
import com.capstone.crashsnap.data.remote.response.HistoryResponse
import com.capstone.crashsnap.databinding.ActivityMainBinding
import com.capstone.crashsnap.getImageUri
import com.capstone.crashsnap.ui.auth.LoginActivity
import com.capstone.crashsnap.ui.history.historylist.HistoryListActivity
import com.capstone.crashsnap.ui.history.historylist.HistoryListActivity.Companion.EXTRA_TOKEN_HISTORYLIST
import com.capstone.crashsnap.ui.maps.MapsActivity
import com.capstone.crashsnap.ui.preview.PreviewActivity
import com.capstone.crashsnap.ui.preview.PreviewActivity.Companion.EXTRA_IMAGE_STRING
import com.capstone.crashsnap.ui.profile.ProfileActivity
import com.capstone.crashsnap.ui.tips.TipsActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentImageUri: Uri
    private lateinit var name: String
    private var oldResult: NetResult<HistoryResponse>? = null
    private var oldData: List<DataItem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            name = user.name
            binding.tvName.text = name
        }


        val layoutManager = LinearLayoutManager(this)
        binding.rvHistory.layoutManager = layoutManager
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.tvSeeAllHistory.setOnClickListener {
            val moveToHistory = Intent(this, HistoryListActivity::class.java)
            viewModel.getSession().observe(this) {
                moveToHistory.putExtra(EXTRA_TOKEN_HISTORYLIST, it.token)
            }
            startActivity(moveToHistory)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    val moveToProfile = Intent(this, ProfileActivity::class.java)
                    startActivity(moveToProfile)
                    true
                }

                else -> false
            }
        }
        binding.fab.setOnClickListener {
            showBottomSheet()
        }

        binding.repairShopButton.setOnClickListener {
            val moveToMaps = Intent(this, MapsActivity::class.java)
            startActivity(moveToMaps)
        }

        binding.expertTipsButton.setOnClickListener {
            val moveToTips = Intent(this, TipsActivity::class.java)
            startActivity(moveToTips)
        }

        viewModel.getSession().observe(this){
            getHistory()
        }

        binding.layoutRefresh.setColorSchemeResources(R.color.redmaroon_normal)

        binding.layoutRefresh.setOnRefreshListener {
            getHistory()
        }


    }

    private fun getHistory() {
        val extraToken = intent.getStringExtra(EXTRA_TOKEN).toString()
        viewModel.getSession().observe(this) { user ->
            var token = user.token
            if (token == null) {
                token = extraToken
            }
            viewModel.getHistory(token).observe(this) { result ->
                if (result == oldResult) {
                    return@observe
                }
                oldResult = result
                when (result) {
                    is NetResult.Success -> {
                        binding.layoutRefresh.isRefreshing = false
                        if (result.data.data == oldData) {
                            return@observe
                        }
                        oldData = result.data.data
                        if (result.data.data.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.tvEmpty2.visibility = View.VISIBLE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvEmpty2.visibility = View.GONE
                        }
                        val adapter = SectionsPageAdapter()
                        adapter.submitList(result.data.data)
                        binding.rvHistory.adapter = adapter

                    }

                    is NetResult.Error -> {
                        binding.layoutRefresh.isRefreshing = false
                        if (result.error.isNotEmpty()){
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.tvEmpty2.visibility = View.VISIBLE
                        }else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvEmpty2.visibility = View.GONE
                        }
                        val adapter = SectionsPageAdapter()
                        binding.rvHistory.adapter = adapter
                        val errorMessage: String = result.error
                        showToast(errorMessage)
                    }

                    is NetResult.Loading -> {
                        binding.layoutRefresh.isRefreshing = true
                    }

                    else -> {}
                }
            }
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_STRING, uri.toString())
            startActivity(intent)
        } else {
            showToast(getString(R.string.no_media))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_STRING, currentImageUri.toString())
            startActivity(intent)
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(getString(R.string.permission_success))
            } else {
                showToast(getString(R.string.permission_err))
            }
        }


    private fun showBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet, null)
        val dialog = BottomSheetDialog(this)
        val galleryGroup = dialogView.findViewById<View>(R.id.galleryGroup)
        val cameraGroup = dialogView.findViewById<View>(R.id.cameraGroup)
        galleryGroup.setOnClickListener {
            startGallery()
            dialog.dismiss()
        }
        cameraGroup.setOnClickListener {
            startCamera()
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_TOKEN = "extra_token"
    }
}