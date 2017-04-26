package am.davsoft.propman.helpers;

import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * @author David Shahbazyan
 * @since Apr 19, 2017
 */
public class ExcelProcessorTest {
    File out;

    @Test
    public void testWritePropertiesIntoExcel() throws Exception {
        Map<String, String> props = new HashMap<>();
        props.put("key1", "val1");
        props.put("key2", "val2");
        props.put("key3", "val3");
        props.put("key4", "val4");
        props.put("key5", "val5");
        props.put("key6", "val6");
        props.put("key7", "val7");

        out = new File("out.xls");
        ExcelProcessor.writePropertiesIntoExcel(props, out);

        Map<String, String> loadedProps = ExcelProcessor.loadPropertiesFromExcel(out);

        assertEquals(props.size(), loadedProps.size());

        for (Map.Entry<String, String> entry : loadedProps.entrySet()) {
            assertTrue(props.entrySet().contains(entry), String.format("No entry with key:%s and val:%s was found.", entry.getKey(), entry.getValue()));
        }
    }

    @AfterClass
    public void cleanupTempData() throws IOException {
        if (out != null) {
            Files.deleteIfExists(out.toPath());
        }
    }

}