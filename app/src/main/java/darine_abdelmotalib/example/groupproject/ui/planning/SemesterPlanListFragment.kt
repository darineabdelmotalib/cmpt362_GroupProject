package darine_abdelmotalib.example.groupproject.ui.planning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentSemesterPlanListBinding

class SemesterPlanListFragment : Fragment() {
    private var _binding: FragmentSemesterPlanListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSemesterPlanListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* -- DEBUG BUTTONS -- */
        binding.debugForwardCourseinfo.setOnClickListener {
            findNavController().navigate(R.id.action_semesterPlanListFragment_to_courseInfoPlanFragment)
        }
        binding.debugForwardScheduleview.setOnClickListener {
            findNavController().navigate(R.id.action_semesterPlanListFragment_to_scheduleViewFragment)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}