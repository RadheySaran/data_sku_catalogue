package Correct_Code;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class Standard_Cat {
	String[] intData = { "createdBy", "updatedBy" };
	String[] dataNotToRead = { "upcId","THICK", "COLOR","WIDTH"};

	List<String> intDataList = Arrays.asList(intData);
	List<String> dataNotToReadList = Arrays.asList(dataNotToRead);

	@DataProvider(name = "excelDataProvider")
	public Object[][] excelDataProvider() throws IOException {
		String excelFilePath = "C:\\Users\\radhe\\Downloads\\standard_sheet_cat.xlsx";
		FileInputStream inputStream = new FileInputStream(excelFilePath);

		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(0);

		int rowCount = sheet.getPhysicalNumberOfRows();
		int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
		System.out.println(rowCount);
		System.out.println(colCount);
		Object[][] data = new Object[rowCount - 1][1];

		Row headerRow = sheet.getRow(0);
		String[] columnHeaders = new String[colCount];
		for (int j = 0; j < colCount; j++) {
			columnHeaders[j] = headerRow.getCell(j).getStringCellValue();
		}

		for (int i = 1; i < rowCount; i++) {
			Row row = sheet.getRow(i);
			JSONObject jsonObject = createJsonObject(row, columnHeaders);
			data[i - 1][0] = jsonObject.toString();
		}

		workbook.close();
		inputStream.close();

		return data;
	}

	@Test(dataProvider = "excelDataProvider")
	void create(String jsonData) throws Exception {
//		System.out.println(jsonData);

		given().log().all().header("Content-Type", "application/json") // Adding Content-Type header
				.header("Authorization",
						"eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6dHJ1ZSwibW9iaWxl"
						+ "TnVtYmVyIjoiNzAwMzE0MzQ1NyIsImNvdW50cnlDb2RlIjoiKzkx"
						+ "IiwiZnVsbE5hbWUiOiJBVklHSE5BIiwiaWQiOjEsInVzZXJUeXBl"
						+ "IjoiQURNSU4iLCJlbWFpbCI6ImFkbWluQGFiYWluZm90ZWNoLmNvb"
						+ "SIsImlzRW1haWxWZXJpZmllZCI6ZmFsc2UsInN1YiI6IjcwMDMxND"
						+ "M0NTciLCJpYXQiOjE3MDcyODE1MDEsImV4cCI6MTcxNTE3MDkwMX0.Z"
						+ "IgyXPhMbxesgSx0av5QbZ4q4L3EZfLEbjllZS6SuKo"
						)
				.body(jsonData).when()
				.post("https://admin-api.avighnasteel.in/api/v1/catalogue/")
				.then().statusCode(200).assertThat().log().all().extract().asString();

	}

	private JSONObject createJsonObject(Row dataRow, String[] columnHeaders) {
		JSONObject jsonObject = new JSONObject();

		for (int j = 0; j < columnHeaders.length; j++) {
			Cell dataCell = dataRow.getCell(j);
			String columnHeader = columnHeaders[j];
			if (intDataList.contains(columnHeader) && !dataNotToReadList.contains(columnHeader)) {

				jsonObject.put(columnHeader, getCellValue(dataCell));
			} else if (!dataNotToReadList.contains(columnHeader)) {

				String data = getCellValue(dataCell).toString();
				if (data.endsWith(".0")) {
					data = data.substring(0, data.length() - 2);
				}

				jsonObject.put(columnHeader, data);

			}
		}

		JSONArray productSpecificationArray = createDynamicProductSpecificationArray(dataRow, columnHeaders);

		jsonObject.put("packagingTypes", productSpecificationArray);
		JSONArray upcMappingsArray = new JSONArray();
		upcMappingsArray.put(createPriceObject(dataRow, columnHeaders));

		jsonObject.put("upcMappings", upcMappingsArray);
		jsonObject.put("name", createProductName(dataRow, columnHeaders));
		jsonObject.put("description", createProductName(dataRow, columnHeaders));

		return jsonObject;
	}

	private JSONArray createDynamicProductSpecificationArray(Row dataRow, String[] columnHeaders) {
		JSONArray productSpecificationArray = new JSONArray();
		productSpecificationArray.put("PLASTIC_PACKAGING");
		return productSpecificationArray;
	}

	private String createProductName(Row dataRow, String[] columnHeaders) {
		String name = "PPGI IS 14246 CGCC ";
		for (String columnHeader : columnHeaders) {
			if (columnHeader.startsWith("THICK")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "THICK"))).toString()
						.substring(0, 4);
				name = name + "[Th-" + data + "]";
			} else if (columnHeader.startsWith("WIDTH")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "WIDTH"))).toString();
				if (data.endsWith(".0")) {
					data = data.substring(0, data.length() - 2);
				}

				name = name + "[W-" + data + "]";
			} else if (columnHeader.startsWith("COLOR")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "COLOR"))).toString();
				name = name + "[C-" + data + "]";
			}
		}

		return name;
	}

	private JSONObject createPriceObject(Row dataRow, String[] columnHeaders) {
		JSONObject specificationObject = new JSONObject();
		for (String columnHeader : columnHeaders) {
			if (columnHeader.startsWith("upcId")) {
				specificationObject.put("upcId", getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "upcId"))));
			}
		}

		specificationObject.put("upcPrice", 80000);
		specificationObject.put("minimumSellingPrice", 75000);
		specificationObject.put("profitMargin",0);
		specificationObject.put("status", "ACTIVE");
		return specificationObject;
	}

	private int getColumnIndex(String[] columnHeaders, String columnName) {
		for (int i = 0; i < columnHeaders.length; i++) {
			if (columnHeaders[i].equalsIgnoreCase(columnName)) {
				return i;
			}
		}
		return -1;
	}

	private Object getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			return (cell.getNumericCellValue());
		case BOOLEAN:
			return (cell.getBooleanCellValue());
		case BLANK:
			return "";
		default:
			return "";
		}
	}
}
