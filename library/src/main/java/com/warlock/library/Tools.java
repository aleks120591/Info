package com.warlock.library;

import android.os.Environment;
import android.util.Log;

import com.warlock.library.root.RootFile;
import com.warlock.library.root.RootUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Warlock Aleks on 16.06.2016.
 */
public class Tools {
    /**
     * Debugging TAG
     */
    public final static String TAG = "KernelAdiutorPlugin";

    /**
     * Get path of external storage
     *
     * @return path of external storage
     */
    public static String getExternalStorage() {
        String path = RootUtils.runCommand("echo ${SECONDARY_STORAGE%%:*}");
        return path.contains("/") ? path : null;
    }

    /**
     * Get path of internal storage
     *
     * @return path of internal storage
     */
    public static String getInternalStorage() {
        String dataPath = existFile("/data/media/0", true) ? "/data/media/0" : "/data/media";
        if (!new RootFile(dataPath).isEmpty()) return dataPath;
        if (existFile("/sdcard", true)) return "/sdcard";
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * Check if a file exists
     *
     * @param file   path to file
     * @param asRoot check as root
     * @return true = file exists, false = file does not exist
     */
    public static boolean existFile(String file, boolean asRoot) {
        if (asRoot) return new RootFile(file).exists();
        return new File(file).exists();
    }

    /**
     * Write a string to any file
     *
     * @param path   path to file
     * @param text   your text
     * @param append append your text to file
     * @param asRoot write as root
     */
    public static void writeFile(String path, String text, boolean append, boolean asRoot) {
        if (asRoot) {
            new RootFile(path).write(text, append);
            return;
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(path, append);
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "Failed to write " + path);
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read any file from storage
     *
     * @param file   path to the file
     * @param asRoot read as root
     * @return content of file
     */
    public static String readFile(String file, boolean asRoot) {
        if (asRoot) return new RootFile(file).readFile();

        StringBuilder s = null;
        FileReader fileReader = null;
        BufferedReader buf = null;
        try {
            fileReader = new FileReader(file);
            buf = new BufferedReader(fileReader);

            String line;
            s = new StringBuilder();
            while ((line = buf.readLine()) != null) s.append(line).append("\n");
        } catch (FileNotFoundException ignored) {
            Log.e(TAG, "File does not exist " + file);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read " + file);
        } finally {
            try {
                if (fileReader != null) fileReader.close();
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s == null ? null : s.toString().trim();
    }
}
