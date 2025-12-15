package cl.duoc.recetas.data.local

import android.content.Context
import androidx.core.content.edit

class LocalStorage(context: Context) {
    private val prefs = context.getSharedPreferences("recetas_prefs", Context.MODE_PRIVATE)

    fun getFavoriteIds(): Set<Int> =
        prefs.getStringSet("favorites", emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()

    fun setFavorite(id: Int, favorite: Boolean) {
        val current = getFavoriteIds().toMutableSet()
        if (favorite) current.add(id) else current.remove(id)
        prefs.edit { putStringSet("favorites", current.map { it.toString() }.toSet()) }
    }

    fun isFavorite(id: Int): Boolean = getFavoriteIds().contains(id)
}
