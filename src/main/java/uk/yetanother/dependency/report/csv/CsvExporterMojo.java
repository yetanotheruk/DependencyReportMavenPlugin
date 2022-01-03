package uk.yetanother.dependency.report.csv;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.BuildingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import uk.yetanother.dependency.report.datastore.IFossDatastore;
import uk.yetanother.dependency.report.datastore.InternalFileFossDatastore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mojo to have the CSV goal. This class uses the Maven Dependency Tree to work out all the dependencies for the
 * maven project and formats the results into a CSV output.
 */
@Mojo(name = "csv", defaultPhase = LifecyclePhase.VERIFY)
public class CsvExporterMojo extends AbstractMojo {

    private static final String DELIMITER = ",";
    private static final String DEFAULT_HEADINGS = "id,groupId,artifactId,version,classifier,type,scope";

    @Component
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
    private List<MavenProject> reactorProjects;

    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "false", property = "console")
    private boolean console;

    @Override
    public void execute() throws MojoExecutionException {
        ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setProject(project);
        DependencyNode rootNode;
        try {
            rootNode = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null);
        } catch (DependencyGraphBuilderException e) {
            throw new MojoExecutionException("Cannot build project dependency graph", e);
        }

        IFossDatastore fossDatastore = new InternalFileFossDatastore(getLog());

        StringWriter writer = new StringWriter();
        writer.write(DEFAULT_HEADINGS + arrayToCsv(fossDatastore.getAdditionalAttributeHeadings()));
        Map<String, String> dependencies = processDependencyTree(rootNode);
        for (Map.Entry<String, String> dependency : dependencies.entrySet()) {
            writer.write("\n" + dependency.getValue() + arrayToCsv(fossDatastore.getAdditionalAttributesForFossItem(dependency.getKey())));
        }
        exportReport(writer.toString());
    }

    private String arrayToCsv(String[] data) {
        StringBuilder stringToReturn = new StringBuilder();
        for (String item : data) {
            stringToReturn.append(DELIMITER).append(item == null ? "" : item);
        }
        return stringToReturn.toString();
    }

    private Map<String, String> processDependencyTree(DependencyNode theRootNode) {
        Map<String, String> dependencies = new HashMap<>();
        DependencyNodeVisitor visitor = new CsvDependencyNodeVisitor(dependencies);
        visitor = new BuildingDependencyNodeVisitor(visitor);
        theRootNode.accept(visitor);
        return dependencies;
    }

    private void exportReport(String report) throws MojoExecutionException {
        getLog().info("Exporting CSV Report to " + outputDirectory.getAbsolutePath());

        if (console) {
            getLog().info("CSV Dependency Report\n" + report);
        }

        if (!outputDirectory.exists()) {
            boolean result = outputDirectory.mkdirs();
            if (!result) {
                throw new MojoExecutionException("Error creating folders for path " + outputDirectory.getAbsolutePath());
            }
        }

        File reportFile = new File(outputDirectory, "dependency-report.csv");
        try (FileWriter w = new FileWriter(reportFile)) {
            w.write(report);
        } catch (IOException e) {
            throw new MojoExecutionException("Error writing report " + reportFile, e);
        }

        getLog().info("CSV Report exported successfully");
    }
}
