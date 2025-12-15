package cl.duoc.recetas.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import cl.duoc.recetas.databinding.ActivityLoginBinding
import cl.duoc.recetas.ui.list.RecipeListActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            if (validateFields()) {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(cl.duoc.recetas.R.anim.slide_in_bottom, cl.duoc.recetas.R.anim.fade_in)
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = "El email es obligatorio"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email inválido"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        val password = binding.etPassword.text.toString()
        if (password.isEmpty()) {
            binding.tilPassword.error = "La contraseña es obligatoria"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Mínimo 6 caracteres"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                binding.btnLogin.isEnabled = !state.isLoading

                if (state.error != null) {
                    binding.tvError.text = state.error
                    binding.tvError.visibility = View.VISIBLE
                    Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                } else {
                    binding.tvError.visibility = View.GONE
                }

                if (state.user != null) {
                    Snackbar.make(binding.root, "¡Bienvenido ${state.user.name}!", Snackbar.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, RecipeListActivity::class.java))
                    overridePendingTransition(cl.duoc.recetas.R.anim.fade_in, cl.duoc.recetas.R.anim.slide_out_bottom)
                    finish()
                }
            }
        }
    }
}
