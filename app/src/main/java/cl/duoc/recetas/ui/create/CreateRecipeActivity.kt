package cl.duoc.recetas.ui.create

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cl.duoc.recetas.databinding.ActivityCreateRecipeBinding
import cl.duoc.recetas.model.Ingredient
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CreateRecipeActivity : ComponentActivity() {

    private lateinit var binding: ActivityCreateRecipeBinding
    private val viewModel: CreateRecipeViewModel by viewModels {
        CreateRecipeViewModelFactory(application)
    }
    
    private var selectedImageUri: Uri? = null
    private var capturedBitmap: Bitmap? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Snackbar.make(binding.root, "Se necesita permiso de cámara", Snackbar.LENGTH_SHORT).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            capturedBitmap = result.data?.extras?.get("data") as? Bitmap
            capturedBitmap?.let {
                binding.ivPreview.setImageBitmap(it)
                selectedImageUri = null
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let {
                binding.ivPreview.setImageURI(it)
                capturedBitmap = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnCamera.setOnClickListener {
            checkCameraPermission()
        }

        binding.btnGallery.setOnClickListener {
            launchGallery()
        }

        binding.btnSave.setOnClickListener {
            if (validateFields()) {
                val name = binding.etName.text.toString().trim()
                val region = binding.etRegion.text.toString().trim()
                val shortDesc = binding.etShortDesc.text.toString().trim()
                val description = binding.etDescription.text.toString().trim()
                val prepTime = binding.etPrepTime.text.toString().toIntOrNull()
                val difficulty = binding.etDifficulty.text.toString().trim()
                val ingredientsText = binding.etIngredients.text.toString().trim()
                
                val ingredients = parseIngredients(ingredientsText)
                
                viewModel.createRecipe(
                    name, region, shortDesc, description, 
                    prepTime, difficulty, ingredients,
                    capturedBitmap, selectedImageUri
                )
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun validateFields(): Boolean {
        var isValid = true

        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tilName.error = "El nombre es obligatorio"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        val description = binding.etDescription.text.toString().trim()
        if (description.isEmpty()) {
            binding.tilDescription.error = "La descripción es obligatoria"
            isValid = false
        } else {
            binding.tilDescription.error = null
        }

        val ingredients = binding.etIngredients.text.toString().trim()
        if (ingredients.isEmpty()) {
            binding.tilIngredients.error = "Los ingredientes son obligatorios"
            isValid = false
        } else {
            binding.tilIngredients.error = null
        }

        return isValid
    }

    private fun parseIngredients(text: String): List<Ingredient> {
        return text.lines()
            .filter { it.isNotBlank() }
            .mapIndexed { index, line ->
                val parts = line.trim().split(" ", limit = 3)
                when (parts.size) {
                    1 -> Ingredient(name = parts[0], order = index)
                    2 -> Ingredient(name = parts[1], quantity = parts[0].toDoubleOrNull(), order = index)
                    else -> Ingredient(
                        quantity = parts[0].toDoubleOrNull(),
                        unit = parts[1],
                        name = parts[2],
                        order = index
                    )
                }
            }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                binding.btnSave.isEnabled = !state.isLoading

                state.error?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                }

                if (state.success) {
                    Snackbar.make(binding.root, "¡Receta guardada!", Snackbar.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
