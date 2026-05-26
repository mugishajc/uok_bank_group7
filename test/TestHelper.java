import storage.DatabaseConnection;
import storage.DatabaseInitializer;

import java.io.File;
import java.io.IOException;

/**
 * Shared setup/teardown for DAO integration tests.
 * Each test class calls setUp() in @Before and tearDown() in @After.
 * An isolated temp-file SQLite database is used so tests never touch
 * the production database in ~/.uokbank/.
 */
public final class TestHelper {

    private static File tempDb;

    private TestHelper() {}

    public static void setUp() throws IOException {
        tempDb = File.createTempFile("uokbank_test_", ".db");
        tempDb.deleteOnExit();
        DatabaseConnection.setTestUrl("jdbc:sqlite:" + tempDb.getAbsolutePath());
        DatabaseInitializer.initForTest();
    }

    public static void tearDown() {
        DatabaseConnection.clearTestUrl();
        if (tempDb != null && tempDb.exists()) {
            tempDb.delete();
        }
        tempDb = null;
    }
}
