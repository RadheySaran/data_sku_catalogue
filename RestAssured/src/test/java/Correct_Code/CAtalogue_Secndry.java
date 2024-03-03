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

public class CAtalogue_Secndry {
	String[] intData = { "secondaryProductId" };
	String[] dataNotToRead = {"catalogueName","THICK", "WIDTH", "SHAPE", "TEMPER",};

	List<String> intDataList = Arrays.asList(intData);
	List<String> dataNotToReadList = Arrays.asList(dataNotToRead);

	@DataProvider(name = "excelDataProvider")
	public Object[][] excelDataProvider() throws IOException {
		String excelFilePath = "C:\\Users\\radhe\\OneDrive\\Documents\\cat.xlsx";
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


		  given()
		  .log().all()
	     .header("Content-Type", "application/json") // Adding Content-Type header
	     .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6dHJ1ZSwibW9iaWxlTn"
	     		+ "VtYmVyIjoiNzAwMzE0MzQ1NyIsImNvdW50cnlDb2RlIjoiKzkxIiwiZnVsbE5hbWUiOiJBVklH"
	     		+ "SE5BIiwiaWQiOjQwLCJ1c2VyVHlwZSI6IkFETUlOIiwiZW1haWwiOiJhZG1pbkBhYmFpbmZvdGVj"
	     		+ "aC5jb20iLCJpc0VtYWlsVmVyaWZpZWQiOmZhbHNlLCJzdWIiOiI3MDAzMTQzNDU3IiwiaWF0Ijox"
	     		+ "NzA3MjE2NzA0LCJleHAiOjE3MTUxMDYxMDR9.gmDfWg9zHygjlA52V-1P4n_j7cej0f08cj2OnefxoAM")
	     .body(jsonData)
	     .when()
	     .post("https://admin-api.avighnasteel.in/api/v1/secondary-catalogue")
	   //  .post("https://admin-api.avighnasteel.in/api/v1/secondary-catalogue")
	   //  .post("https://admin-api.avighnasteel.in/api/v1/secondary-catalogue")
	     .then() 	   
		  .statusCode(200)
		  .assertThat()
		  .log().all()
		  .extract().asString();

	}

	private JSONObject createJsonObject(Row dataRow, String[] columnHeaders) {
		JSONObject jsonObject = new JSONObject();

		for (int j = 0; j < columnHeaders.length; j++) {
			Cell dataCell = dataRow.getCell(j);
			String columnHeader = columnHeaders[j];
			if (intDataList.contains(columnHeader) && !dataNotToReadList.contains(columnHeader)) {

				jsonObject.put(columnHeader, getCellValue(dataCell));
			} else if (!dataNotToReadList.contains(columnHeader)) {

				if (columnHeader.equals("defectDetails")) {

					String data = "<p>" + getCellValue(dataCell).toString() + "</p>";
					jsonObject.put(columnHeader, data);
				} else if (columnHeader.equals("availableQuantity")) {
					 String data =
					 Double.toString(Double.parseDouble(getCellValue(dataCell).toString()) /
					 1000);
					jsonObject.put(columnHeader, data);
				} else {
					String data = getCellValue(dataCell).toString();
					if (data.endsWith(".0")) {
						data = data.substring(0, data.length() - 2);
					}
					jsonObject.put(columnHeader, data);
				}

			}
		}

		JSONArray productSpecificationArray = createDynamicProductSpecificationArray(dataRow, columnHeaders);

		jsonObject.put("packagingType", productSpecificationArray);

		jsonObject.put("price", createPriceObject());
		jsonObject.put("catalogueName", createProductName(dataRow, columnHeaders));

		return jsonObject;
	}

	private JSONArray createDynamicProductSpecificationArray(Row dataRow, String[] columnHeaders) {
		JSONArray productSpecificationArray = new JSONArray();
		productSpecificationArray.put("PLASTIC_PACKAGING");
		productSpecificationArray.put("WITHOUT_PACKAGING");
		productSpecificationArray.put("METAL_PACKAGING");

		return productSpecificationArray;
	}

	private String createProductName(Row dataRow, String[] columnHeaders) {
		String name = "";
		for (String columnHeader : columnHeaders) {
			if (columnHeader.startsWith("catalogueName")) {
				name = name + getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "catalogueName"))).toString();
			}else if (columnHeader.startsWith("SHAPE")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "SHAPE"))).toString();
				name = name + " " + data + " ";
			} else if (columnHeader.startsWith("THICK")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "THICK"))).toString();
				name = name + "[Th-" + data + "]";
			} else if (columnHeader.startsWith("WIDTH")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "WIDTH"))).toString();
				name = name + "[W-" + data + "]";
			} else if (columnHeader.startsWith("TEMPER")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "TEMPER"))).toString();
				name=name+"[TM-"+data+"]";			}
		}

		return name;
	}

	private JSONObject createPriceObject() {
		JSONObject specificationObject = new JSONObject();
		specificationObject.put("default", "75000");
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
