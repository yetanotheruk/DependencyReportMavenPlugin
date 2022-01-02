package uk.yetanother.dependency.report.datastore;

import java.nio.file.Path;

public interface IFossDatastore {

    String[] getAdditionalAttributeHeadings();

    String[] getAdditionalAttributesForFossItem(String fossId);

    void createDatastore(Path fileToLoad);

    void updateDatastore(Path fileToLoad, boolean overrideExisting);

    boolean clearDatastore();

    boolean isDatastoreEmpty();

}
