package com.mcstarrysky.starrytown.util.inventory;

import lombok.NonNull;

import java.io.File;

public interface FileSaved {
    File getFile();

    void setFile(@NonNull File file);

    void load(@NonNull File file);

    String getFilename();
}
