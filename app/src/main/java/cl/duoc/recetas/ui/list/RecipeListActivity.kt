package cl.duoc.recetas.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import cl.duoc.recetas.R
import cl.duoc.recetas.data.RecetasRepository
import cl.duoc.recetas.data.local.AppDatabase
import cl.duoc.recetas.data.local.LocalStorage
import cl.duoc.recetas.data.local.SessionManager
import cl.duoc.recetas.data.remote.RecetasApiClient
import cl.duoc.recetas.databinding.ActivityRecipeListBinding
import cl.duoc.recetas.ui.admin.AdminUsersActivity
import cl.duoc.recetas.ui.auth.LoginActivity
import cl.duoc.recetas.ui.auth.ProfileActivity
import cl.duoc.recetas.ui.create.CreateRecipeActivity
import cl.duoc.recetas.ui.detail.RecipeDetailActivity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class RecipeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeListBinding
    private lateinit var sessionManager: SessionManager
    private var isAdmin = false

    private val viewModel: RecipeListViewModel by viewModels {
        val repo = RecetasRepository(RecetasApiClient.service, LocalStorage(applicationContext))
        RecipeListViewModelFactory(repo)
    }

    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)

        // Toolbar
        setSupportActionBar(binding.toolbar)

        adapter = RecipeAdapter(emptyList(),
            onClick = { recipe ->
                startActivity(Intent(this, RecipeDetailActivity::class.java).apply {
                    putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id)
                })
            },
            onFavorite = { recipe -> viewModel.toggleFavorite(recipe) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadRecetas() }

        binding.fabCreate.setOnClickListener {
            startActivity(Intent(this, CreateRecipeActivity::class.java))
        }

        checkUserRole()
        observeUi()
    }

    private fun checkUserRole() {
        lifecycleScope.launch {
            val role = sessionManager.userRole.firstOrNull()
            isAdmin = role == "admin"
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.action_admin)?.isVisible = isAdmin
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_admin -> {
                startActivity(Intent(this, AdminUsersActivity::class.java))
                true
            }
            R.id.action_logout -> {
                lifecycleScope.launch {
                    sessionManager.clearSession()
                    startActivity(Intent(this@RecipeListActivity, LoginActivity::class.java))
                    finishAffinity()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.swipeRefresh.isRefreshing = state.isLoading
                    binding.tvError.text = state.error ?: ""
                    binding.tvError.visibility = if (state.error != null) android.view.View.VISIBLE else android.view.View.GONE
                    adapter.update(state.data)
                }
            }
        }
    }
}
