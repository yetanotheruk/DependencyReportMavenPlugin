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
 * Mojo to have the update-datastore goal. This given a valid datafile will update the datastore used by the plugin to add
 * addition FOSS attributes to the exported reports.
 */
@Mojo(name = "update-datastore", defaultPhase = LifecyclePhase.INITIALIZE)
public class UpdateDatastoreMojo extends AbstractMojo {

    @Parameter(property = "datafile", required = true)
    private String datafile;

    @Parameter(defaultValue = "false", property = "override", required = false)
    private boolean overrideExisting;

    @Override
    public void execute() throws MojoExecutionException {
        IFossDatastore fossDatastore = new InternalFileFossDatastore(getLog());
        Path datafilePath = Paths.get(datafile);
        if (Files.exists(datafilePath)) {
            fossDatastore.updateDatastore(datafilePath, overrideExisting);
        } else {
            throw new MojoExecutionException(String.format("The file provided %s cannot be located", datafile));
        }
    }

}
