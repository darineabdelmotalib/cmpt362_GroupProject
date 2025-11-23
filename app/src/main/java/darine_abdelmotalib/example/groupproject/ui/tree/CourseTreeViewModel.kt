package darine_abdelmotalib.example.groupproject.ui.tree

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.bandb.graphview.graph.Graph

class CourseTreeViewModel : ViewModel() {
    val graph = MutableLiveData<Graph>()
}