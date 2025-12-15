package cl.duoc.recetas.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import cl.duoc.recetas.data.AuthRepository
import cl.duoc.recetas.data.local.AppDatabase
import cl.duoc.recetas.data.local.SessionManager
import cl.duoc.recetas.databinding.ActivityProfileBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
        viewModel.loadProfile()
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()
            
            if (name.isEmpty()) {
                binding.tilName.error = "El nombre es obligatorio"
                return@setOnClickListener
            }
            
            if (newPassword.isNotEmpty() && newPassword.length < 6) {
                binding.tilNewPassword.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }
            
            viewModel.updateProfile(name, newPassword.ifEmpty { null })
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                state.user?.let { user ->
                    binding.etName.setText(user.name)
                    binding.tvEmail.text = user.email
                    binding.tvRole.text = when (user.role) {
                        "admin" -> "🛡️ Administrador"
                        else -> "👤 Usuario"
                    }
                }

                state.error?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                }

                if (state.success) {
                    Snackbar.make(binding.root, "Perfil actualizado correctamente", Snackbar.LENGTH_SHORT).show()
                    binding.etNewPassword.text?.clear()
                }
            }
        }
    }
}
