package darine_abdelmotalib.example.groupproject.ui.requirements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentCourseInfoBinding

class CourseInfoFragment : Fragment() {

    private var _binding: FragmentCourseInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCourseInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* -- DEBUG BUTTONS -- */
        binding.debugBack.setOnClickListener {
            findNavController().navigate(R.id.action_courseInfoFragment_to_programRequirementsFragment)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}