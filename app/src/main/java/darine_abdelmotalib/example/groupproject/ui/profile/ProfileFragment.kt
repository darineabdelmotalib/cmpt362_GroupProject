package darine_abdelmotalib.example.groupproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentProfileBinding
import darine_abdelmotalib.example.groupproject.data.db.CsRequirementsDb
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by activityViewModels()

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

        binding.profileProgressBar.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_courseMazeFragment)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.name.observe(viewLifecycleOwner) { newName ->
            binding.profileName.text = newName
        }

        viewModel.major.observe(viewLifecycleOwner) { newMajor ->
            binding.profileMajor.text = newMajor
        }

        setupCourseProgressBar()
    }

    override fun onResume() {
        super.onResume()
        setupCourseProgressBar()
        loadProfileData()
    }

    private fun loadProfileData() {
        val context = requireContext()

        binding.profileName.text = UserProfilePrefs.getName(context)

        val major1 = UserProfilePrefs.getMajor(context)
        val major2 = UserProfilePrefs.getMajor2(context)

        if (major2.isNotEmpty() && !major2.equals("None", ignoreCase = true)) {
            binding.profileMajor.text = "$major1 & $major2"
        } else {
            binding.profileMajor.text = major1
        }

        val avatarUri = UserProfilePrefs.getAvatarUri(context)
        if (avatarUri != null) {
            try {
                binding.profileCharacter.setImageURI(android.net.Uri.parse(avatarUri))
            } catch (e: Exception) {
                binding.profileCharacter.setImageResource(R.drawable.default_pfp)
            }
        } else {
            binding.profileCharacter.setImageResource(R.drawable.default_pfp)
        }
    }

    private fun setupCourseProgressBar() {
        val allUniqueCourses = CsRequirementsDb.getAllUniqueCourses()
        val totalXp = allUniqueCourses.size

        val completedXp = UserProgressDb.getCompletedCount(requireContext())

        val progressPercentage = if (totalXp > 0) {
            ((completedXp.toDouble() / totalXp.toDouble()) * 100).toInt()
        } else {
            0
        }

        binding.profileProgressBar.max = 100
        binding.profileProgressBar.progress = progressPercentage
        binding.textProgressPercentage.text = "$progressPercentage% Complete"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}