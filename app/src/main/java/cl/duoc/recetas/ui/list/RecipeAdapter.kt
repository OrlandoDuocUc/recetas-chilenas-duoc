package cl.duoc.recetas.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cl.duoc.recetas.databinding.ItemRecipeBinding
import cl.duoc.recetas.model.Recipe

class RecipeAdapter(
    private var items: List<Recipe>,
    private val onClick: (Recipe) -> Unit,
    private val onFavorite: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeVH>() {

    inner class RecipeVH(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeVH {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeVH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecipeVH, position: Int) {
        val item = items[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvRegion.text = item.region ?: "Región desconocida"
        holder.binding.tvDifficulty.text = "Dificultad: ${item.difficulty ?: "-"}"
        holder.binding.tvFavorite.text = if (item.isFavorite) "? Favorito" else "? No favorito"

        holder.binding.btnFavorite.setOnClickListener { onFavorite(item) }
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    fun update(newItems: List<Recipe>) {
        items = newItems
        notifyDataSetChanged()
    }
}
