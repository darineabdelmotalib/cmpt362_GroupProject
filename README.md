SFU Course Planner

A mobile application designed to help SFU students plan their degrees, track program requirements, and visualize course dependencies. Built as part of CMPT 362 – Mobile Application Development.

Overview

The SFU Course Planner provides students with a structured way to:

Track and complete program requirements

View progress toward degree completion

Explore detailed course information

Understand prerequisite paths through an interactive course tree

Navigate quickly between major sections with a clean UI

Core Features

Program Requirements Checklist
Interactive list of required courses with permanent saving of completed items.

Profile Progress Tracking
Displays overall program progress based on completed requirements.

Semester Course Details
Provides lecture, lab, instructor, and scheduling information for selected semesters.

Program Search & Selection
Searchable list of majors and minors for accurate setup.

Interactive Course Tree
Dynamic visualization of all course prerequisites and dependencies.

Persistent Database Storage
Supports requirement tracking, course data, and future scheduling features.

Navigation Drawer
Access Profile, Program Requirements, Semester Plan, and Course Tree from anywhere in the app.

Project Structure

Profile Section: User details, program selection, progress overview

Program Requirements: Checklist and persistent tracking

Semester Plan: Detailed course information per term

Course Tree: Visualization of prerequisite relationships

Navigation: Drawer-based app-wide navigation system

Threading Model
UI Thread

Handles rendering, navigation, progress updates, and interface interactions.

Background Thread

Handles database reads/writes, loading saved data, course tree data processing, and scheduling-related operations.

Future Enhancements

Color-coded course connections in the Course Tree

Boxes indicating courses not offered in the current semester

Interactive Maze Timeline for visualizing academic progress

Dark Mode

Help and Info section

Improved UI transitions and layout polish

Course notes and tagging

Course ratings (external data and in-app ratings)

Team

Angela Liong – Course Tree Visualization

Cas Sugihwo – App Structure & Navigation

Darine Abdelmotalib – Program Requirements & Database

Elaiza Chavez – Profile Progress Bar

Rana Hoshyarsadeghi – Presentation, website, and feature planning
