# Plug-ins

Flare provides general conventions by mixing and matching a set of behaviors provided by individual plug-ins. This allows those whose builds do not precisely match a pre-defined convention to still make use of Flare's functionality

## org.starchartlabs.flare.dependency-constraints

The dependency-constraints plug-in allows defining version constraints for use across all sub-modules and configurations in a single properties file. This is done by adding a DSL to the project which allows specifying the file to read from.

### DSL

The DSL is named `dependencyConstraints`, and has a single `File` argument which references the file to read

```
dependencyConstraints {
	file file('example.properties')
}
```

### File Format

The file(s) currently supported by the plug-in follow a simple format. Lines may be empty, a comment, or a version definition. Comment lines start with the `#` character. Version lines are a GAV in the format `group:artifact:version`

## org.starchartlabs.flare.increased-test-logging

The increased-test-logging plug-in increases the test output for the Gradle test task. It is the equivalent of the boilerplate:

```
test {
    testLogging{
        exceptionFormat 'full'
        
        quiet{
            events 'failed', 'skipped'
        }
        
        info{
            events 'failed', 'skipped', 'passed', 'standard_out', 'standard_error'
        }
        
        debug{
            events 'failed', 'skipped', 'passed', 'standard_out', 'standard_error', 'started'
        }
    }
}
```

## org.starchartlabs.flare.managed-credentials

The managed-credentials plug-in adds a configuration DSL for defining re-usable username/password sets within the build. Credentials may be setup to read from multiple (ordered) sources. No attempt is made to read credentials until they are actively referenced within the build. Each ordered source is checked for both a username and password value, and the first source to provide both is used.

### DSL

The DSL is named `credentials`, and allows defining multiple named credential sets. Each named set has support for credentials via:

- `environment`, which reads a username and password from environment variables
- `systemProperties`, which reads a username and password from system properties
- `defaultCredentials`, which allows hard-coding a set of values. *It is HIGHLY recommended to avoid coding credentials into your build files directly*

```
credentials {
    example1 {
        environment('ENV1', 'ENV2')
        systemProperties('example_user', 'example_key')
        defaultLogin('defaultuser', 'defaultpassword')
    }
    
    example1 {
        environment('ENV3', 'ENV4')
    }
}
```

### Use

Created credentials may be reference via `${credentials.name1.username}` and `${credentials.name1.password}`

## org.starchartlabs.flare.merge-coverage-reports

The merge-coverage-reports plug-in adds a task which generates a single XML Jacoco report which includes coverage information from reports in all sub-modules. The added task (`mergeCoverageReports`), depends on the `test` tasks of all sub-modules, and is added as a dependency of the `check` task of the root project

## org.starchartlabs.flare.source-jars

The source-jars plug-in adds two tasks of type `Jar` - `sourcesJar`, which generates a jar file with the source files of the project, and `javadocJar`, which generates a jar file with the javadoc files of the project. These tasks are also added to the `archives` artifact configuration of the project.

These jars are intended to be uploaded alongside binary artifacts for open-source projects. They allow developers to trace through the associated code paths when debugging, and to view associated Javadoc documentation within their IDE.
