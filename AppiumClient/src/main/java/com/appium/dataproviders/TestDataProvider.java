package com.appium.dataproviders;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;

import com.appium.utils.ExcelUtil;

public class TestDataProvider {
    @DataProvider(name = "ExcelDataSource")
    public static ListIterator<String[]> readExcelData(String fullFileName, String sheetName)
            throws FileNotFoundException, IOException {

        XSSFWorkbook workBook = new XSSFWorkbook(ExcelUtil.getExcelFullPathName(fullFileName));
        XSSFSheet sheet = workBook.getSheet(sheetName);

        Iterator<Row> rowIter = sheet.rowIterator();

        List<String[]> rowHolder = new ArrayList<String[]>();
        while (rowIter.hasNext()) {
            Row row = (Row) rowIter.next();
            Iterator<Cell> cellIter = row.cellIterator();

            List<String> cellHolder = new ArrayList<String>();
            while (cellIter.hasNext()) {
                Cell cell = (Cell) cellIter.next();
                String cellValue = ExcelUtil.parseStringCellValue(workBook, cell);
                cellHolder.add(cellValue);
            }
            rowHolder.add(cellHolder.toArray(new String[cellHolder.size()]));
        }
        return rowHolder.listIterator();
    }

    @DataProvider(name = "ExcelDataSourceByMethod")
    public static ListIterator<Object[]> readExcelDataByMethod(java.lang.reflect.Method m) throws Exception {

        XSSFWorkbook workBook = new XSSFWorkbook(ExcelUtil.getExcelFullPathName(m.getDeclaringClass().getName()));
        XSSFSheet sheet = workBook.getSheetAt(0);

        Iterator<Row> rowIter = sheet.rowIterator();

        List<Object[]> rowHolder = new ArrayList<Object[]>();
        ROW:
        while (rowIter.hasNext()) {
            Row row = (Row) rowIter.next();
            Iterator<Cell> cellIter = row.cellIterator();
            Cell cell = (Cell) cellIter.next();
            String funcValue = (String) ExcelUtil.parseObjectCellValue(workBook, cell);
            // if (m != null && funcValue.equals(m.getName())) {
            Object cellValue;
            if (m != null && m.getName().substring(m.getName().indexOf("_") + 1).equals(funcValue)) {
                List<Object> cellHolder = new ArrayList<Object>();
                while (cellIter.hasNext()) {
                    cell = (Cell) cellIter.next();
                    cellValue = ExcelUtil.parseStringCellValue(workBook, cell);
                    if (cellValue instanceof String) {
                        String cellValueStr = ((String) cellValue);
                        if (cellValue != null && cellValueStr.startsWith("#")) {
                            rowHolder.add(readExcelSheetData(workBook, cellValueStr.substring(1)));
                            continue ROW;
                        }
                    }
                    cellHolder.add(cellValue);
                }
                rowHolder.add(cellHolder.toArray(new Object[cellHolder.size()]));
            }
        }
        if (rowHolder.size() == 0) {
            throw new SkipException("No data was found for function: " + m.getName());
        }
        return rowHolder.listIterator();
    }


    @DataProvider(name = "ExcelDataSheetMapByMethod")
    public static Object[][] readExcelDataSheetMapByMethod(
            java.lang.reflect.Method m) throws Exception {
        try {
            InputStream is = ExcelUtil.getExcelFullPathName(m
                    .getDeclaringClass().getSimpleName());
            XSSFWorkbook workBook = new XSSFWorkbook(is);
            XSSFSheet sheet = workBook.getSheet(m.getName());

            Row headers = sheet.getRow(0);
            int rowCount = sheet.getLastRowNum();

            Object[][] excelMap = new Object[rowCount][];
            Iterator<Row> rowIter = sheet.rowIterator();

            int rowIndex = 0;
            while (rowIter.hasNext()) {
                Row row = (Row) rowIter.next();
                Iterator<Cell> cellIter = row.cellIterator();
                Map<String, String> filtermap = new HashMap<String, String>();
                int index = 0;
                if (rowIndex >= 1) {
                    while (cellIter.hasNext()) {
                        Cell header = headers.getCell(index);
                        Cell cell = (Cell) cellIter.next();
                        String headerCell = parseCellValue(workBook, header,
                                null);
                        String cellValue = parseCellValue(workBook, cell, null);
                        filtermap.put(headerCell, cellValue);
                        index++;
                    }
                    excelMap[rowIndex - 1] = new Object[]{filtermap};
                }
                rowIndex++;
            }
            return excelMap;

        } catch (FileNotFoundException e) {
            System.out.println("Could not read the Excel sheet");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read the Excel sheet");
            e.printStackTrace();
        }
        return null;
    }

