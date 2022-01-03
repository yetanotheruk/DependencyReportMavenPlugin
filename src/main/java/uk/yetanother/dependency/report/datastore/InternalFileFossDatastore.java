package uk.yetanother.dependency.report.datastore;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The FOSS Datastore implementation that holds the data as a CSV file within the filesystem.
 */
public class InternalFileFossDatastore implements IFossDatastore {

    private static final String ID_COL_NAME = "id";

    private final Log logger;
    private final Map<String, String[]> fossAttributes = new HashMap<>();
    private final Path datastoreLocation;
    private String[] headings = new String[0];

    /**
     * Instantiates a new Internal file FOSS datastore.
     *
     * @param logger the logger
     */
    public InternalFileFossDatastore(Log logger) throws MojoExecutionException {
        datastoreLocation = getDatastoreLocation();
        logger.info("Foss Datastore located at " + datastoreLocation);
        this.logger = logger;
        if (Files.exists(datastoreLocation)) {
            processFossData(loadDatastore(datastoreLocation));
        }
    }

    public static Path getDatastoreLocation() throws MojoExecutionException {
        try {
            String dataDir = new File(InternalFileFossDatastore.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            return Paths.get(dataDir + FileSystems.getDefault().getSeparator() + "fossAdditionalAttributes.csv");
        } catch (URISyntaxException e) {
            throw new MojoExecutionException("Unable to determine datastore location", e);
        }
    }

    private List<String[]> loadDatastore(Path filePath) throws MojoExecutionException {
        try (Reader reader = Files.newBufferedReader(filePath); CSVReader csvReader = new CSVReader(reader)) {
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            throw new MojoExecutionException("Unable to read datastore data", e);
        }
    }

    private void saveDatastore(List<String[]> stringArray, Path path) throws MojoExecutionException {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(path.toString()));
            writer.writeAll(stringArray);
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write to the datastore", e);
        }
    }

    private void processFossData(List<String[]> lines) {
        if (lines != null && lines.size() > 1) {
            headings = Arrays.copyOfRange(lines.get(0), 1, lines.get(0).length);
            lines.remove(0);
            for (String[] line : lines) {
                fossAttributes.put(line[0], Arrays.copyOfRange(line, 1, line.length));
            }
        }
    }

    @Override
    public String[] getAdditionalAttributeHeadings() {
        return headings;
    }

    @Override
    public String[] getAdditionalAttributesForFossItem(String fossId) {
        return fossAttributes.getOrDefault(fossId, new String[headings.length]);
    }

    @Override
    public void createDatastore(Path fileToLoad) throws MojoExecutionException {
        List<String[]> lines = loadDatastore(fileToLoad);
        if (lines.size() > 1) {
            clearDatastore();
            saveDatastore(lines, datastoreLocation);
            processFossData(lines);
        } else {
            throw new MojoExecutionException(String.format("Either no data found in the provided datafile or the file could not be read. (%s)", fileToLoad.toString()));
        }
    }

    @Override
    public void updateDatastore(Path fileToLoad, boolean overrideExisting) throws MojoExecutionException {
        // If the datastore is empty just redirect to the create process.
        if (isDatastoreEmpty()) {
            createDatastore(fileToLoad);
            return;
        }

        List<String[]> lines = loadDatastore(fileToLoad);
        if (lines.size() > 1) {
            if (checkHeadingsMatch(headings, Arrays.copyOfRange(lines.get(0), 1, lines.get(0).length))) {
                lines.remove(0);
                updateDatastoreData(lines, overrideExisting);

                List<String[]> newDataLines = new ArrayList<>();
                newDataLines.add(ArrayUtils.addAll(new String[]{ID_COL_NAME}, headings));
                for (Map.Entry<String, String[]> fossAttribute : fossAttributes.entrySet()) {
                    newDataLines.add(ArrayUtils.addAll(new String[]{fossAttribute.getKey()}, fossAttribute.getValue()));
                }
                clearDatastore();

                // Save new data and reload all data to refresh class
                saveDatastore(newDataLines, datastoreLocation);
                processFossData(loadDatastore(datastoreLocation));
            } else{
                throw new MojoExecutionException("The headings in the updated datafile are not compatible with the existing datastore");
            }
        } else {
            throw new MojoExecutionException(String.format("Either no data found in the provided datafile or the file could not be read. (%s)", fileToLoad.toString()));
        }
    }

    private void updateDatastoreData(List<String[]> lines, boolean overrideExisting) {
        if (overrideExisting) {
            for (String[] line : lines) {
                fossAttributes.put(line[0], Arrays.copyOfRange(line, 1, line.length));
            }
        } else {
            mergeFossAttributes(lines);
        }
    }

    private boolean checkHeadingsMatch(String[] storedHeadings, String[] datafileHeadings) {
        if (storedHeadings.length != datafileHeadings.length) {
            logger.error(String.format("The datastore currently has the headings '%s' that does not match with the provided datafiles '%s'", Arrays.toString(storedHeadings), Arrays.toString(datafileHeadings)));
            return false;
        }

        for (int i = 0; i < storedHeadings.length; i++) {
            if (!storedHeadings[i].equals(datafileHeadings[i])) {
                return false;
            }
        }
        return true;
    }

    private void mergeFossAttributes(List<String[]> lines) {
        for (String[] line : lines) {
            if (!fossAttributes.containsKey(line[0])) {
                fossAttributes.put(line[0], Arrays.copyOfRange(line, 1, line.length));
            }
        }
    }

    @Override
    public boolean clearDatastore() throws MojoExecutionException {
        boolean result;
        try {
            result = Files.deleteIfExists(datastoreLocation);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to delete datastore", e);
        }
        headings = new String[0];
        fossAttributes.clear();
        return result;
    }

    @Override
    public boolean isDatastoreEmpty() {
        return fossAttributes.isEmpty();
    }
}