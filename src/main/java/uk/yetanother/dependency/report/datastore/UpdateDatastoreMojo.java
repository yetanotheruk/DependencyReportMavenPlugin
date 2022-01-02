package uk.yetanother.dependency.report.datastore;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.file.Files;
import java.nio.file.Path;

@Mojo(name = "update-datastore", defaultPhase = LifecyclePhase.INITIALIZE)
public class UpdateDatastoreMojo extends AbstractMojo {

    @Parameter(property = "datafile", required = true)
    private Path datafile;

    @Parameter(defaultValue = "false", property = "override", required = false)
    private boolean overrideExisting;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        IFossDatastore fossDatastore = new InternalFileFossDatastore(getLog());
        if (Files.exists(datafile)) {
            fossDatastore.updateDatastore(datafile, overrideExisting);
        } else {
            getLog().error(String.format("The file provided %s cannot be located", datafile.toString()));
        }
    }

}
