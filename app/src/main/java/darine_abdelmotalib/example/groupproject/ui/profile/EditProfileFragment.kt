package darine_abdelmotalib.example.groupproject.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* -- DEBUG BUTTONS -- */
        binding.debugForward.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_searchForProgramFragment)
        }
        binding.debugBack.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userNameInput.setText(viewModel.name.value)
        binding.inputChangeMajor.setText(viewModel.major.value)
        binding.inputAddMinor.setText(viewModel.minor.value)

        binding.buttonChangeMajor.setOnClickListener {
            navigateToSearchForProgram("major")
        }

        binding.buttonAddMajor.setOnClickListener {
            navigateToSearchForProgram("major")
        }

        binding.buttonAddMinor.setOnClickListener {
            navigateToSearchForProgram("minor")
        }

        binding.buttonSaveChanges.setOnClickListener {
            val newName = binding.userNameInput.text.toString()
            val newMajor = binding.inputChangeMajor.text.toString()
            val newMinor = binding.inputAddMinor.text.toString()

            viewModel.updateProfile(newName, newMajor, newMinor)

            Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()

            // Go back to Profile Page
            findNavController().popBackStack()
        }

        // 3. Handle "Cancel"
        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun navigateToSearchForProgram(type: String) {
        val bundle = bundleOf("search_type" to type)
        findNavController().navigate(
            R.id.action_editProfileFragment_to_searchForProgramFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}