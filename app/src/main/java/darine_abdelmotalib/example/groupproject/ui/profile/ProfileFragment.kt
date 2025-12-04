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
import darine_abdelmotalib.example.groupproject.data.prefs.ThemePrefs

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

        binding.profileProgressBar.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_courseMazeFragment)
        }

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
        setupDarkModeSwitch()
        setupAvatarSelection()
    }

    private fun setupAvatarSelection() {
        binding.buttonChangeAvatar.setOnClickListener {
            val dialog = AvatarSelectionDialogFragment { _ ->
                // Reload avatar when selection changes
                loadAvatar()
            }
            dialog.show(parentFragmentManager, "AvatarSelectionDialog")
        }
    }

    private fun loadAvatar() {
        val context = requireContext()
        val avatarUri = UserProfilePrefs.getAvatarUri(context)
        
        if (avatarUri != null) {
            // Custom avatar from URI
            try {
                binding.profileCharacter.setImageURI(android.net.Uri.parse(avatarUri))
            } catch (e: Exception) {
                binding.profileCharacter.setImageResource(UserProfilePrefs.getAvatarDrawable(context))
            }
        } else {
            // Use built-in avatar
            binding.profileCharacter.setImageResource(UserProfilePrefs.getAvatarDrawable(context))
        }
    }

    private fun setupDarkModeSwitch() {
        // Set initial state
        binding.switchDarkMode.isChecked = ThemePrefs.isDarkMode(requireContext())

        // Handle switch changes
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            ThemePrefs.setDarkMode(requireContext(), isChecked)
        }
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

        loadAvatar()
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