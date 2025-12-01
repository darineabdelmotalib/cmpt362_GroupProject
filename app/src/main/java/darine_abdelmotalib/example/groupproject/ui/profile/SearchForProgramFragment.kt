package darine_abdelmotalib.example.groupproject.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentSearchForProgramBinding

data class ProgramOption(val title: String, val degree: String)

class SearchForProgramFragment : Fragment() {

    private var _binding: FragmentSearchForProgramBinding? = null
    private val binding get() = _binding!!

    private val allPrograms = listOf(
        ProgramOption("None", "Clear Selection"),
        ProgramOption("Computing Science", "Bachelor of Science"),
        ProgramOption("Software Systems", "Bachelor of Science"),
        ProgramOption("Data Science", "Bachelor of Science"),
        ProgramOption("Mathematics", "Bachelor of Science"),
        ProgramOption("Business", "Bachelor of Business Administration"),
        ProgramOption("Engineering Science", "Bachelor of Applied Science"),
        ProgramOption("Criminology", "Bachelor of Arts"),
        ProgramOption("Psychology", "Bachelor of Arts"),
        ProgramOption("Communication", "Bachelor of Arts")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchForProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)

        val excludedTitle = arguments?.getString("exclude_program")
        val allowNone = arguments?.getBoolean("allow_none", true) ?: true

        //Filter the Master List
        val availablePrograms = allPrograms.filter { program ->
            val isNotExcluded = (program.title != excludedTitle)

            val isNoneAllowed = if (!allowNone) (program.title != "None") else true

            isNotExcluded && isNoneAllowed
        }

        val adapter = ProgramSearchAdapter(availablePrograms) { selected ->
            setFragmentResult("requestKey", bundleOf("selectedProgram" to selected.title))
            findNavController().popBackStack()
        }

        binding.programResults.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.programResultCount.text = "${availablePrograms.size} Results Found"

        //Search Logic
        fun performSearch(query: String) {
            val cleanQuery = query.trim().lowercase()

            val filteredList = if (cleanQuery.isEmpty()) {
                availablePrograms // Show the filtered base list
            } else {
                availablePrograms.filter { it.title.lowercase().contains(cleanQuery) }
            }

            adapter.updateList(filteredList)

            if (filteredList.isEmpty()) {
                binding.programResultCount.text = "No results found"
            } else {
                binding.programResultCount.text = "${filteredList.size} Results Found"
            }
        }

        binding.inputSearchProgram.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonSearchProgram.setOnClickListener {
            performSearch(binding.inputSearchProgram.text.toString())
        }

        binding.debugBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ProgramSearchAdapter(
        private var items: List<ProgramOption>,
        private val onClick: (ProgramOption) -> Unit
    ) : RecyclerView.Adapter<ProgramSearchAdapter.VH>() {

        fun updateList(newItems: List<ProgramOption>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_program_row, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.subtitle.text = item.degree
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.title)
            val subtitle: TextView = view.findViewById(R.id.subtitle)
        }
    }
}