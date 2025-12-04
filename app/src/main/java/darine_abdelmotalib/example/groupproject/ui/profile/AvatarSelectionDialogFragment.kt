package darine_abdelmotalib.example.groupproject.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import darine_abdelmotalib.example.groupproject.databinding.DialogAvatarSelectionBinding
import darine_abdelmotalib.example.groupproject.ui.adapter.AvatarAdapter

class AvatarSelectionDialogFragment(
    private val onAvatarSelected: (Int) -> Unit
) : DialogFragment() {

    private var _binding: DialogAvatarSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAvatarSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentIndex = UserProfilePrefs.getAvatarIndex(requireContext())

        val adapter = AvatarAdapter(
            avatars = UserProfilePrefs.AVATARS,
            selectedIndex = currentIndex,
            onAvatarSelected = { index ->
                UserProfilePrefs.saveAvatarIndex(requireContext(), index)
                onAvatarSelected(index)
                dismiss()
            }
        )

        binding.avatarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.avatarRecyclerView.adapter = adapter

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

