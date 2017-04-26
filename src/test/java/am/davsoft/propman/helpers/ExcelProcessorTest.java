package am.davsoft.propman.helpers;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * @author David Shahbazyan
 * @since Apr 19, 2017
 */
public class ExcelProcessorTest {

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

        File out = new File("/FileArchive/projects/Java/propman/out.xls");
        ExcelProcessor.writePropertiesIntoExcel(props, out);
    }

}