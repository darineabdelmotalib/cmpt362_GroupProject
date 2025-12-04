package darine_abdelmotalib.example.groupproject.ui.planning.semester

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import darine_abdelmotalib.example.groupproject.data.api.SfuCalendarApi
import darine_abdelmotalib.example.groupproject.data.api.SemesterDates
import darine_abdelmotalib.example.groupproject.data.db.UserProgressDb
import darine_abdelmotalib.example.groupproject.databinding.DialogSemesterTimelineBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SemesterTimelineDialogFragment : DialogFragment() {

    private var _binding: DialogSemesterTimelineBinding? = null
    private val binding get() = _binding!!

    private var semesterKey: String = ""

    companion object {
        private const val ARG_SEMESTER_KEY = "semester_key"

        fun newInstance(semesterKey: String): SemesterTimelineDialogFragment {
            val fragment = SemesterTimelineDialogFragment()
            val args = Bundle()
            args.putString(ARG_SEMESTER_KEY, semesterKey)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semesterKey = arguments?.getString(ARG_SEMESTER_KEY) ?: ""
    }

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
        _binding = DialogSemesterTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Parse semester key to get term and year
        val termPair = UserProgressDb.unKey(semesterKey)
        val term = termPair.first.replaceFirstChar { it.uppercase() }
        val year = termPair.second.toIntOrNull() ?: 2025

        // Set title
        binding.dialogTitle.text = "$term $year Important Dates"
        binding.dialogSubtitle.text = "Key dates and deadlines for this semester"

        // Show loading
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.timelineContainer.visibility = View.GONE

        // Fetch dates
        fetchSemesterDates(termPair.first, year)

        // Close button
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
    }

    private fun fetchSemesterDates(term: String, year: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val dates = withContext(Dispatchers.IO) {
                    SfuCalendarApi.fetchSemesterDates(term, year)
                }
                displayDates(dates)
            } catch (e: Exception) {
                // Use estimated dates as fallback
                val dates = SfuCalendarApi.getEstimatedDates(term, year)
                displayDates(dates)
            }
        }
    }

    private fun displayDates(dates: SemesterDates) {
        binding.loadingIndicator.visibility = View.GONE
        binding.timelineContainer.visibility = View.VISIBLE

        binding.dateEnrollmentStart.text = dates.enrollmentStart
        binding.dateClassesBegin.text = dates.classesBegin
        binding.dateLastAdd.text = dates.lastDayToAdd
        binding.dateDropNoW.text = dates.lastDayToDropNoW
        binding.dateDropWithW.text = dates.lastDayToDropWithW
        binding.dateClassesEnd.text = dates.classesEnd
        binding.dateExamPeriod.text = dates.examPeriod
    }

    override fun onStart() {
        super.onStart()
        // Make dialog wider
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