    @DataProvider(name = "ExcelDataSheetByMethod")
    public static Object[][] readExcelDataSheetByMethod(java.lang.reflect.Method m) throws Exception {

        XSSFWorkbook workBook = new XSSFWorkbook(ExcelUtil.getExcelFullPathName(m.getDeclaringClass().getName()));
        XSSFSheet sheet = workBook.getSheet(m.getName());

        Row headers = sheet.getRow(0);
        int rowCount = sheet.getLastRowNum();

        Object[][] excelMap = new Object[rowCount][];
        Iterator<Row> rowIter = sheet.rowIterator();

        int rowIndex = 0;
        while (rowIter.hasNext()) {
            Row row = (Row) rowIter.next();
            Iterator<Cell> cellIter = row.cellIterator();
            Map<String, String> filtermap = new HashMap<String, String>();
            int index = 0;
            if (rowIndex >= 1) {
                while (cellIter.hasNext()) {
                    Cell header = headers.getCell(index);
                    Cell cell = (Cell) cellIter.next();
                    String headerCell = ExcelUtil.parseStringCellValue(workBook, header);
                    String cellValue = ExcelUtil.parseStringCellValue(workBook, cell);
                    filtermap.put(headerCell, cellValue);
                    index++;
                }
                excelMap[rowIndex - 1] = new Object[]{filtermap};
            }
            rowIndex++;
        }
        return excelMap;
    }

    private static String[] readExcelSheetData(XSSFWorkbook workBook, String sheetName) {
        XSSFSheet sheet = workBook.getSheet(sheetName);

        Iterator<Row> rowIter = sheet.rowIterator();

        // List<String[]> rowHolder = new ArrayList<String[]>();
        List<String> cellHolder = new ArrayList<String>();
        while (rowIter.hasNext()) {
            Row row = (Row) rowIter.next();
            Iterator<Cell> cellIter = row.cellIterator();

            String rowValue = "";
            while (cellIter.hasNext()) {
                Cell cell = (Cell) cellIter.next();
                String cellValue = ExcelUtil.parseStringCellValue(workBook, cell);
                rowValue = rowValue.equals("") ? cellValue : rowValue + "," + cellValue;
            }
            cellHolder.add(rowValue);
        }
        // rowHolder.add(cellHolder.toArray(new String[cellHolder.size()]));
        // return rowHolder.listIterator();
        return cellHolder.toArray(new String[cellHolder.size()]);
    }


