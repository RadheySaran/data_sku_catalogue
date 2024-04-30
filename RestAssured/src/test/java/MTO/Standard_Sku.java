
package MTO;

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

public class Standard_Sku {
	String[] intData = { "upcId", "createdBy", "updatedBy"};
	String[] dataNotToRead = { "THICKNESS", "COLOUR" };

	List<String> intDataList = Arrays.asList(intData);
	List<String> dataNotToReadList = Arrays.asList(dataNotToRead);

	@DataProvider(name = "excelDataProvider")
	public Object[][] excelDataProvider() throws IOException {
		String excelFilePath = "C:\\Users\\radhe\\Downloads\\SKU_10.xlsx";

		FileInputStream inputStream = new FileInputStream(excelFilePath);

		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(0);

		int rowCount = sheet.getPhysicalNumberOfRows();
		int colCount = sheet.getRow(0).getPhysicalNumberOfCells();

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
		System.out.println(jsonData);

//		 Uncomment the rest of the code for actual API request if needed

//		given().log().all().header("Content-Type", "application/json") 
//				.header("Authorization",
//						"eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6dHJ1ZSwibW9iaWxlTnVtYmVyIjoiNz"
//						+ "AwMzE0MzQ1NyIsImNvdW50cnlDb2RlIjoiKzkxIiwiZnVsbE5hbWUiOiJBVklHSE5B"
//						+ "IiwiaWQiOjEsInVzZXJUeXBlIjoiQURNSU4iLCJlbWFpbCI6ImFkbWluQGFiYWluZm9"
//						+ "0ZWNoLmNvbSIsImlzRW1haWxWZXJpZmllZCI6ZmFsc2UsInN1YiI6IjcwMDMxNDM0NTciLCJ"
//						+ "pYXQiOjE3MDcyODE1MDEsImV4cCI6MTcxNTE3MDkwMX0.ZIgyXPhMbxesgSx0av5QbZ4q4L3E"
//						+ "ZfLEbjllZS6SuKo")
//				.body(jsonData)
//		        .when()
//		// .post("http://localhost:4000/api/v1/inventory/") 
//		 .post("https://staging-admin-api.avighnasteel.in/api/v1/inventory/") 
//		 .then()
//		 .statusCode(200)
//		 .assertThat()
//		 .log().all().extract().asString();

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

		return jsonObject;
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
