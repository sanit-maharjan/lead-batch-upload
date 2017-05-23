package hello;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;

public class ExcelWriter implements ItemWriter<PersonDto>, ItemStreamWriter<PersonDto> {

	private String[] HEADERS;
	private String outputFilename;
	private Workbook workbook;
	private CellStyle dataCellStyle;
	private int currRow = 0;

	public ExcelWriter(String outputFilename, String headers) {
		super();
		this.outputFilename = outputFilename;
		this.HEADERS = headers.split(",");
	}

	private void addHeaders(Sheet sheet) {

		Workbook wb = sheet.getWorkbook();

		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();

		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);

		Row row = sheet.createRow(0);
		int col = 0;
		for (String header : HEADERS) {
			Cell cell = row.createCell(col);
			cell.setCellValue(header);
			cell.setCellStyle(style);
			col++;
		}

	}

	private void addTitleToSheet(Sheet sheet) {

		Workbook wb = sheet.getWorkbook();

		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();

		font.setFontHeightInPoints((short) 14);
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);

		Row row = sheet.createRow(currRow);
		row.setHeightInPoints(16);
		currRow++;

	}

	@AfterStep
	public void afterStep(StepExecution stepExecution) throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFilename);
		workbook.write(fos);
		fos.close();
		System.out.println("AAAAAAAAAAAAAAAAAafer step");
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("BEEEEFFFORE step");
		outputFilename =  outputFilename + new Date().getTime() + ".xlsx";

		workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet("Testing");
		// sheet.createFreezePane(0, 2, 0, 1);
		sheet.setDefaultColumnWidth(20);

		addTitleToSheet(sheet);
		addHeaders(sheet);
		initDataStyle();

	}

	private void initDataStyle() {
		dataCellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();

		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
		dataCellStyle.setFont(font);
	}

	@Override
	public void write(List<? extends PersonDto> items) throws Exception {

		Sheet sheet = workbook.getSheetAt(0);

		for (PersonDto data : items) {
			Map<String, String> dataToWrite = getRowData(data);
			Row row = sheet.createRow(currRow);
			int column = 0;
			for (String header : HEADERS) {
				createStringCell(row, dataToWrite.get(header), column++);
			}			
			currRow++;
		}
	}

	private void createStringCell(Row row, String val, int col) {
		Cell cell = row.createCell(col);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(val);
	}

	private void createNumericCell(Row row, Double val, int col) {
		Cell cell = row.createCell(col);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(val);
	}
	
	public Map<String, String> getRowData(PersonDto data) {
		Map<String, String> row = new HashMap<>();
		for (String header : HEADERS) {
			switch (header) {
			case "firstName":
				row.put("firstName", data.getFirstName());
				break;
			case "lastName":
				row.put("lastName", data.getLastName());
				break;
			case "email":
				row.put("email", data.getEmail());
				break;
			case "gender":
				row.put("gender", data.getGender());
				break;
			case "company":
				row.put("company", data.getCompany());
				break;
			case "position":
				row.put("position", data.getPosition());
				break;
			case "phone":
				row.put("phone", data.getPhone());
				break;
			case "address":
				row.put("address", data.getAddress());
				break;			
			default:
				break;
			}			
		}
		return row;
	}

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		
	/*	outputFilename =  outputFilename + new Date().getTime() + ".xlsx";

		workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet("Testing");
		// sheet.createFreezePane(0, 2, 0, 1);
		sheet.setDefaultColumnWidth(20);

		addTitleToSheet(sheet);
		addHeaders(sheet);
		initDataStyle();*/

	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws ItemStreamException {
		// TODO Auto-generated method stub
		
	}

}