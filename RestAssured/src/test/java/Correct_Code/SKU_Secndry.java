
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

public class SKU_Secndry {
	String[] intData = { "warehouseId", "productCategoryId", "gradeId" };
	String[] dataNotToRead = { "THICK", "WIDTH", "LENGTH", "TEMPER", "Lot Id", "CATE", "REMARK" };

	List<String> intDataList = Arrays.asList(intData);
	List<String> dataNotToReadList = Arrays.asList(dataNotToRead);

	@DataProvider(name = "excelDataProvider")
	public Object[][] excelDataProvider() throws IOException {
		String excelFilePath = "C:\\Users\\radhe\\OneDrive\\Documents\\ski-01.xlsx";
		
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

//		given().log().all().header("Content-Type", "application/json") // Adding Content-Type header
//				.header("Authorization",
//						"eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6dHJ1ZSwibW9iaWxlTnVtYmVyIjoiNzAwMzE0MzQ1NyIs"
//								+ "ImNvdW50cnlDb2RlIjoiKzkxIiwiZnVsbE5hbWUiOiJBVklHSE5BIiwiaWQiOjQwLCJ1c2VyVHlwZSI6IkFETUlOIiwiZW1haWwiOi"
//								+ "JhZG1pbkBhYmFpbmZvdGVjaC5jb20iLCJpc0VtYWlsVmVyaWZpZWQiOmZhbHNlLCJzdWIiOiI3MDAzMTQzNDU3IiwiaWF0IjoxNzA2N"
//								+ "jEwNTQwLCJleHAiOjE3MTQ0OTk5NDB9.1AKJ5Wt6f1hpFw1PMw4LxlMkT45VnaWpdQf5Gts5z9I")
//				.body(jsonData)
//		        .when()
//       		.post("https://staging-admin-api.avighnasteel.in/api/v1/secondary-product")
//       		.then()
//				.statusCode(200).assertThat().log().all().extract().asString();

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
				} else if (columnHeader.equals("grossWeight") || columnHeader.equals("netWeight")) {
					String data = Double.toString(Double.parseDouble(getCellValue(dataCell).toString()) / 1000);
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

		JSONObject attributesObject = new JSONObject();
		JSONArray productSpecificationArray = createDynamicProductSpecificationArray(dataRow, columnHeaders);

		attributesObject.put("productSpecification", productSpecificationArray);
		attributesObject.put("productClassification", new JSONArray());
		attributesObject.put("productGeneralization", new JSONArray());

		jsonObject.put("attributes", attributesObject);

		return jsonObject;
	}

	private JSONArray createDynamicProductSpecificationArray(Row dataRow, String[] columnHeaders) {
		JSONArray productSpecificationArray = new JSONArray();
		for (String columnHeader : columnHeaders) {
			if (columnHeader.startsWith("LENGTH")) {
				String specificationName = "Length";
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "LENGTH"))).toString();
				if (data.endsWith(".0")) {
					data = data.substring(0, data.length() - 2);
				}
				String minValue = data;
				String maxValue = data;

				productSpecificationArray.put(createSpecificationObject(specificationName, minValue, maxValue));
			} else if (columnHeader.startsWith("WIDTH")) {
				String specificationName = "Width";
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "WIDTH"))).toString();
				if (data.endsWith(".0")) {
					data = data.substring(0, data.length() - 2);
				}
				String minValue = data;
				String maxValue = data;

				productSpecificationArray.put(createSpecificationObject(specificationName, minValue, maxValue));
			} else if (columnHeader.startsWith("THICK")) {
				String specificationName = "Thickness";
				String minValue = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "THICK"))).toString();
				String maxValue = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "THICK"))).toString();

				productSpecificationArray.put(createSpecificationObject(specificationName, minValue, maxValue));
			} else if (columnHeader.startsWith("TEMPER")) {
				String specificationName = "Temper";
				String minValue = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "TEMPER"))).toString();
				String maxValue = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "TEMPER"))).toString();

				productSpecificationArray.put(createSpecificationObject(specificationName, minValue, maxValue));
			}
		}

		return productSpecificationArray;
	}

	private JSONObject createSpecificationObject(String name, String minValue, String maxValue) {
		JSONObject specificationObject = new JSONObject();
		specificationObject.put("name", name);
		specificationObject.put("minValue", minValue);
		specificationObject.put("maxValue", maxValue);
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
