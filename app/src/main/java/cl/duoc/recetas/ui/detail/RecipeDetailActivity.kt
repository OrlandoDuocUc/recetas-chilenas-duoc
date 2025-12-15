package cl.duoc.recetas.ui.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cl.duoc.recetas.data.RecetasRepository
import cl.duoc.recetas.data.local.LocalStorage
import cl.duoc.recetas.data.remote.RecetasApiClient
import cl.duoc.recetas.databinding.ActivityRecipeDetailBinding
import kotlinx.coroutines.launch

class RecipeDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_RECIPE_ID = "extra_recipe_id"
    }

    private lateinit var binding: ActivityRecipeDetailBinding

    private val viewModel: RecipeDetailViewModel by viewModels {
        val repo = RecetasRepository(RecetasApiClient.service, LocalStorage(applicationContext))
        val id = intent.getIntExtra(EXTRA_RECIPE_ID, -1)
        RecipeDetailViewModelFactory(repo, id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFavorite.setOnClickListener { viewModel.toggleFavorite() }
        binding.btnRetry.setOnClickListener { viewModel.load() }

        observeUi()
    }

    private fun observeUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) android.view.View.VISIBLE else android.view.View.GONE
                    binding.tvError.text = state.error ?: ""
                    binding.tvError.visibility = if (state.error != null) android.view.View.VISIBLE else android.view.View.GONE

                    state.data?.let { recipe ->
                        binding.tvName.text = recipe.name
                        binding.tvRegion.text = recipe.region ?: "Región desconocida"
                        binding.tvDifficulty.text = "Dificultad: ${recipe.difficulty ?: "-"}"
                        binding.tvPrep.text = "Tiempo: ${recipe.prepTimeMinutes ?: "-"} min"
                        binding.tvDescription.text = recipe.description

                        val ingText = if (recipe.ingredients.isEmpty()) {
                            "Sin ingredientes"
                        } else {
                            recipe.ingredients.sortedBy { it.order ?: Int.MAX_VALUE }
                                .joinToString(separator = "\n") { ing ->
                                    "- ${ing.name} ${ing.quantity ?: ""} ${ing.unit ?: ""}".trim()
                                }
                        }
                        binding.tvIngredients.text = ingText

                        binding.btnFavorite.text = if (recipe.isFavorite) "Quitar de favoritos" else "Marcar favorito"
                    }
                }
            }
        }
    }
}
