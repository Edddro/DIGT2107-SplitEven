package splitseven.storage;

import splitseven.model.AppData;

import java.io.IOException;

/** The only way the rest of the app reads or writes persistent data. */
public interface Storage {
    AppData load() throws IOException;

    void save(AppData data) throws IOException;
}