    public static ArrayList<Map<String, String>> readExcelDataSheet(
            String className, String sheetName) throws Exception {
        try {
            InputStream is = ExcelUtil.getExcelFullPathName(className);
            XSSFWorkbook workBook = new XSSFWorkbook(is);
            XSSFSheet sheet = workBook.getSheet(sheetName);
            Row headers = sheet.getRow(0);
            ArrayList<Map<String, String>> excelData = new ArrayList<Map<String, String>>();
            Iterator<Row> rowIter = sheet.rowIterator();
            int rowIndex = 0;
            while (rowIter.hasNext()) {
                Row row = (Row) rowIter.next();
                Map<String, String> filtermap = new HashMap<String, String>();
                if (rowIndex >= 1) {
                    int totalCells = row.getLastCellNum(); // gives no of cells
                    for (int index = 0; index < totalCells; index++) {
                        Cell header = headers.getCell(index);
                        Cell cell = row.getCell(index);
                        String headerCell = parseCellValue(workBook, header,
                                null);
                        String cellValue = parseCellValue(workBook, cell, null);
                        filtermap.put(headerCell, cellValue);
                    }
                    excelData.add(filtermap);
                }
                rowIndex++;
            }
            return excelData;

        } catch (FileNotFoundException e) {
            System.out.println("Could not read the Excel sheet " + className
                    + " " + sheetName);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read the Excel sheet " + className
                    + " " + sheetName);
            e.printStackTrace();
        }
        return null;
    }

    public static void updateExcelDataSheet(String sourcePath,
                                            String sourceSheetName, String targetPath, String targetSheetName)
            throws Exception {
        try {
            InputStream sourceInputStream = ExcelUtil
                    .getExcelFullPathName(sourcePath);
            XSSFWorkbook sourceWorkBook = new XSSFWorkbook(sourceInputStream);

            XSSFSheet sheet = sourceWorkBook.getSheet(sourceSheetName);
            Iterator<Row> rowIter = sheet.rowIterator();
            ArrayList<ArrayList<String>> rowHolder = new ArrayList<ArrayList<String>>();
            while (rowIter.hasNext()) {
                Row row = (Row) rowIter.next();
                Iterator<Cell> cellIter = row.cellIterator();
                ArrayList<String> cellHolder = new ArrayList<String>();
                while (cellIter.hasNext()) {
                    Cell cell = (Cell) cellIter.next();
                    String cellValue = ExcelUtil.parseStringCellValue(
                            sourceWorkBook, cell);
                    cellHolder.add(cellValue);
                }
                rowHolder.add(cellHolder);
            }

            InputStream targetInputStream = ExcelUtil
                    .getExcelFullPathName(targetPath);
            XSSFWorkbook targetWorkBook = new XSSFWorkbook(targetInputStream);

            XSSFSheet targetSheet = targetWorkBook.getSheet(targetSheetName);
            for (int i = 0; i < rowHolder.size(); i++) {
                for (int j = 0; j < rowHolder.get(i).size(); j++) {
                    Row currentRow = targetSheet.getRow(i);
                    if (!rowHolder.get(i).get(j).isEmpty()) {
                        currentRow.getCell(j).setCellValue(
                                rowHolder.get(i).get(j).toString());
                    }
                }
            }
            FileOutputStream fileOut = ExcelUtil.getFileOutputStream(targetPath);
            targetWorkBook.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read the Excel sheet " + sourcePath
                    + " " + sourceSheetName);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read the Excel sheet " + sourcePath
                    + " " + sourceSheetName);
            e.printStackTrace();
        }
    }

    public static void updateExcelDataSheetColumn(String sourcePath,
                                                  String sourceSheetName, String targetPath, String targetSheetName,
                                                  String columnName) throws Exception {
        try {
            InputStream sourceInputStream = ExcelUtil
                    .getExcelFullPathName(sourcePath);
            XSSFWorkbook sourceWorkBook = new XSSFWorkbook(sourceInputStream);

            XSSFSheet sheet = sourceWorkBook.getSheet(sourceSheetName);
            int columnIndex = 0;
            Row header = sheet.getRow(0);
            Iterator<Cell> cellIter = header.cellIterator();
            while (cellIter.hasNext()) {
                Cell headerLabel = cellIter.next();
                if (headerLabel.getStringCellValue().equalsIgnoreCase(
                        columnName)) {
                    columnIndex = headerLabel.getColumnIndex();
                }
            }
            Iterator<Row> rowIter = sheet.rowIterator();
            ArrayList<String> rowHolder = new ArrayList<String>();
            while (rowIter.hasNext()) {
                Row row = (Row) rowIter.next();
                rowHolder.add(row.getCell(columnIndex).getStringCellValue());
            }
            InputStream targetInputStream = ExcelUtil
                    .getExcelFullPathName(targetPath);
            XSSFWorkbook targetWorkBook = new XSSFWorkbook(targetInputStream);

            XSSFSheet targetSheet = targetWorkBook.getSheet(targetSheetName);
            Row targetHeader = targetSheet.getRow(0);
            int targetColumnIndex = 0;
            Iterator<Cell> targetCellIter = targetHeader.cellIterator();
            while (targetCellIter.hasNext()) {
                Cell headerCell = targetCellIter.next();
                if (headerCell.getStringCellValue()
                        .equalsIgnoreCase(columnName)) {
                    targetColumnIndex = headerCell.getColumnIndex();
                }
            }
            for (int i = 0; i < rowHolder.size(); i++) {
                Row currentRow = targetSheet.getRow(i);
                currentRow.getCell(targetColumnIndex).setCellValue(
                        rowHolder.get(i).toString());
            }

            FileOutputStream fileOut = ExcelUtil.getFileOutputStream(targetPath);
            targetWorkBook.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read the Excel sheet " + sourcePath
                    + " " + sourceSheetName);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read the Excel sheet " + targetPath
                    + " " + sourceSheetName);
            e.printStackTrace();
        }
    }

    private static String parseCellValue(Workbook workBook, Cell cell,
                                         Map<String, String> arguments) {
        FormulaEvaluator evaluator = workBook.getCreationHelper()
                .createFormulaEvaluator();
        String cellValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        DateFormat df2 = new SimpleDateFormat();
                        cellValue = df2.format(cell.getDateCellValue()).toString();
                    } else {
                        DecimalFormat df = new DecimalFormat("0.00");
                        cellValue = df.format(
                                new Double(cell.getNumericCellValue())).toString();
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = new Boolean(cell.getBooleanCellValue()).toString();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    cellValue = evaluator.evaluate(cell).formatAsString();
                    cellValue = cellValue.replaceAll("\"", "");
                    break;
            }

            if (cellValue != null) {
                if (cellValue.equals("$empty")) {
                    cellValue = "";
                }
                if (cellValue.startsWith("$")) {
                    for (Entry<String, String> entry : arguments.entrySet()) {
                        if (cellValue.substring(1).equals(entry.getKey())) {
                            cellValue = entry.getValue();
                        }
                    }
                }
                if (cellValue.equals("null")) {
                    cellValue = null;
                }
            }
        }

        return cellValue;
    }

}


