<h1>SFU Course Planner</h1>

<p>A mobile application designed to help SFU students plan their degrees, track program requirements, and visualize prerequisite pathways. Developed for <strong>CMPT 362, Mobile Application Development</strong>.</p>

<h2>Overview</h2>

<p>The SFU Course Planner provides a structured and interactive way for students to:</p>

<ul>
  <li>Track and complete program requirements</li>
  <li>View degree progress</li>
  <li>Search for majors and minors</li>
  <li>Explore detailed course information</li>
  <li>Visualize prerequisite chains with an interactive Course Tree</li>
  <li>Navigate efficiently with a drawer-based interface</li>
</ul>

<h2>Core Features</h2>

<ul>
  <li>Program Requirements Checklist</li>
  <li>Profile Progress Tracking</li>
  <li>Semester Course Details</li>
  <li>Program Search &amp; Selection</li>
  <li>Interactive Course Tree</li>
  <li>Persistent Database Storage</li>
  <li>Navigation Drawer</li>
</ul>

<h2>Project Structure</h2>

<pre>
app/
 ├── manifests/
 ├── java/com/.../courseplanner/
 │    ├── activities/
 │    ├── adapters/
 │    ├── data/
 │    │     ├── AppDatabase.kt
 │    │     ├── CourseDao.kt
 │    │     ├── RequirementDao.kt
 │    │     └── entities/
 │    ├── models/
 │    ├── ui/
 │    └── utils/
 └── res/
      ├── layout/
      ├── drawable/
      ├── values/
      └── menu/
</pre>

<h2>Threading Model</h2>

<h3>UI Thread</h3>
<ul>
  <li>UI rendering</li>
  <li>Navigation</li>
  <li>Progress bar updates</li>
  <li>Course Tree visualization</li>
  <li>Checklist interactions</li>
</ul>

<h3>Background Thread</h3>
<ul>
  <li>Database read/write operations</li>
  <li>Loading requirement states</li>
  <li>Loading Course Tree data</li>
  <li>Scheduling computations (future)</li>
</ul>

<h2>Future Enhancements</h2>

<ul>
  <li>Color-coded course connections</li>
  <li>"Unavailable This Semester" indicator boxes</li>
  <li>Interactive Maze Timeline</li>
  <li>Dark Mode</li>
  <li>Help &amp; Info section</li>
  <li>Smoother UI transitions</li>
  <li>Course notes and tagging</li>
  <li>Course ratings (external + in-app)</li>
</ul>


<h2>Team</h2>

<ul>
  <li>Angela Liong – Course Tree Visualization</li>
  <li>Cas Sugihwo – App Structure & Navigation</li>
  <li>Darine Abdelmotalib – Program Requirements & Database</li>
  <li>Elaiza Chavez – Profile Progress Bar</li>
  <li>Rana Hoshyarsadeghi – Presentation, website updates, feature planning</li>
</ul>

