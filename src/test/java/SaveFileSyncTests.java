import com.github.nitrogen2oxygen.savefilesync.client.ClientData;
import com.github.nitrogen2oxygen.savefilesync.client.save.Save;
import com.github.nitrogen2oxygen.savefilesync.server.DataServer;
import com.github.nitrogen2oxygen.savefilesync.server.ServerType;
import com.github.nitrogen2oxygen.savefilesync.util.DataManager;
import com.github.nitrogen2oxygen.savefilesync.util.DataServers;
import com.github.nitrogen2oxygen.savefilesync.util.FileLocations;
import com.github.nitrogen2oxygen.savefilesync.util.Saves;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SaveFileSyncTests {
    public static ClientData data;
    public static File dataFolder;

    @Test
    @BeforeAll
    @DisplayName("Preparing the test environment")
    public static void setupData() throws IOException {
        dataFolder = Files.createTempDirectory("SaveFileSyncTest").toFile();
        data = DataManager.load(dataFolder.getPath());
    }

    @Test
    @DisplayName("Test the main client data object works as intended")
    public void testData() throws Exception {
        // Check if the data folder exists
        File dataDirectory = new File(dataFolder.getPath());
        assert dataDirectory.exists() && dataDirectory.isDirectory();

        // Make a test save
        File tmpFile = Files.createTempFile("SaveFileSyncTest", ".tmp").toFile();
        tmpFile.deleteOnExit();
        Save save = Saves.build(false, "TestSave", tmpFile);
        data.addSave(save);
        assert data.getSave("TestSave") == save;
        data.removeSave("TestSave");
        assert data.getSave("TestSave") == null;

        // Does serialization work correct
        DataManager.save(data, dataFolder.getPath());
        File serverFile = new File(FileLocations.getServerFile(dataFolder.getPath()));
        assert serverFile.exists();
        File settingsFile = new File(FileLocations.getConfigFile(dataFolder.getPath()));
        assert settingsFile.exists();
        File savesDirectory = new File(FileLocations.getSaveDirectory(dataFolder.getPath()));
        assert savesDirectory.exists();
    }

    @Test
    @DisplayName("Ensure many functions related to local save files work")
    public void testSaveFiles() throws Exception {
        // Create a few test save files
        File testFile1 = Files.createTempFile("SaveFileSyncTest1", ".tmp").toFile();
        File testFile2 = Files.createTempDirectory("SaveFileSyncTest2").toFile();
        File testFile3 = Files.createTempFile("SaveFileSyncTest3", ".tmp").toFile();
        testFile1.deleteOnExit();
        testFile2.deleteOnExit();
        testFile3.deleteOnExit();

        // Write data to files
        FileUtils.write(testFile1, "Testing testing 123!", StandardCharsets.UTF_8);
        FileUtils.write(testFile3, "Testing testing 456!!!", StandardCharsets.UTF_8);

        // Create save instances
        Save save1 = Saves.build(false, "Test 1", testFile1);
        Save save2 = Saves.build(true, "Test 2", testFile2);

        // Load files to data
        data.addSave(save1);
        data.addSave(save2);

        // Make sure data manager doesn't take bad saves
        Save badSave1 = Saves.build(false, "Bad Test 1", testFile1);
        Save badSave2 = Saves.build(false, "Test 1", testFile3);
        Save badSave3 = Saves.build(true,"Bad Test 3", testFile1.getParentFile());

        Assertions.assertThrows(Exception.class, () -> data.addSave(badSave1));
        Assertions.assertThrows(Exception.class, () -> data.addSave(badSave2));
        Assertions.assertThrows(Exception.class, () -> data.addSave(badSave3));

        // Check if building saves verifies the file type
        Assertions.assertThrows(Exception.class, () -> Saves.build(true, "Bad Test 4", testFile1));
        Assertions.assertThrows(Exception.class, () -> Saves.build(false, "Bad Test 5", testFile2));

        // Checking the same with json objects
        JSONObject testObject1 = new JSONObject();
        JSONObject testObject2 = new JSONObject();
        Assertions.assertThrows(Exception.class, () -> Saves.buildFromJSON(testObject1));

        testObject1.put("name", "Bad Test 6");
        testObject1.put("location", testFile1.getPath());
        Saves.buildFromJSON(testObject1);
        testObject1.put("type", "directory");
        Assertions.assertThrows(Exception.class, () -> Saves.buildFromJSON(testObject1));

        testObject2.put("name", "Bad Test 7");
        testObject2.put("location", testFile2.getPath());
        Saves.buildFromJSON(testObject2);
        testObject2.put("type", "file");
        Assertions.assertThrows(Exception.class, () -> Saves.buildFromJSON(testObject2));


        // Test zip file creation
        File zipFile = Files.createTempFile("SaveFileSyncTest1", ".tmp.zip").toFile();
        zipFile.deleteOnExit();
        FileUtils.writeByteArrayToFile(zipFile, save1.toZipData());
        ZipFile zip = new ZipFile(zipFile);

        // Test zip file extraction
        ZipEntry entry = zip.entries().nextElement();
        assert entry.getName().equals(testFile1.getName());
    }

    @Test
    @DisplayName("Test if all server types return valid instances")
    public void testServers() {
        /* We cannot test actual server integrations from unit tests. Those must be done manually */
        for (ServerType type : ServerType.values()) {
            DataServer testServer = DataServers.buildServer(type);
            if (type == ServerType.NONE) {
                assert testServer == null;
            } else {
                assert testServer != null;
            }
        }
    }

    @Test
    @DisplayName("Ensure the settings set up correctly and handle errors")
    public void testSettings() {

    }

    @Test
    @AfterAll
    @DisplayName("Cleans up the test data folder")
    public static void cleanupTests() throws IOException {
        FileUtils.cleanDirectory(dataFolder);
        FileUtils.deleteDirectory(dataFolder);
    }

}
