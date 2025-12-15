package cl.duoc.recetas.ui.admin

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cl.duoc.recetas.databinding.ActivityAdminUsersBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AdminUsersActivity : ComponentActivity() {

    private lateinit var binding: ActivityAdminUsersBinding
    private val viewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(application)
    }
    private lateinit var adapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = UsersAdapter(emptyList()) { user ->
            showDeleteConfirmation(user.id, user.name)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadUsers()
        }

        observeViewModel()
        viewModel.loadUsers()
    }

    private fun showDeleteConfirmation(userId: Int, userName: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar usuario")
            .setMessage("¿Estás seguro de eliminar a $userName?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteUser(userId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.swipeRefresh.isRefreshing = state.isLoading

                adapter.updateUsers(state.users)
                
                binding.tvEmpty.visibility = if (state.users.isEmpty() && !state.isLoading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                state.error?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                }

                if (state.deleteSuccess) {
                    Snackbar.make(binding.root, "Usuario eliminado", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}
