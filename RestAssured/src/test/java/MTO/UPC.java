package MTO;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class UPC {
	
	String[] intData = { "categoryId", "brandId", "gradeId" };
	String[] dataNotToRead = { "Length", "Thickness", "Width", "categoryId", "brandId", "gradeId"};

	List<String> intDataList = Arrays.asList(intData);
	List<String> dataNotToReadList = Arrays.asList(dataNotToRead);

	@DataProvider(name = "excelDataProvider")
	public Object[][] excelDataProvider() throws JSONException, Throwable {
		String excelFilePath = "C:\\Users\\radhe\\Downloads\\upc_jsw.xlsx";
		
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

		given().log().all().header("Content-Type", "application/json") // Adding Content-Type header
				.header("Authorization",
						"eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6dHJ1ZSwibW9iaWxlTnVtYmVyIjoiNzAwMzE0MzQ1NyIsImNvdW50cnlDb2RlIjoiKzkxIiwiZnVsbE5hbWUiOiJBVklHSE5BIiwiaWQiOjUsInVzZXJUeXBlIjoiQURNSU4iLCJlbWFpbCI6ImFkbWluQGFiYWluZm90ZWNoLmNvbSIsImlzRW1haWxWZXJpZmllZCI6ZmFsc2UsInN1YiI6IjcwMDMxNDM0NTciLCJpYXQiOjE3MTIyMjY4OTAsImV4cCI6MTcyMDExNjI5MH0.hRw5f7mzJFfRqdw7i-UtzLxMhAgwdupnEBrskwpQaJQ")
				.body(jsonData)
		        .when()
       		.post("http://localhost:4000/api/v1/product-upc")
       		.then()
				.statusCode(200).assertThat().log().all().extract().asString();

	}
	private String upc_name(Row dataRow, String[] columnHeaders) throws Throwable
	{
		 	String key = "";
			String categoryId =  getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "categoryId"))).toString();
			String brandId =  getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "brandId"))).toString();
			String gradeId =  getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "gradeId"))).toString();
			String shape =  getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "shape"))).toString();
			key = categoryId+brandId+gradeId+shape;
			for (String columnHeader : columnHeaders) {
//				if (columnHeader.startsWith("Length")) {
//					String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Length"))).toString();
//					if (data.endsWith(".0")) {
//						data = data.substring(0, data.length() - 2);
//					}
//					key= key+data;
//				} else 
					if (columnHeader.startsWith("Width")) {
					String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Width"))).toString();
					if (data.endsWith(".0")) {
						data = data.substring(0, data.length() - 2);
					}
					key= key+data;				} else if (columnHeader.startsWith("Thickness")) {
					String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Thickness"))).toString();
					if (data.endsWith(".0")) {
						data = data.substring(0, data.length() - 2);
					}
					key= key+data;				}
			}

	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
			return DatatypeConverter.printHexBinary(hash).toString().substring(0,6);
	}

	private JSONObject createJsonObject(Row dataRow, String[] columnHeaders) throws JSONException, Throwable {
		JSONObject jsonObject = new JSONObject();

		for (int j = 0; j < columnHeaders.length; j++) {
			Cell dataCell = dataRow.getCell(j);
			String columnHeader = columnHeaders[j];
			if (intDataList.contains(columnHeader) && !dataNotToReadList.contains(columnHeader)) {

				jsonObject.put(columnHeader, getCellValue(dataCell));
			} else if (!dataNotToReadList.contains(columnHeader)) {
				jsonObject.put(columnHeader, getCellValue(dataCell).toString());
			}
		}

		JSONObject attributesObject = new JSONObject();
		JSONArray productSpecificationArray = createDynamicProductSpecificationArray(dataRow, columnHeaders);

		attributesObject.put("SPECIFICATION", productSpecificationArray);
		attributesObject.put("CLASSIFICATION", new JSONArray());
		attributesObject.put("GENERALIZATION", new JSONArray());

		jsonObject.put("attributes", attributesObject);
		Object categoryId =  getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "categoryId")));
		Object brandId =  getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "brandId")));
		Object gradeId =  getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "gradeId")));
		JSONObject productObject = createProductObject(categoryId,gradeId,brandId);
		jsonObject.put("product", productObject);
		jsonObject.put("upcCode", "UPC-"+upc_name(dataRow, columnHeaders));
		

		return jsonObject;
	}

	private JSONArray createDynamicProductSpecificationArray(Row dataRow, String[] columnHeaders) {
		JSONArray productSpecificationArray = new JSONArray();
		for (String columnHeader : columnHeaders) {
//			if (columnHeader.startsWith("Length")) {
//				String specificationName = "Length";
//				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Length"))).toString();
//				if (data.endsWith(".0")) {
//					data = data.substring(0, data.length() - 2);
//				}
//				productSpecificationArray.put(createSpecificationObject(specificationName, 1, ""));
//			} else
				if (columnHeader.startsWith("Width")) {
				String specificationName = "Width";
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Width"))).toString();
				if (data.endsWith(".0")) {
					data = data.substring(0, data.length() - 2);
				}
				productSpecificationArray.put(createSpecificationObject(specificationName, 1, data));
			} else if (columnHeader.startsWith("Thickness")) {
				String specificationName = "Thickness";
				String data = getCellValue(dataRow.getCell(getColumnIndex(columnHeaders, "Thickness"))).toString();
				if (data.endsWith(".0")) {
					data = data.substring(0, data.length() - 2);
				}
				productSpecificationArray.put(createSpecificationObject(specificationName, 2, data));
			}
		}

		return productSpecificationArray;
	}
	
	private JSONObject createSpecificationObject(String name, Integer id, String attributeOptions) {
		JSONObject specificationObject = new JSONObject();
		specificationObject.put("name", name);
		specificationObject.put("id", id);
		specificationObject.put("attributeOptions", attributeOptions);
		specificationObject.put("attributeType", "SPECIFICATION");
		specificationObject.put("uom", "MM");
		specificationObject.put("fieldType", "DROPDOWN");
		return specificationObject;
	}
	
	private JSONObject createProductObject(Object categoryId, Object gradeId, Object brandId) {
		JSONObject specificationObject = new JSONObject();
		specificationObject.put("title", "");
		specificationObject.put("categoryId", categoryId);
		specificationObject.put("productCode", "Test Product Code");
		specificationObject.put("brandId", brandId);
		specificationObject.put("keywords", "okk");
		specificationObject.put("gradeId", gradeId);
		specificationObject.put("productProperties", new JSONArray());
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

