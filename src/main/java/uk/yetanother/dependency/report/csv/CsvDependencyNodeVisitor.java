package uk.yetanother.dependency.report.csv;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.Map;

/**
 * CSV dependency node visitor is used by the Maven Dependency Tree code and builds up a unique set of dependencies seen.
 */
public class CsvDependencyNodeVisitor implements DependencyNodeVisitor {

    private final Map<String, String> dependencies;

    /**
     * CSV dependency node visitor constructor. Takes an empty Set of Strings that will be populated with the dependencies
     * seen while parsing the dependency tree.
     *
     * @param dependencies empty Set of Strings that will be populated with dependency information.
     */
    public CsvDependencyNodeVisitor(Map<String, String> dependencies) {
        this.dependencies = dependencies;
    }

    public boolean visit(DependencyNode node) {
        Artifact artifact = node.getArtifact();
        if (artifact != null) {
            this.dependencies.put(artifact.getId(),
                    String.format("%s,%s,%s,%s,%s,%s,%s",
                            artifact.getId(),
                            artifact.getGroupId(),
                            artifact.getArtifactId(),
                            artifact.getVersion(),
                            artifact.getClassifier() != null ? artifact.getClassifier() : "",
                            artifact.getType(),
                            artifact.getScope() != null ? artifact.getScope() : ""));
        }
        return true;
    }

    public boolean endVisit(DependencyNode node) {
        return true;
    }
}
