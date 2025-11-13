package darine_abdelmotalib.example.groupproject.ui.requirements
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.FragmentChooseProgramBinding

class ChooseProgramFragment : Fragment() {
    private var _binding: FragmentChooseProgramBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChooseProgramBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* -- DEBUG BUTTONS -- */
        binding.debugForward.setOnClickListener {
            findNavController().navigate(R.id.action_chooseProgramFragment_to_programRequirementsFragment)
        }
        /* -- END DEBUG BUTTONS -- */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}