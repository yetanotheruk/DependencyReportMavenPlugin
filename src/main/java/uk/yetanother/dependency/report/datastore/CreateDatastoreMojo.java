package uk.yetanother.dependency.report.datastore;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Mojo to have the create-datastore goal. This given a valid datafile will create the datastore used by the plugin to add
 * addition FOSS attributes to the exported reports. If a datastore already exists it will be replaced.
 */
@Mojo(name = "create-datastore", defaultPhase = LifecyclePhase.INITIALIZE)
public class CreateDatastoreMojo extends AbstractMojo {

    @Parameter(property = "datafile", required = true)
    private String datafile;

    @Override
    public void execute() throws MojoExecutionException {
        IFossDatastore fossDatastore = new InternalFileFossDatastore(getLog());
        Path datafilePath = Paths.get(datafile);
        if (Files.exists(datafilePath)) {
            fossDatastore.createDatastore(datafilePath);
        } else {
            throw new MojoExecutionException(String.format("The file provided %s cannot be located", datafile));
        }
    }

}
