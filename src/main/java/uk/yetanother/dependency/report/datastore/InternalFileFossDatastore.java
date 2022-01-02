package uk.yetanother.dependency.report.datastore;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class InternalFileFossDatastore implements IFossDatastore {

    private static final Path DEFAULT_DATASTORE_LOCATION = Paths.get("fossAdditionalAttributes.csv");
    private static final String ID_COL_NAME = "id";

    private final Log logger;
    private final Map<String, String[]> fossAttributes = new HashMap<>();
    private String[] headings = new String[0];

    public InternalFileFossDatastore(Log logger) {
        this.logger = logger;
        if (Files.exists(DEFAULT_DATASTORE_LOCATION)) {
            processFossData(loadDatastore(DEFAULT_DATASTORE_LOCATION));
        }
    }

    private List<String[]> loadDatastore(Path filePath) {
        try (Reader reader = Files.newBufferedReader(filePath); CSVReader csvReader = new CSVReader(reader)) {
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            logger.error(e);
        }
        return Collections.emptyList();
    }

    public void saveDatastore(List<String[]> stringArray, Path path) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(path.toString()));
            writer.writeAll(stringArray);
            writer.close();
        } catch (IOException e) {
            logger.error(e);
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
    public void createDatastore(Path fileToLoad) {
        List<String[]> lines = loadDatastore(fileToLoad);
        if (lines.size() > 1) {
            clearDatastore();
            saveDatastore(lines, DEFAULT_DATASTORE_LOCATION);
            processFossData(lines);
        } else {
            logger.error(String.format("Either no data found in the provided datafile or the file could not be read. (%s)", fileToLoad.toString()));
        }
    }

    @Override
    public void updateDatastore(Path fileToLoad, boolean overrideExisting) {
        // If the datastore is empty just redirect to the create process.
        if (isDatastoreEmpty()) {
            createDatastore(fileToLoad);
            return;
        }

        List<String[]> lines = loadDatastore(fileToLoad);
        if (lines.size() > 1) {
            if (checkHeadingsMatch(headings, Arrays.copyOfRange(lines.get(0), 1, lines.get(0).length))) {
                lines.remove(0);
                if (overrideExisting) {
                    for (String[] line : lines) {
                        fossAttributes.put(line[0], Arrays.copyOfRange(line, 1, line.length));
                    }
                } else {
                    mergeFossAttributes(lines);
                }

                List<String[]> newDataLines = new ArrayList<>();
                newDataLines.add(ArrayUtils.addAll(new String[]{ID_COL_NAME}, headings));
                for (Map.Entry<String, String[]> fossAttribute : fossAttributes.entrySet()) {
                    newDataLines.add(ArrayUtils.addAll(new String[]{fossAttribute.getKey()}, fossAttribute.getValue()));
                }
                clearDatastore();

                // Save new data and reload all data to refresh class
                saveDatastore(newDataLines, DEFAULT_DATASTORE_LOCATION);
                processFossData(loadDatastore(DEFAULT_DATASTORE_LOCATION));
            }
        } else {
            logger.error(String.format("Either no data found in the provided datafile or the file could not be read. (%s)", fileToLoad.toString()));
        }
    }

    private boolean checkHeadingsMatch(String[] storedHeadings, String[] datafileHeadings) {
        if (storedHeadings.length != datafileHeadings.length) {
            logger.error(String.format("The datastore currently has the headings '%s' that does not match with the provided datafiles '%s'", Arrays.toString(storedHeadings), Arrays.toString(datafileHeadings)));
            return false;
        }

        for (int i = 0; i < storedHeadings.length - 1; i++) {
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
    public boolean clearDatastore() {
        boolean result = false;
        try {
            result = Files.deleteIfExists(DEFAULT_DATASTORE_LOCATION);
        } catch (IOException e) {
            logger.error(e);
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