package com.example.xpensate.Fragments.Profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.xpensate.API.home.UpdateContact.ProfileImage
import com.example.xpensate.API.home.UpdateUsernameResponse
import com.example.xpensate.AuthInstance
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.R
import com.example.xpensate.TokenDataStore
import com.example.xpensate.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class Profile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    binding.selectedImage.setImageURI(selectedImageUri)
                    uploadProfileImage(selectedImageUri)
                }
            }
        }
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            TokenDataStore.loadImageOnStart(requireContext())
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding?: return
        navController = findNavController()
        loadUserData()

                setupRealTimeNameUpdate()
            setupNavigation()
            setupImagePicker()
            lifecycleScope.launch {
                val savedUsername = TokenDataStore.getUsername(requireContext()).first()
                val savedProfileImage = TokenDataStore.getImage(requireContext()).first()
                if (!savedUsername.isNullOrBlank()) {
                    binding.userName.setText(savedUsername)
                    Log.d("ProfileFragment", "Loaded username: $savedUsername")
                }
                if (savedProfileImage != null) {
                    Glide.with(requireContext())
                        .load(savedProfileImage)
                        .into(binding.selectedImage)
                }
            }
            binding.userName.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    return@setOnKeyListener true
                }
                false
            }
            binding.card2.logo.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.currency_preference)
            binding.card2.textView.text = "Currency Preference"
            binding.card4.logo.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.applock)
            binding.card4.textView.text = "App Lock"
            binding.card3.logo.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.currency_nav)
            binding.card3.textView.text = "Exchanger"
            binding.card5.logo.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.export_icon)
            binding.card5.textView.text = "Export Expenses"
            binding.card6.logo.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.share)
            binding.card6.textView.text = "Share App"

            setupNavigation()
    }

    private fun setupNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.navigate(R.id.action_profile2_to_blankFragment)
                }
            })

        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                TokenDataStore.clearTokens(requireContext())
                Toast.makeText(requireContext(), "Logged out Successfully", Toast.LENGTH_SHORT)
                    .show()
                navController.navigate(R.id.action_profile2_to_login2)
            }
        }
        binding.card1.root.setOnClickListener {
            navController.navigate(R.id.action_profile2_to_updateContact)
        }
        binding.card2.root.setOnClickListener {
            navController.navigate(R.id.action_profile2_to_preferredCurrency)
        }
        binding.card3.root.setOnClickListener {
            navController.navigate(R.id.action_profile2_to_currencyConverter)
        }
        binding.card4.root.setOnClickListener{
            navController.navigate(R.id.action_profile2_to_appLock)
        }

    }

    private fun setupImagePicker() {
        binding.selectedImage.setOnClickListener { openImagePicker() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun setupRealTimeNameUpdate() {
        binding.userName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val newName = editable?.toString()?.trim()
                if (!newName.isNullOrEmpty()) {
                    updateUserName(newName)

                }
            }
        })
    }

    private fun loadUserData() {
        val sharedPreferences =
            requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val imageUriString = sharedPreferences.getString("profile_image", null)

        binding.userName.setText(username)

        if (!imageUriString.isNullOrEmpty()) {
            val imageUri = Uri.parse(imageUriString)
            try {
                val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.selectedImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserName(newName: String) {

        lifecycleScope.launch {
                try {
                    val savedUsername = TokenDataStore.getUsername(requireContext()).first()
                    if (newName != savedUsername) {
                        AuthInstance.api.updateName(newName)
                            .enqueue(object : Callback<UpdateUsernameResponse> {
                                override fun onResponse(
                                    call: Call<UpdateUsernameResponse>,
                                    response: Response<UpdateUsernameResponse>
                                ) {

                                    if (response.isSuccessful) {
                                        response.body()?.let {
                                            lifecycleScope.launch {
                                                TokenDataStore.saveUsername(
                                                    requireContext(),
                                                    newName
                                                )
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Name updated successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        if(response.code() == 500){
                                            Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                                        }
                                        val errorBody = response.message().toString()
                                        Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<UpdateUsernameResponse>,
                                    t: Throwable
                                ) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Network error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                } catch (e: Exception) {
                    ProgressDialogHelper.hideProgressDialog()
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "An unexpected error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun uploadProfileImage(imageUri: Uri) {
        try {
            val filePath = getPathFromUri(imageUri)
            if (filePath == null) {
                Toast.makeText(requireContext(), "Invalid image path", Toast.LENGTH_SHORT).show()
                return
            }

            val file = File(filePath)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            ProgressDialogHelper.showProgressDialog(requireContext())

            lifecycleScope.launch {
                try {
                    AuthInstance.api.uploadProfileImage(imagePart).enqueue(object : Callback<ProfileImage> {
                        override fun onResponse(
                            call: Call<ProfileImage>,
                            response: Response<ProfileImage>
                        ) {
                            ProgressDialogHelper.hideProgressDialog()
                            if (response.isSuccessful) {
                                response.body()?.let { profileImage ->
                                    val imageUrl = profileImage.data.profile_image.toString()
                                    if (imageUrl != null) {
                                        Glide.with(requireContext()).load(imageUrl)
                                            .into(binding.selectedImage)
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "Profile image updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to upload image.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<ProfileImage>, t: Throwable) {
                            ProgressDialogHelper.hideProgressDialog()
                            Toast.makeText(
                                requireContext(),
                                "Network error:",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "An unexpected error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: Exception) {
            ProgressDialogHelper.hideProgressDialog()

            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun saveImageUrl(imageUrl: Bitmap) {
        lifecycleScope.launch {
            TokenDataStore.saveImage(requireContext(), imageUrl)
        }
    }


    private fun getPathFromUri(uri: Uri): String? {
        var filePath: String? = null
        val contentResolver = requireContext().contentResolver

        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            filePath = uri.path
        }
        return filePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}