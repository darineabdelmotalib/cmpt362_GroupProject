package darine_abdelmotalib.example.groupproject.ui.requirements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentProgramRequirementsBinding

class ProgramRequirementsFragment : Fragment() {

    private var _binding: FragmentProgramRequirementsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProgramRequirementsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* -- DEBUG BUTTONS -- */
        binding.debugForward.setOnClickListener {
            findNavController().navigate(R.id.action_programRequirementsFragment_to_courseInfoFragment)
        }
        binding.debugBack.setOnClickListener {
            findNavController().navigate(R.id.action_programRequirementsFragment_to_chooseProgramFragment)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}