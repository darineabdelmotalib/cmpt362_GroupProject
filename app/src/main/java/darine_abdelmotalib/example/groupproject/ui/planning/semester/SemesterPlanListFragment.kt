package darine_abdelmotalib.example.groupproject.ui.planning.semester

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentSemesterPlanListBinding

class SemesterPlanListFragment : Fragment() {
    private var _binding: FragmentSemesterPlanListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SemesterPlanListViewModel
    private lateinit var appContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(SemesterPlanListViewModel::class.java)
        appContext = requireContext().applicationContext

        _binding = FragmentSemesterPlanListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        /* -- DEBUG BUTTONS -- */
        binding.debugForwardCourseinfo.setOnClickListener {
            findNavController().navigate(R.id.action_semesterPlanListFragment_to_courseInfoPlanFragment)
        }
        binding.debugForwardScheduleview.setOnClickListener {
            findNavController().navigate(R.id.action_semesterPlanListFragment_to_scheduleViewFragment)
        }

        /*Check logcat to see if sem adding/removing is successful*/
        binding.debugAddSem.setOnClickListener {
            viewModel.debugAddSem(appContext)
        }
        binding.debugAddCourse.setOnClickListener {
            viewModel.debugAddCourse(appContext)
        }
        binding.debugRemoveCourse.setOnClickListener {
            viewModel.debugRemoveCourse(appContext)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    /*In Fragment*/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        /*Inflates a separate layout*/
        inflater.inflate(R.menu.menu_semester_view, menu)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}