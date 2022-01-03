package uk.yetanother.dependency.report.datastore;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class InternalFileFossDatastoreTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static final String DEFAULT_TEST_DATAFILE_FILENAME = "fossDatafile.csv";
    private static Path datastoreLocation;

    @BeforeClass
    public static void beforeClass() throws Exception {
        datastoreLocation = InternalFileFossDatastore.getDatastoreLocation();
        System.out.println(datastoreLocation);
    }

    @AfterClass
    public static void afterClass() throws IOException {
        Files.deleteIfExists(datastoreLocation);
    }

    @Before
    public void setup() throws IOException {
        Files.deleteIfExists(datastoreLocation);
    }

    @Test
    public void createDatastoreTest() throws URISyntaxException, MojoExecutionException {
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
    public void clearDatastoreTest() throws URISyntaxException, MojoExecutionException {
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
    public void isDatastoreEmptyTest() throws URISyntaxException, MojoExecutionException {
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        assertTrue(fossDatastore.isDatastoreEmpty());

        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        fossDatastore.createDatastore(filePath);
        assertFalse(fossDatastore.isDatastoreEmpty());

        fossDatastore.clearDatastore();
        assertTrue(fossDatastore.isDatastoreEmpty());
    }

    @Test
    public void updateDatastoreValidNoOverrideTest() throws URISyntaxException, MojoExecutionException {
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
    public void updateDatastoreValidWithOverrideTest() throws URISyntaxException, MojoExecutionException {
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
    public void updateDatastoreInvalidColumnsTest() throws URISyntaxException, MojoExecutionException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);
        assertTrue(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));

        Path filePathValidUpdate = Paths.get(ClassLoader.getSystemResource("fossDatafileInvalidUpdate.csv").toURI());
        try{
            fossDatastore.updateDatastore(filePathValidUpdate, false);
            fail("Exception was expected");
        } catch (MojoExecutionException e){
            e.printStackTrace();
        }

        assertEquals(3, fossDatastore.getAdditionalAttributeHeadings().length);
        assertTrue(checkAttributeSequenceMatches("col", fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(checkAttributeSequenceMatches("1.", fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(checkAttributeSequenceMatches("2.", fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("3.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));
    }

    @Test
    public void updateDatastoreInvalidNumberOfColumnsTest() throws URISyntaxException, MojoExecutionException {
        Path filePath = Paths.get(ClassLoader.getSystemResource(DEFAULT_TEST_DATAFILE_FILENAME).toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));
        fossDatastore.createDatastore(filePath);
        assertTrue(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));

        Path filePathValidUpdate = Paths.get(ClassLoader.getSystemResource("fossDatafileInvalidUpdate2.csv").toURI());

        try{
            fossDatastore.updateDatastore(filePathValidUpdate, false);
            fail("Exception was expected");
        } catch (MojoExecutionException e){
            e.printStackTrace();
        }

        assertEquals(3, fossDatastore.getAdditionalAttributeHeadings().length);
        assertTrue(checkAttributeSequenceMatches("col", fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(checkAttributeSequenceMatches("1.", fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(checkAttributeSequenceMatches("2.", fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(checkAttributeSequenceMatches("3.", fossDatastore.getAdditionalAttributesForFossItem("foss3")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss4")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss5")));
    }

    @Test
    public void updateDatastoreThatDoesNotExist() throws URISyntaxException, MojoExecutionException {
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
    public void constructorTest() throws URISyntaxException, MojoExecutionException {
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
    public void loadWithEmptyDataFile() throws URISyntaxException, MojoExecutionException {
        Path filePath = Paths.get(ClassLoader.getSystemResource("fossDatafileWithNoData.csv").toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));

        try{
            fossDatastore.createDatastore(filePath);
            fail("Exception was expected");
        } catch (MojoExecutionException e){
            e.printStackTrace();
        }

        assertFalse(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss3")));
    }

    @Test
    public void loadWithEmptyFile() throws URISyntaxException, MojoExecutionException {
        Path filePath = Paths.get(ClassLoader.getSystemResource("empty.csv").toURI());
        InternalFileFossDatastore fossDatastore = new InternalFileFossDatastore(new DefaultLog(new ConsoleLogger()));

        try{
            fossDatastore.createDatastore(filePath);
            fail("Exception was expected");
        } catch (MojoExecutionException e){
            e.printStackTrace();
        }

        assertFalse(doesFossFileExist());
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributeHeadings()));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss1")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss2")));
        assertTrue(areAttributesEmpty(fossDatastore.getAdditionalAttributesForFossItem("foss3")));
    }

    private boolean doesFossFileExist() {
        return Files.exists(datastoreLocation);
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