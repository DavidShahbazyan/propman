package am.davsoft.propman.controllers;

import am.davsoft.propman.helpers.TaskStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ResourceBundle;

/**
 * @author David Shahbazyan
 * @since Apr 20, 2017
 */
public class MainController implements Initializable {
    @FXML private TextField txtExcelPath, txtPropertiesPath;
    @FXML private Label lblStatus, lblProgressPercents;
    @FXML private ProgressBar pbProgress;
    @FXML private HBox hbProgressPercentsBlock;
    @FXML private Hyperlink hlClearExcelSelection, hlClearPropertiesSelection;

    private Stage currentStage;

    private final ObjectProperty<TaskStatus> taskStatus = new SimpleObjectProperty<>();
    private final FileChooser.ExtensionFilter excelExtensions = new FileChooser.ExtensionFilter("Microsoft Excel Sheet", "*.xls", "*.xlsx");
    private final FileChooser.ExtensionFilter propsExtensions = new FileChooser.ExtensionFilter(".properties", "*.properties");
    private final ObjectProperty<File> excelFile = new SimpleObjectProperty<>();
    private final ObjectProperty<File> propertiesFile = new SimpleObjectProperty<>();
    private FileChooser fileChooser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        excelFile.addListener((observable, oldValue, newValue) -> txtExcelPath.setText(newValue != null ? newValue.getPath() : ""));
        propertiesFile.addListener((observable, oldValue, newValue) -> txtPropertiesPath.setText(newValue != null ? newValue.getPath() : ""));
        taskStatus.addListener((observable, oldValue, newValue) -> lblStatus.setText(newValue.getMessage()));
        taskStatus.setValue(TaskStatus.READY);
        hlClearExcelSelection.visibleProperty().bind(excelFile.isNotNull());
        hlClearExcelSelection.managedProperty().bind(excelFile.isNotNull());
        hlClearPropertiesSelection.visibleProperty().bind(propertiesFile.isNotNull());
        hlClearPropertiesSelection.managedProperty().bind(propertiesFile.isNotNull());
        hbProgressPercentsBlock.visibleProperty().bind(pbProgress.visibleProperty());
        hbProgressPercentsBlock.managedProperty().bind(pbProgress.managedProperty());
        pbProgress.progressProperty().addListener((observable, oldValue, newValue) -> lblProgressPercents.setText(String.valueOf(Math.round(100 * newValue.doubleValue()))));
//        lblProgressPercents.textProperty().bind(pbProgress.progressProperty().asString());

        hideProgressBar();
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    @FXML
    public void onBrowseExcelAction(ActionEvent event) {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(excelExtensions);
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            excelFile.setValue(file);
        }
    }

    @FXML
    public void onClearExcelSelectionAction(ActionEvent event) {
        excelFile.setValue(null);
    }

    @FXML
    public void onNewExcelAction(ActionEvent event) {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(excelExtensions);
        File file = fileChooser.showSaveDialog(currentStage);
        if (file != null) {
            excelFile.setValue(file);
        }
    }

    @FXML
    public void onBrowsePropertiesAction(ActionEvent event) {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(propsExtensions);
        File file = fileChooser.showOpenDialog(currentStage);
        if (file != null) {
            propertiesFile.set(file);
        }
    }

    @FXML
    public void onClearPropertiesSelectionAction(ActionEvent event) {
        propertiesFile.setValue(null);
    }

    @FXML
    public void onNewPropertiesAction(ActionEvent event) {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(propsExtensions);
        File file = fileChooser.showSaveDialog(currentStage);
        if (file != null) {
            propertiesFile.set(file);
        }
    }

    @FXML
    public void exportToExcelAction(ActionEvent event) {
        propertiesToExcel(propertiesFile.getValue(), excelFile.getValue());
    }

    @FXML
    public void importToPropertiesAction(ActionEvent event) {
        excelToProperties(excelFile.getValue(), propertiesFile.getValue());
    }

    @FXML
    public void updatePropertiesAction(ActionEvent event) {
        excelToProperties(excelFile.getValue(), propertiesFile.getValue());
    }

    private void propertiesToExcel(File properties, File excel) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    PropertiesConfiguration config = new PropertiesConfiguration();
                    config.setDelimiterParsingDisabled(true);
                    PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(config);
                    layout.load(new InputStreamReader(new FileInputStream(properties), Charset.forName("UTF-8")));

                    Workbook book = new SXSSFWorkbook();
                    Sheet sheet = book.createSheet("Sheet1");

                    int rowIndex = sheet.getLastRowNum();
                    int done = 0;
                    int todo = layout.getKeys().size();
                    for (String key : layout.getKeys()) {
                        Row row = sheet.createRow(rowIndex);

                        Cell keyCell = row.createCell(0);
                        keyCell.setCellValue(key);

                        Cell valCell = row.createCell(1);
                        valCell.setCellValue(config.getString(key));

                        sheet.autoSizeColumn(0);
                        sheet.autoSizeColumn(1);

                        rowIndex++;
                        updateProgress(++done, todo);
                    }

                    book.write(new FileOutputStream(excel));
                    book.close();
                } catch (ConfigurationException | IOException e) {
                    Logger.getLogger(getClass()).error("Error occurred in propertiesToExcel method: ", e);
                }
                return null;
            }

            @Override
            protected void running() {
                super.running();
                taskStatus.setValue(TaskStatus.PROCESSING);
                showProgressBar(this);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                taskStatus.setValue(TaskStatus.READY);
                hideProgressBar();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                taskStatus.setValue(TaskStatus.CANCELED);
                hideProgressBar();
            }

            @Override
            protected void failed() {
                super.failed();
                taskStatus.setValue(TaskStatus.FAILED);
                hideProgressBar();
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void excelToProperties(File excel, File properties) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    PropertiesConfiguration config = new PropertiesConfiguration();
                    config.setDelimiterParsingDisabled(true);
                    PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(config);
                    if (!properties.exists()) {
                        Files.createFile(properties.toPath());
                    }
                    layout.load(new InputStreamReader(new FileInputStream(properties), Charset.forName("UTF-8")));

                    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(excel));
                    HSSFWorkbook wb = new HSSFWorkbook(fs);
                    HSSFSheet sheet = wb.getSheetAt(0);

                    int rows = sheet.getLastRowNum();
                    int done = 0;

                    for (Row row : sheet) {
                        if (row != null) {
                            String key = row.getCell(0).getStringCellValue();
                            String val = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                            if (key != null && !key.isEmpty()) {
                                config.setProperty(key, val);
                            }
                        }
                        updateProgress(++done, rows);
                    }

                    layout.save(new OutputStreamWriter(new FileOutputStream(properties), Charset.forName("UTF-8")));
                } catch (ConfigurationException | IOException e) {
                    Logger.getLogger(getClass()).error("Error occurred in excelToProperties method: ", e);
                }
                return null;
            }

            @Override
            protected void running() {
                super.running();
                taskStatus.setValue(TaskStatus.PROCESSING);
                showProgressBar(this);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                taskStatus.setValue(TaskStatus.READY);
                hideProgressBar();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                taskStatus.setValue(TaskStatus.CANCELED);
                hideProgressBar();
            }

            @Override
            protected void failed() {
                super.failed();
                taskStatus.setValue(TaskStatus.FAILED);
                hideProgressBar();
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showProgressBar(Task task) {
        pbProgress.progressProperty().bind(task.progressProperty());
        pbProgress.setVisible(true);
    }

    private void hideProgressBar() {
        pbProgress.setVisible(false);
        pbProgress.progressProperty().unbind();
    }
}
