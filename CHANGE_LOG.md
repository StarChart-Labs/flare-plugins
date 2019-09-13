# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
### Added
- source-jars plug-in which provides tasks to build source and javadoc jars and adds them to the project's archives artifact configuration
- Integrate source-jars with maven-publish plug-in as published artifacts
- metadata-base plug-in which provides a DSL for re-usable project meta data values
- Add metadata-base plug-in to multi-module-library plug-in setup for all projects
- metadata-pom plug-in which applies meta data in metadata-base DSL to generated Maven POM files
- Add metadata-pom plug-in to multi-module-library plug-in setup for all projects

### Changed
- Started allowing limitation of the configurations dependency constraints are applied to via the constraint file specification

## [0.1.0]
### Added
- managed-credentials plug-in which provides a DSL for re-usable loaded credentials from multiple sources
- dependency-constraints plug-in which provides a DSL for applying dependency version constraints to all configurations in bulk from an external file
- merge-coverage-reports plug-in which provides setup for merging all coverage reports into a single XML report for the entire Gradle project
- increased-test-logging plug-in which provides standard conventions for logging test output in Gradle build logs
- multi-module-library plug-in which provides application and default conventions for all Flare plug-ins applicable in a StarChart Labs multi-module library configuration
