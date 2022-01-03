package uk.yetanother.dependency.report.datastore;

import org.apache.maven.plugin.MojoExecutionException;

import java.nio.file.Path;

/**
 * The interface FOSS datastore. This allows for the actual storage medium to be abstracted away and changed
 * without any user impact going forward.
 */
public interface IFossDatastore {

    /**
     * Get additional attribute headings that have been provided and need to be added to the basic report.
     *
     * @return the string [ ] of the additional headings in the ordered provided
     */
    String[] getAdditionalAttributeHeadings();

    /**
     * Get additional attributes for FOSS item. Based on the FOSS Id ID provided lookup in the datastore for additional details held.
     *
     * @param fossId the FOSS item id
     * @return the string [ ] returns the additional attributes in the same order as the additional headings or
     * an array of null strings if the datastore does not have any more details for this FOSS item.
     */
    String[] getAdditionalAttributesForFossItem(String fossId);

    /**
     * Creates the datastore using the data in the provided datafile. If a datastore already exists it will be replaced.
     *
     * @param fileToLoad the datafile to use
     */
    void createDatastore(Path fileToLoad) throws MojoExecutionException;

    /**
     * Updates the datastore using. the data in the provided datafile. This maintains existing datastore and add or updates data to it.
     *
     * @param fileToLoad       the datafile to use
     * @param overrideExisting whether to update existing datastore records with the data in the provided datafile or not.
     */
    void updateDatastore(Path fileToLoad, boolean overrideExisting) throws MojoExecutionException;

    /**
     * Clears the datastore, completely removing any additional data the plugin held.
     *
     * @return whether the datastore was deleted or not
     */
    boolean clearDatastore() throws MojoExecutionException;

    /**
     * Is datastore empty.
     *
     * @return true if the datastore is empty or not initialized, false if not.
     */
    boolean isDatastoreEmpty();

}
