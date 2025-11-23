package darine_abdelmotalib.example.groupproject.ui.tree

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import darine_abdelmotalib.example.groupproject.R
import darine_abdelmotalib.example.groupproject.data.api.SfuCourseApi
import darine_abdelmotalib.example.groupproject.data.db.CsRequirementsDb
import darine_abdelmotalib.example.groupproject.data.db.RequirementCourse
import darine_abdelmotalib.example.groupproject.data.db.RequirementGroup
import dev.bandb.graphview.AbstractGraphAdapter
import dev.bandb.graphview.decoration.edge.ArrowEdgeDecoration
import dev.bandb.graphview.graph.Graph
import dev.bandb.graphview.graph.Node
import dev.bandb.graphview.layouts.energy.FruchtermanReingoldLayoutManager
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.getValue

class CourseTreeFragment : Fragment() {
    private lateinit var graphRecycler: RecyclerView
    private lateinit var adapter: GraphAdapter
    private val viewModel: CourseTreeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_tree, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        graphRecycler = view.findViewById(R.id.recycler)
        setupGraphView()
    }

    private fun setupGraphView() {
        // set a layout manager that the RecyclerView will use
        graphRecycler.layoutManager = FruchtermanReingoldLayoutManager(
            requireActivity(),
            250
        ).apply {
            useMaxSize = true // nodes are all the same size; biggest node defines the size for all the other nodes
        }

        // attach item decorations to draw edges
        graphRecycler.addItemDecoration(ArrowEdgeDecoration())

        // adapter class extends from 'AbstractGraphAdapter'
        adapter = GraphAdapter()
        graphRecycler.adapter = adapter

        lifecycleScope.launch {
            // build graph
            if (viewModel.graph.value == null) {
                val graph = withContext(IO) {
                    buildGraph()
                }
                viewModel.graph.value = graph
            }

            viewModel.graph.value.let { graph ->
                adapter.submitGraph(graph)
            }
        }
    }

    private suspend fun buildGraph(): Graph {
        val graph = Graph()
        val courses = getAllCourses()

        // set up dictionary for created Node object to avoid creating multiples of a course
        val nodeMap = mutableMapOf<String, Node>()

        // add all courses
        for (course in courses) {
            val code = "${course.dept} ${course.number}".trim().uppercase()

            // get key (if exists) or put the new Node into the map
            val courseNode = nodeMap.getOrPut(code) {Node(code)}
            if (!graph.contains(courseNode)) {
                graph.addNode(courseNode)
            }

            val outline = try {
                withContext(IO) {
                    SfuCourseApi.fetchCourseOutline(course.dept, course.number)
                }
            } catch (e: Exception) {
                // don't include courses not available during current semester
                graph.removeNode(courseNode)
                nodeMap.remove(code)
                continue
            }

            val prereqs = setupPrereqs(outline.prerequisites)
            for (prereq in prereqs) {
                val prereqCode = prereq.trim().uppercase()
                val prereqNode = nodeMap.getOrPut(prereqCode) {Node(prereqCode)}
                if (!graph.contains(prereqNode)) {
                    graph.addNode(prereqNode)
                }
            }
        }

        // add edges
        for (course in courses) {
            val code = "${course.dept} ${course.number}".trim().uppercase()
            val courseNode = nodeMap[code] ?: continue

            val outline = try {
                withContext(IO) {
                    SfuCourseApi.fetchCourseOutline(course.dept, course.number)
                }
            } catch (e: Exception) {
                continue
            }

            val prereqs = setupPrereqs(outline.prerequisites).filter { it != code }
            for (prereq in prereqs) {
                val prereqCode = prereq.trim().uppercase()
                val prereqNode = nodeMap.getOrPut(prereqCode) {Node(prereqCode)}
                if (!graph.contains(prereqNode)) {
                    graph.addNode(prereqNode)
                }
                graph.addEdge(prereqNode, courseNode)
            }
        }

        return graph
    }

    private fun setupPrereqs(prereqRaw: String?): List<String> {
        if (prereqRaw.isNullOrBlank()) {
            return emptyList()
        }

        val courseRegex = Regex("""[A-Z]{3,4}\s\d{3}[A-Z]?""")
        return courseRegex.findAll(prereqRaw).map { it.value }.toList().distinct()
    }

    private fun getAllCourses(): List<RequirementCourse> {
        val courses = mutableListOf<RequirementCourse>()

        // lower division courses
        courses += CsRequirementsDb.lowerDivision

        // upper division courses
        for (group in CsRequirementsDb.upperGroups) {
            if (group is RequirementGroup.CourseList) {
                courses += group.courses
            }
            if (group is RequirementGroup.OrGroup) {
                for (list in group.options) {
                    courses += list.courses
                }
            }
        }

        return courses
    }
}

class GraphAdapter() : AbstractGraphAdapter<GraphAdapter.NodeViewHolder>() {
    inner class NodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById<TextView>(R.id.courseNode)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.graph_node, parent, false)
        return NodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        holder.text.text = getNodeData(position).toString()
    }

    override fun submitGraph(graph: Graph?) {
        super.submitGraph(graph)
        this.graph = graph
        notifyDataSetChanged()
    }
}