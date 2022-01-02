package uk.yetanother.dependency.report.datastore;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "clear-datastore", defaultPhase = LifecyclePhase.INITIALIZE)
public class ClearDatastoreMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        IFossDatastore fossDatastore = new InternalFileFossDatastore(getLog());
        fossDatastore.clearDatastore();
    }

}
