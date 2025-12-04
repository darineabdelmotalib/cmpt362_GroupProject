package darine_abdelmotalib.example.groupproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.R

class AvatarAdapter(
    private val avatars: List<Int>,
    private var selectedIndex: Int,
    private val onAvatarSelected: (Int) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    inner class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImage: ImageView = itemView.findViewById(R.id.avatar_image)
        val selectedIndicator: ImageView = itemView.findViewById(R.id.avatar_selected_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        holder.avatarImage.setImageResource(avatars[position])

        // Show selection state
        if (position == selectedIndex) {
            holder.avatarImage.setBackgroundResource(R.drawable.bg_avatar_item_selected)
            holder.selectedIndicator.visibility = View.VISIBLE
        } else {
            holder.avatarImage.setBackgroundResource(R.drawable.bg_avatar_item)
            holder.selectedIndicator.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val previousIndex = selectedIndex
            selectedIndex = holder.adapterPosition
            notifyItemChanged(previousIndex)
            notifyItemChanged(selectedIndex)
            onAvatarSelected(selectedIndex)
        }
    }

    override fun getItemCount(): Int = avatars.size
}

