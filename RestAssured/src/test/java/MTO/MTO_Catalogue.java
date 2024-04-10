package MTO;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class MTO_Catalogue {
	String[] intData = { "createdBy", "updatedBy" };
	String[] dataNotToRead = { "upcId","Brand", "Standard","Grade", "category","shape"};

	List<String> intDataList = Arrays.asList(intData);
	List<String> dataNotToReadList = Arrays.asList(dataNotToRead);

	@DataProvider(name = "excelDataProvider")
	public Object[][] excelDataProvider() throws IOException {
		String excelFilePath = "C:\\Users\\radhe\\Downloads\\MTO_Catlogue.xlsx";
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
				.post("http://localhost:4000/api/v1/catalogue/")
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
//		JSONArray upcMappingsArray = new JSONArray();
//		upcMappingsArray.put(createPriceObject(dataRow, columnHeaders));

		jsonObject.put("upcMappings", createPriceObjects(dataRow, columnHeaders));
		jsonObject.put("name", createProductName(dataRow, columnHeaders));
		jsonObject.put("description", createProductName(dataRow, columnHeaders));
		jsonObject.put("catalogueType", "MAKE_TO_ORDER" );
		jsonObject.put("category", "STANDARD" );

		return jsonObject;
	}

	private JSONArray createDynamicProductSpecificationArray(Row dataRow, String[] columnHeaders) {
		JSONArray productSpecificationArray = new JSONArray();
		productSpecificationArray.put("PLASTIC_PACKAGING");
		return productSpecificationArray;
	}

	private String createProductName(Row dataRow, String[] columnHeaders) {
		String name = "";
		String brand = "";
		String category = "";
		String shape = "";
		String standard = "";
		String grade = "";
		
		for (String columnHeader : columnHeaders) {
			if (columnHeader.startsWith("Brand")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Brand"))).toString();
				brand = data;
			} else if (columnHeader.startsWith("category")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "category"))).toString();
				category = data;
			} else if (columnHeader.startsWith("shape")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "shape"))).toString();
				shape = data;
			}else if (columnHeader.startsWith("Standard")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Standard"))).toString();
				standard = data;
			}else if (columnHeader.startsWith("Grade")) {
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Grade"))).toString();
				grade = data;
			}
		}
		name= brand+" "+category+" "+shape+" "+standard+" "+grade;

		return name;
	}

	private List<JSONObject> createPriceObjects(Row dataRow, String[] columnHeaders) {
	    List<JSONObject> specificationObjects = new ArrayList<>();
	    
	    String upcIdsString = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "upcId"))).toString();
		if (upcIdsString.endsWith(".0")) {
			upcIdsString = upcIdsString.substring(0, upcIdsString.length() - 2);
		}

	    String[] upcIds = upcIdsString.split(" , ");
	    
	    for (String upcId : upcIds) {
	        JSONObject specificationObject = new JSONObject();
	        specificationObject.put("upcId", upcId);
	        specificationObject.put("upcPrice", 80000);
	        specificationObject.put("minimumSellingPrice", 75000);
	        specificationObject.put("profitMargin", 0);
	        specificationObject.put("status", "ACTIVE");
	        specificationObject.put("minimumOrderQuantity", 10);
	        specificationObjects.add(specificationObject);
	    }
	    
	    return specificationObjects;
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
