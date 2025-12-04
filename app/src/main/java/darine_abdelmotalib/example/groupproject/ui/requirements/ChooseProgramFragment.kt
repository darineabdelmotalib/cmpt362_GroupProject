package darine_abdelmotalib.example.groupproject.ui.requirements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentChooseProgramBinding
import darine_abdelmotalib.example.groupproject.ui.adapter.ProgramItem
import darine_abdelmotalib.example.groupproject.ui.adapter.ProgramListAdapter

class ChooseProgramFragment : Fragment() {

    private var _binding: FragmentChooseProgramBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { ProgramListAdapter(::onProgramClicked) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseProgramBinding.inflate(inflater, container, false)
        val root = binding.root

        val toolbar = binding.includeToolbar.toolbar
        toolbar.title = getString(R.string.title_program_requirements)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.programList.layoutManager = LinearLayoutManager(requireContext())
        binding.programList.adapter = adapter
        binding.programList.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        adapter.submitList(
            listOf(
                ProgramItem(
                    id = "cmpt-bsc",
                    title = "Computing Science",
                    subtitle = "Bachelor of Science"
                )
            )
        )

        return root
    }

    private fun onProgramClicked(item: ProgramItem) {
        findNavController()
            .navigate(R.id.action_chooseProgramFragment_to_programRequirementsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
