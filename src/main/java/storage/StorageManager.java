package storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import engine.DoNotPassGoBankerException;

public class StorageManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
    private AppData data;

    private boolean hasUnsavedChanges = false;

    private File lastFile;

    /**
     * Marks the data as changed.
     */
    public void markAsChanged() {
        hasUnsavedChanges = true;
    }

    /**
     * Checks if the data has been changed since the last save.
     * @return True if the data has been changed since the last save. False if not.
     */
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    /**
     * Checks if there is a last file in memory.
     * @return True if there is a last file in memory. False if not.
     */
    public boolean hasLastFile() {
        return lastFile != null;
    }

    /**
     * Saves the data to the given file.
     * @param file File to save to.
     */
    public void saveAs(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
            hasUnsavedChanges = false;
            lastFile = file;
            System.out.println("Successfully saved to: " + file.getAbsolutePath());
        } 
    }

    /**
     * Saves the data to the last file.
     * @throws DoNotPassGoBankerException If there is no last file.
     */
    public void save() throws DoNotPassGoBankerException, IOException {
        if (!hasLastFile()) throw new DoNotPassGoBankerException("Last file not in memory.");
        saveAs(lastFile);
    }

    /**
     * Loads the data from a given file.
     * @param file File to load from.
     * @return The data.
     * @throws DoNotPassGoBankerException If the file is not found.
     * @throws IOException If the file fails to read.
     */
    public AppData load(File file) throws DoNotPassGoBankerException, IOException {
        if (!file.exists()) {
            throw new DoNotPassGoBankerException("File " + file.getAbsolutePath() + " not found.");
        }
        try (FileReader reader = new FileReader(file)) {
            data = gson.fromJson(reader, AppData.class);
            lastFile = file;
            return data;
        }
    }

    /**
     * Sets new data.
     * @param data The data.
     */
    public void setData(AppData data) {
        this.data = data;
    }

    /** 
     * Gets settings.
     * @return Settings.s
     */
    public Settings getSettings() {
        return data.settings;
    }
}
