package darine_abdelmotalib.example.groupproject.ui.tree

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
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
import kotlin.text.lowercase

class CourseTreeFragment : Fragment() {
    private lateinit var graphRecycler: RecyclerView
    private lateinit var adapter: GraphAdapter
    private val viewModel: CourseTreeViewModel by viewModels()
    private var courseList: List<String> = emptyList()
    private val prereqMap = mutableMapOf<String, List<String>>()

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
        viewModel.courseList.observe(viewLifecycleOwner) { list ->
            if (::adapter.isInitialized) {
                adapter.updateGraph(
                    viewModel.courseList.value ?: emptyList(),
                    viewModel.prereqMap.value ?: emptyMap())
                adapter.notifyDataSetChanged()
            }
        }

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

        lifecycleScope.launch {
            // build graph
            if (viewModel.graph.value == null) {
                val graph = withContext(IO) {
                    buildGraph()
                }
                viewModel.graph.value = graph
                viewModel.courseList.value = courseList
                viewModel.prereqMap.value = prereqMap
            }

            // adapter class extends from 'AbstractGraphAdapter'
            adapter = GraphAdapter(courseList, prereqMap)
            graphRecycler.adapter = adapter

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

        // check if we visited a node in the tree for recursively added prerequisites (avoid loops)
        val visited = mutableSetOf<String>()

        // add all courses
        for (course in courses) {
            val code = "${course.dept} ${course.number}".trim().uppercase()
            addCourses(code, graph, nodeMap, prereqMap, visited)
        }

        courseList = nodeMap.keys.toList()

        // ensure courses not offered are not included in graph
        val coursesNotOffered = graph.nodes.filter { it !in nodeMap.values }
        for (node in coursesNotOffered) {
            graph.removeNode(node)
        }

        return graph
    }

    private suspend fun addCourses(
        courseCode: String,
        graph: Graph,
        nodeMap: MutableMap<String, Node>,
        prereqMap: MutableMap<String, List<String>>,
        visitedNodes: MutableSet<String>
    ) {
        // course already added, nothing else to add
        if (visitedNodes.contains(courseCode)) {
            return
        }

        // create node
        val courseNode = nodeMap.getOrPut(courseCode) {Node(courseCode)}
        if (!graph.contains(courseNode)) {
            graph.addNode(courseNode)
        }

        val dept = courseCode.substringBefore(" ").trim().uppercase()
        val number = courseCode.substringAfter(" ").trim().uppercase()
        val outline = try {
            withContext(IO) {
                SfuCourseApi.fetchCourseOutline(dept, number)
            }
        } catch (e: Exception) {
            // don't include courses not available during current semester
            graph.removeNode(courseNode)
            nodeMap.remove(courseCode)
            visitedNodes.remove(courseCode)
            return
        }

        // get prereqs
        val prereqs = parsePrereqs(outline.prerequisites)
        val filteredPrereqs = setupPrereqs(prereqs.toString()).filter { it != courseCode }
        prereqMap[courseCode] = filteredPrereqs

        // add prereq nodes and draw edges
        for (prereq in filteredPrereqs) {
            val prereqNode = nodeMap.getOrPut(prereq) {Node(prereq)}
            if (!graph.contains(prereqNode)) {
                graph.addNode(prereqNode)
            }

            // recursively add prereqs until all added
            addCourses(prereq, graph, nodeMap, prereqMap, visitedNodes)
            graph.addEdge(prereqNode, courseNode)
        }

        // mark node as visited
        visitedNodes.add(courseCode)
        Log.d("$courseNode", "$filteredPrereqs")
    }

    private fun parsePrereqs(prereqText: String?): List<String> {
        val prereqsResult = mutableListOf<String>()

        if (prereqText != null) {
            val parts = prereqText.split(Regex(",| and "))
            for (part in parts) {
                val groups = part.replace("[()]".toRegex(), "").trim() // prereqs groups by parentheses
                val or = groups.split(Regex("\\bor\\b")).map { it.trim() } // split "or" groups (CMPT 120 or CMPT 130)
                val course = Regex("""[A-Z]{3,4}\s*\d{3}[A-Z]?""").find(or.first())
                if (course != null) {
                    prereqsResult.add(course.value)
                }
            }
        }

        return prereqsResult
    }

    private fun setupPrereqs(prereqRaw: String?): List<String> {
        if (prereqRaw.isNullOrBlank()) {
            return emptyList()
        }

        val courseRegex = Regex("""[A-Z]{3,4}\s\d{3}[A-Z]?""")
        return courseRegex.findAll(prereqRaw)
            .map { it.value.trim().uppercase() }.toList().distinct()
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

class GraphAdapter(
    private var courses: List<String>,
    private var coursePrereqs: Map<String, List<String>>) :
    AbstractGraphAdapter<GraphAdapter.NodeViewHolder>() {
    inner class NodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById<TextView>(R.id.courseNode)
    }

    fun updateGraph(courseChanges: List<String>, updatedCoursePrereqs: Map<String, List<String>>) {
        courses = courseChanges
        coursePrereqs = updatedCoursePrereqs
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

        val courseCode = getNodeData(position).toString()
        val course = courses[position]
        val prereqs = coursePrereqs[courseCode] ?: emptyList()

        // format course codes for preferences keys ("CMPT 120" -> "cmpt-120")
        val regex = Regex("""([A-Za-z]+)\s*(\d+[A-Za-z]*)""")
        val code = regex.matchEntire(courseCode)

        var courseCompletion = false
        var prereqsCompletion = true
        val prefs = holder.itemView.context.getSharedPreferences("course_completion_prefs", Context.MODE_PRIVATE)
        if (code != null) {
            val prefKey = "${code.groupValues[1]}-${code.groupValues[2]}".lowercase()
            courseCompletion = prefs.getBoolean(prefKey, false)

            // check if all prereqs are completed
            for (prereq in prereqs) {
                val prereqCode = regex.matchEntire(prereq)
                if (prereqCode != null) {
                    val prefKey = "${prereqCode.groupValues[1]}-${prereqCode.groupValues[2]}".lowercase()
                    val completed = prefs.getBoolean(prefKey, false)
                    if (!completed) {
                        prereqsCompletion = false
                        break
                    }
                }
            }
        }

        val nodeColor = holder.text.background
        if (nodeColor is GradientDrawable) { // default is grey - cannot take course
            if (courseCompletion == true) {
                nodeColor.setColor(Color.parseColor("#98FD8F")) // green - course completed
            }
            else if (prereqsCompletion == true && courseCompletion == false) {
                nodeColor.setColor(Color.parseColor("#99ccff")) // blue - prereqs met, can enroll in course
            }
            else {
                nodeColor.setColor(holder.itemView.resources.getColor(R.color.light_grey))
            }
        }
    }

    override fun submitGraph(graph: Graph?) {
        super.submitGraph(graph)
        this.graph = graph
        notifyDataSetChanged()
    }
}