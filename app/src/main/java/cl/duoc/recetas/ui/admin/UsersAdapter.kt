package cl.duoc.recetas.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cl.duoc.recetas.data.local.entities.UserEntity
import cl.duoc.recetas.databinding.ItemUserBinding
import java.text.SimpleDateFormat
import java.util.*

class UsersAdapter(
    private var users: List<UserEntity>,
    private val onDelete: (UserEntity) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            tvName.text = user.name
            tvEmail.text = user.email
            
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvDate.text = "Registrado: ${dateFormat.format(Date(user.createdAt))}"
            
            btnDelete.setOnClickListener { onDelete(user) }
        }
    }

    fun updateUsers(newUsers: List<UserEntity>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
