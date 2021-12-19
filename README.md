# Yet Another Dependency Report Maven Plugin
This Maven Plugin aims to help developers with FOSS compliance. It extends the dependency:tree functions to generate and export a CSV Report of all the required Maven Libraries for the project.

## Getting Started
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

### Maven

To start using dependency-report-maven-plugin simply add the following plugin to your project POM.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>uk.yetanother</groupId>
            <artifactId>dependency-report-maven-plugin</artifactId>
            <version>1.0.0</version>
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
mvn uk.yetanother:dependency-report-maven-plugin:1.0.0:csv
```

####Parameters
| Parameter       | Type    | Default                    | Description                                                                                |
|-----------------|---------|----------------------------|--------------------------------------------------------------------------------------------|
| console         | boolean | false                      | Should the report also be printed to the console.                                          |
| outputDirectory | String  | ${project.build.directory} | Where should the report be exported too. By default the projects target directory is used. |

## Built With
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors
* **Ashley Baker** - (https://github.com/ashleycbaker)

## License
This project is licensed under Apache License, Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details