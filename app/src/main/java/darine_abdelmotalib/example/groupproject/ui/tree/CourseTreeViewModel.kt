package darine_abdelmotalib.example.groupproject.ui.tree

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.bandb.graphview.graph.Graph
import kotlin.String

class CourseTreeViewModel : ViewModel() {
    val graph = MutableLiveData<Graph>()
    val courseList = MutableLiveData<List<String>>()
    val prereqMap = MutableLiveData<Map<String, List<String>>>()
}