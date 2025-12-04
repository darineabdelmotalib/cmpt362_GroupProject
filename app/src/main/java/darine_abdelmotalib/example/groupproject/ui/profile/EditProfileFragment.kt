package darine_abdelmotalib.example.groupproject.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    // ViewModel to handle data updates
    private val viewModel: UserProfileViewModel by activityViewModels()

    private var currentAvatarUri: String? = null

    private enum class FieldType { MAJOR_1, MAJOR_2, MINOR }
    private var activeField = FieldType.MAJOR_1

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) { e.printStackTrace() }

            currentAvatarUri = it.toString()
            binding.profileCharacter.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()

        //Load Data
        binding.userNameInput.setText(UserProfilePrefs.getName(context))
        binding.inputChangeMajor.setText(UserProfilePrefs.getMajor(context))

        //Handle "None" or Empty for Major 2
        val savedMajor2 = UserProfilePrefs.getMajor2(context)
        if (savedMajor2.isEmpty() || savedMajor2 == "None") {
            binding.inputAddMajor.setText("") // Show empty/hint if none
        } else {
            binding.inputAddMajor.setText(savedMajor2)
        }

        binding.inputAddMinor.setText(UserProfilePrefs.getMinor(context))

        //Load Image
        val savedUri = UserProfilePrefs.getAvatarUri(context)
        if (savedUri != null) {
            currentAvatarUri = savedUri
            try {
                binding.profileCharacter.setImageURI(Uri.parse(savedUri))
            } catch (e: Exception) {
                binding.profileCharacter.setImageResource(R.drawable.default_pfp)
            }
        } else {
            binding.profileCharacter.setImageResource(R.drawable.default_pfp)
        }

        binding.buttonChangeCharacter.setOnClickListener { pickImage.launch("image/*") }

        //Navigation Helper
        val launchSearch = { field: FieldType ->
            activeField = field

            val major1 = binding.inputChangeMajor.text.toString()
            val major2 = binding.inputAddMajor.text.toString()

            val exclude = when(field) {
                FieldType.MAJOR_2 -> major1
                FieldType.MAJOR_1 -> major2
                else -> null
            }

            //Major 1 CANNOT be None. Major 2 and Minor CAN be None.
            val allowNone = (field != FieldType.MAJOR_1)

            val bundle = bundleOf(
                "exclude_program" to exclude,
                "allow_none" to allowNone //
            )

            findNavController().navigate(R.id.action_editProfileFragment_to_searchForProgramFragment, bundle)
        }

        binding.buttonChangeMajor.setOnClickListener { launchSearch(FieldType.MAJOR_1) }
        binding.inputChangeMajor.setOnClickListener { launchSearch(FieldType.MAJOR_1) }

        binding.buttonAddMajor.setOnClickListener { launchSearch(FieldType.MAJOR_2) }
        binding.inputAddMajor.setOnClickListener { launchSearch(FieldType.MAJOR_2) }

        binding.buttonAddMinor.setOnClickListener { launchSearch(FieldType.MINOR) }
        binding.inputAddMinor.setOnClickListener { launchSearch(FieldType.MINOR) }

        setFragmentResultListener("requestKey") { _, bundle ->
            val result = bundle.getString("selectedProgram")
            when (activeField) {
                FieldType.MAJOR_1 -> binding.inputChangeMajor.setText(result)
                FieldType.MAJOR_2 -> binding.inputAddMajor.setText(result)
                FieldType.MINOR -> binding.inputAddMinor.setText(result)
            }
        }

        binding.buttonSaveChanges.setOnClickListener {
            val newName = binding.userNameInput.text.toString()
            val newMajor = binding.inputChangeMajor.text.toString()

            var newMajor2 = binding.inputAddMajor.text.toString()
            if (newMajor2 == "None") {
                newMajor2 = ""
            }

            var newMinor = binding.inputAddMinor.text.toString()
            if (newMinor == "None") {
                newMinor = ""
            }

            // Update ViewModel
            viewModel.updateProfile(newName, newMajor, newMajor2, newMinor, currentAvatarUri)

            Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.buttonCancel.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}