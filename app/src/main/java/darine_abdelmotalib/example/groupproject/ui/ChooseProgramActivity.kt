package darine_abdelmotalib.example.groupproject.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.databinding.ActivityChooseProgramBinding
import darine_abdelmotalib.example.groupproject.ui.adapter.ProgramItem
import darine_abdelmotalib.example.groupproject.ui.adapter.ProgramListAdapter
import darine_abdelmotalib.example.groupproject.utils.ToolbarUtils


class ChooseProgramActivity : ComponentActivity() {

    private lateinit var binding: ActivityChooseProgramBinding
    private val adapter by lazy { ProgramListAdapter(::onProgramClicked) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseProgramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ToolbarUtils.setupToolbar(this, binding.includeToolbar.topAppBar, R.string.title_program_requirements)



        binding.programList.layoutManager = LinearLayoutManager(this)
        binding.programList.adapter = adapter
        binding.programList.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        adapter.submitList(
            listOf(ProgramItem("cmpt-bsc", "Computing Science", "Bachelors of Science"))
        )
    }

    private fun onProgramClicked(item: ProgramItem) {
        startActivity(
            android.content.Intent(
                this,
                ProgramRequirementsActivity::class.java
            )
        )
    }

}
