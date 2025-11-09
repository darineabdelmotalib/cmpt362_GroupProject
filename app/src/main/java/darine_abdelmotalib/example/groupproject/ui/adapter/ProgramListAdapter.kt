package darine_abdelmotalib.example.groupproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.databinding.ItemProgramRowBinding

data class ProgramItem(
    val id: String,
    val title: String,
    val subtitle: String
)

class ProgramListAdapter(
    private val onClick: (ProgramItem) -> Unit
) : ListAdapter<ProgramItem, ProgramListAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemProgramRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemProgramRowBinding,
        private val onClick: (ProgramItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProgramItem) {
            binding.title.text = item.title
            binding.subtitle.text = item.subtitle
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ProgramItem>() {
            override fun areItemsTheSame(oldItem: ProgramItem, newItem: ProgramItem) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ProgramItem, newItem: ProgramItem) =
                oldItem == newItem
        }
    }
}
