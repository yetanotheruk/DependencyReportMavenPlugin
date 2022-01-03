# Yet Another Dependency Report Maven Plugin
This Maven Plugin aims to help developers with FOSS compliance. It extends the dependency:tree functions to generate and export a CSV Report of all the required Maven Libraries for the project.

## Quick Start
dependency-report-maven-plugin is provided as a pre-built maven plugin, released in Maven Central.

By default, the plugin will run during the Maven Verify Phase and export a file into the projects target directory with the filename dependency-report.csv with the following format.

```
id,groupId,artifactId,version,classifier,type,scope
```

For example
```
id,groupId,artifactId,version,classifier,type,scope
org.springframework.boot:spring-boot:jar:2.6.1,org.springframework.boot,spring-boot,2.6.1,,jar,compile
```

To start using dependency-report-maven-plugin simply add the following plugin to your project POM.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>uk.yetanother</groupId>
            <artifactId>dependency-report-maven-plugin</artifactId>
            <version>1.1.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>csv</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Or it can be run directly by 

```shell
mvn uk.yetanother:dependency-report-maven-plugin:1.1.0:csv
```
## Maven
###Goals and Parameters

>**csv**
> -- Generates a CSV FOSS report and exports it.

| Parameter       | Type    | Default                    | Mandatory | Description                                                                                |
|-----------------|---------|----------------------------|-----------|--------------------------------------------------------------------------------------------|
| console         | boolean | false                      | No        | Should the report also be printed to the console.                                          |
| outputDirectory | String  | ${project.build.directory} | No        | Where should the report be exported too. By default the projects target directory is used. |

<hr/>

>**create-database**
> -- Instructs the system to create a new Datastore of additional information for FOSS items from a provided datafile.

| Parameter | Type   | Default | Mandatory | Description                        |
|-----------|--------|---------|-----------|------------------------------------|
| datafile  | String | N/A     | Yes       | Full path to the datafile to load  |

<hr/>

>**update-database**
> -- Instructs the system to add or update information held in the Datastore from the provided datafile.

| Parameter | Type    | Default | Mandatory | Description                                                                                |
|-----------|---------|---------|-----------|--------------------------------------------------------------------------------------------|
| datafile  | String  | N/A     | Yes       | Full path to the datafile to load                                                          |
| override  | boolean | false   | No        | whether to update existing datastore records with the data in the provided datafile or not |

<hr/>

>**clear-database**
> -- Instructs the system to delete all information held in the Datastore and to return to default behaviour.

| Parameter | Type | Default | Mandatory | Description |
|-----------|------|---------|-----------|-------------|
| None      | -    | -       | -         | -           |

##How-To
###Adding custom fields to FOSS Report
From Version 1.1.0+ you are able to introduce your own fields to the exported report. By default, the exported FOSS report contains the following headings;
* id
* groupId
* artifactId
* version
* classifier
* type
* scope

However, it is highly likely for your own FOSS compliance you will need to collect additional information. Maybe you already have a large collection of information you would like to drawn upon.

This can be achieved by providing a CSV datafile to the plugin and when the report is being generated it will look to see if additional information is found and add this to the report automatically.

The CSV datafile must have the first rows as the headings with first column as the id of the FOSS item (this is the full Maven ID) and then as many columns as you require in the report. This will be added to the report in the same order after the default columns.
```
id,customCol1,customCol2...,customColn
```

For example, If we run the Dependency Report Plugin on a project and get the following;

```
id,groupId,artifactId,version,classifier,type,scope
org.springframework.boot:spring-boot-starter-tomcat:jar:2.6.1,org.springframework.boot,spring-boot-starter-tomcat,2.6.1,,jar,compile,,
org.springframework.boot:spring-boot-starter-json:jar:2.6.1,org.springframework.boot,spring-boot-starter-json,2.6.1,,jar,compile,,
org.springframework.data:spring-data-relational:jar:2.3.0,org.springframework.data,spring-data-relational,2.3.0,,jar,compile,,
```

But we also need the automated report to have the following fields;
* licence
* subjectToExportControl
* assessor

We can load the following datafile;

```
id,licence,subjectToExportControl,assessor
org.springframework.boot:spring-boot-starter-tomcat:jar:2.6.1,Apache 11.0,No,M.Russell
org.springframework.data:spring-data-relational:jar:2.3.0,Apache 12.0,Yes,A.Baker
```
Using the command

```shell
mvn uk.yetanother:dependency-report-maven-plugin:1.1.0:create-datastore -Ddatafile="C:/temp/data.csv"
```

Now when we run the report we get the following output; (Note: as for the item with no data in the datafile, the column are left blank)
```
id,groupId,artifactId,version,classifier,type,scope,licence,subjectToExportControl,assessor
org.springframework.boot:spring-boot-starter-tomcat:jar:2.6.1,org.springframework.boot,spring-boot-starter-tomcat,2.6.1,,jar,compile,,,Apache 11.0,No,M.Russell
org.springframework.boot:spring-boot-starter-json:jar:2.6.1,org.springframework.boot,spring-boot-starter-json,2.6.1,,jar,compile,,,,,
org.springframework.data:spring-data-relational:jar:2.3.0,org.springframework.data,spring-data-relational,2.3.0,,jar,compile,,,Apache 12.0,Yes,A.Baker
```

Overtime as the datafile is extended by yourself and pootontally other projects the updates can be shared and updated to the plugins Datastore using the command;
```shell
mvn uk.yetanother:dependency-report-maven-plugin:1.1.0:update-datastore -Ddatafile="C:/temp/data.csv"
```

## Built With
* [Maven](https://maven.apache.org/) - Dependency Management
* [DependencyCheck 6.5.0](https://jeremylong.github.io/DependencyCheck/index.html) - Vulnerability Scanner (No Issues found at time of scan)

## Authors
* **Ashley Baker** - (https://github.com/ashleycbaker)

## License
This project is licensed under Apache License, Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details