package com.balugaq.runtimepylon.config.pack;

import com.balugaq.runtimepylon.util.Debug;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;

@Data
@AllArgsConstructor
@NullMarked
public class Settings {
    private File settingsFolder;
    private PackNamespace namespace;

    public void merge(File settingsFolder) {
        if (!settingsFolder.exists()) {
            try {
                settingsFolder.createNewFile();
            } catch (IOException e) {
                Debug.log(e);
            }
        }
    }
}
