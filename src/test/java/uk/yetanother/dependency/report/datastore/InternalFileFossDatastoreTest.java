package uk.yetanother.dependency.report.datastore;

import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class InternalFileFossDatastoreTest {

    private static final Path DEFAULT_DATASTORE_LOCATION = Paths.get("fossAdditionalAttributes.csv");
    private static final String DEFAULT_TEST_DATAFILE_FILENAME = "fossDatafile.csv";

    @Before
    public void setup() throws IOException {
        Files.deleteIfExists(DEFAULT_DATASTORE_LOCATION);
    }

    @AfterClass
    public static void afterClass() throws IOException {
        Files.deleteIfExists(DEFAULT_DATASTORE_LOCATION);
    }

    @Test
    public void createDatastoreTest() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);

        assertTrue(doesFossFileExist());
        assertEquals(3, fossDatastore.getAdditionalAttributeHeadings().length);
        assertTrue(checkAttributeSequenceMatches("col", fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(checkAttributeSequenceMatches("1.", fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(checkAttributeSequenceMatches("2.", fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("3.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
    }

    @Test
    public void clearDatastoreTest() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);
        assertTrue(doesFossFileExist());

        fossDatastore.clearDatastore();
        assertFalse(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss3")));
    }

    @Test
    public void isDatastoreEmptyTest() throws URISyntaxException {
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        assertTrue(fossDatastore.isDatastoreEmpty());

        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        fossDatastore.createDatastore(filePath);
        assertFalse(fossDatastore.isDatastoreEmpty());

        fossDatastore.clearDatastore();
        assertTrue(fossDatastore.isDatastoreEmpty());
    }

    @Test
    public void updateDatastoreValidNoOverrideTest() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);
        assertTrue(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));

        Path filePathValidUpdate = Paths.get(ClassLoader.getSystemResource("fossDatafileValidUpdate.csv").toURI());
        fossDatastore.updateDatastore(filePathValidUpdate, false);
        assertTrue(doesFossFileExist());
        assertEquals(3, fossDatastore.getAdditionalAttributeHeadings().length);
        assertTrue(checkAttributeSequenceMatches("col", fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(checkAttributeSequenceMatches("1.", fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(checkAttributeSequenceMatches("2.", fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("3.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
        assertTrue(checkAttributeSequenceMatches("4.", fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(checkAttributeSequenceMatches("5.", fossDatastore.getAdditionalAttributesForFossItem("foss5")));
    }

    @Test
    public void updateDatastoreValidWithOverrideTest() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);
        assertTrue(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));

        Path filePathValidUpdate = Paths.get(ClassLoader.getSystemResource("fossDatafileValidUpdate.csv").toURI());
        fossDatastore.updateDatastore(filePathValidUpdate, true);
        assertTrue(doesFossFileExist());
        assertEquals(3, fossDatastore.getAdditionalAttributeHeadings().length);
        assertTrue(checkAttributeSequenceMatches("col", fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(checkAttributeSequenceMatches("1.", fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(checkAttributeSequenceMatches("2.", fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("33.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
        assertTrue(checkAttributeSequenceMatches("4.", fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(checkAttributeSequenceMatches("5.", fossDatastore.getAdditionalAttributesForFossItem("foss5")));
    }

    @Test
    public void updateDatastoreInvalidColumnsTest() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);
        assertTrue(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));

        Path filePathValidUpdate = Paths.get(ClassLoader.getSystemResource("fossDatafileInvalidUpdate.csv").toURI());
        fossDatastore.updateDatastore(filePathValidUpdate, false);

        assertEquals(3, fossDatastore.getAdditionalAttributeHeadings().length);
        assertTrue(checkAttributeSequenceMatches("col", fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(checkAttributeSequenceMatches("1.", fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(checkAttributeSequenceMatches("2.", fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("3.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));
    }

    @Test
    public void updateDatastoreInvalidNumberOfColumnsTest() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);
        assertTrue(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));

        Path filePathValidUpdate = Paths.get(ClassLoader.getSystemResource("fossDatafileInvalidUpdate2.csv").toURI());
        fossDatastore.updateDatastore(filePathValidUpdate, false);

        assertEquals(3, fossDatastore.getAdditionalAttributeHeadings().length);
        assertTrue(checkAttributeSequenceMatches("col", fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(checkAttributeSequenceMatches("1.", fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(checkAttributeSequenceMatches("2.", fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("3.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));
    }

    @Test
    public void updateDatastoreThatDoesNotExist() throws URISyntaxException {
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        assertFalse(doesFossFileExist());

        Path filePathValidUpdate = Paths.get(ClassLoader.getSystemResource("fossDatafileValidUpdate.csv").toURI());
        fossDatastore.updateDatastore(filePathValidUpdate, true);
        assertTrue(doesFossFileExist());

        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("33.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
        assertTrue(checkAttributeSequenceMatches("4.", fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(checkAttributeSequenceMatches("5.", fossDatastore.getAdditionalAttributesForFossItem("foss5")));
    }

    @Test
    public void constructorTest() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);
        assertTrue(doesFossFileExist());

        fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        assertEquals(3, fossDatastore.getAdditionalAttributeHeadings().length);
        assertTrue(checkAttributeSequenceMatches("col", fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(checkAttributeSequenceMatches("1.", fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(checkAttributeSequenceMatches("2.", fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("3.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
    }

    @Test
    public void loadWithEmptyDataFile() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource("fossDatafileWithNoData.csv").toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);

        assertFalse(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss3")));
    }

    @Test
    public void loadWithEmptyFile() throws URISyntaxException {
        Path filePath = Paths.get(ClassLoader.getSystemResource("empty.csv").toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);

        assertFalse(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss3")));
    }

    private boolean doesFossFileExist() {
        return Files.exists(DEFAULT_DATASTORE_LOCATION);
    }

    private boolean areAttributesEmpty(String[] attributes) {
        for (String attribute : attributes) {
            if (attribute != null && !attribute.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAttributeSequenceMatches(String prefix, String[] attributes) {
        for (int i = 1; i < attributes.length; i++) {
            if (!attributes[i - 1].equals(prefix + i)) {
                return false;
            }
        }
        return true;
    }

}