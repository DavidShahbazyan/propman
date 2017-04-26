package am.davsoft.propman.helpers;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author David Shahbazyan
 * @since Apr 19, 2017
 */
public class ExcelProcessor {
    public static void writePropertiesIntoExcel(Map<String, String> properties, File out) throws FileNotFoundException, IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("MyTestSheetName");

        int rowIndex = sheet.getLastRowNum();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Row row = sheet.createRow(rowIndex);

            Cell key = row.createCell(0);
            key.setCellValue(entry.getKey());

            Cell val = row.createCell(1);
            val.setCellValue(entry.getValue());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            rowIndex++;
        }

        book.write(new FileOutputStream(out));
        book.close();
    }

    public static Map<String, String> loadPropertiesFromExcel(File in) throws FileNotFoundException, IOException {
        Map<String, String> retVal = new LinkedHashMap<>();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(in));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;

            int rows = sheet.getPhysicalNumberOfRows();

//            int cols = 0; // No of columns
//            int tmp = 0;

            // This trick ensures that we get the data properly even if it doesn't start from first few rows
//            for (int i = 0; i < 10 || i < rows; i++) {
//                row = sheet.getRow(i);
//                if (row != null) {
//                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
//                    if (tmp > cols) cols = tmp;
//                }
//            }

            for (int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                if (row != null) {
                    retVal.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return retVal;
    }
}
