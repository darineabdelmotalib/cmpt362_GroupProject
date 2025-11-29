package darine_abdelmotalib.example.groupproject.ui.planning.semester

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import darine_abdelmotalib.example.groupproject.R
import android.widget.Spinner

class AddSemDialogFragment(
    private val onConfirm: (String, String) -> Unit
) : DialogFragment() {

    private val seasonOptions = listOf("Spring", "Summer", "Fall")
    private val yearOptions = listOf("2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2024", "2025", "2026")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createAddSemDialog()
    }
    fun createAddSemDialog(): Dialog{
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater;
        val view = inflater.inflate(R.layout.dialog_add_semester, null)
        val seasonSpinner = view.findViewById<Spinner>(R.id.semSeasonSpinner)
        val yearSpinner = view.findViewById<Spinner>(R.id.semYearSpinner)
        seasonSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            seasonOptions)
        yearSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            yearOptions)

        builder.setView(view)
            .setTitle("Add a Semester")
            .setPositiveButton("Add") { _, _ ->
                val semSelection = seasonSpinner.selectedItem.toString()
                val yearSelection = yearSpinner.selectedItem.toString()
                onConfirm(semSelection, yearSelection)
            }
            .setNegativeButton("Cancel", null)

        return builder.create()

    }

}