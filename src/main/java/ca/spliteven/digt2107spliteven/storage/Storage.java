package ca.spliteven.digt2107spliteven.storage;

import java.io.IOException;

/** The application uses this interface rather than accessing files directly. */
public interface Storage {
    AppData load() throws IOException;

    void save(AppData appData) throws IOException;
}
