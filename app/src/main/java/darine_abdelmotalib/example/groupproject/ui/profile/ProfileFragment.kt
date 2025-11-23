package darine_abdelmotalib.example.groupproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentProfileBinding
import darine_abdelmotalib.example.groupproject.data.db.CsRequirementsDb
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* -- DEBUG BUTTONS -- */
        binding.debugForward.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCourseProgressBar()
    }

    private fun setupCourseProgressBar() {
        val allUniqueCourses = CsRequirementsDb.getAllUniqueCourses()
        val totalXp = allUniqueCourses.size

        val completedXp = UserProgressDb.getCompletedCount()

        val progressPercentage = if (totalXp > 0) {
            ((completedXp.toDouble() / totalXp.toDouble()) * 100).toInt()
        } else {
            0
        }

        binding.profileProgressBar.max = 100
        binding.profileProgressBar.progress = progressPercentage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}