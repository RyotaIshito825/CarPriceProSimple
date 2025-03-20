package com.techacademy.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Car;
import com.techacademy.entity.Car.Classification;
import com.techacademy.entity.Car.JapaneseCalendar;
import com.techacademy.entity.Employee;
import com.techacademy.entity.PriceCard;
import com.techacademy.service.CarService;
import com.techacademy.service.EmployeeService;
//import com.techacademy.service.PdfGenerationService;
import com.techacademy.service.PriceCardService;

@Controller
@RequestMapping("cars")
public class CarsController {

    private final CarService carService;
    private final PriceCardService priceCardService;
    private final EmployeeService employeeService;
//    private final PdfGenerationService pdfGenerationService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    public CarsController(CarService carService, PriceCardService priceCardService, EmployeeService employeeService) {
        this.carService = carService;
        this.priceCardService = priceCardService;
        this.employeeService = employeeService;
//        this.pdfGenerationService = pdfGenerationService;
    }

    // 車両一覧画面表示
    @GetMapping
    public String top(Model model) {

        List<Car> cars = carService.findAll();
        model.addAttribute("carList", cars);

        PriceCard priceCard = priceCardService.findByEmployeeId(1);
        if (priceCard != null) {
            model.addAttribute("priceCardName", priceCard.getPriceCardName());
        }

        return "cars/list";
    }

    // 車両詳細画面表示
    @GetMapping(value = "/{id}")
    public String detail(@PathVariable Integer id, Model model) {

        Car car = carService.findById(id);
        boolean viExits = String.valueOf(car.getViYear()) == null ? false : true;

        model.addAttribute("viExits", viExits);
        model.addAttribute("car", car);

        return "cars/detail";
    }

    // 車両新規登録
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Car car) {
        return "cars/new";
    }

    // 車両新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Car car, BindingResult res, Model model) {

        if (res.hasErrors()) {
            model.addAttribute("car", car);
            return "cars/new";
        }

        ErrorKinds result = carService.createCar(car);
        if (result == ErrorKinds.SUCCESS) {
            carService.createCarSave(car);
        }
        return "redirect:/cars";
    }

    // 車両更新画面表示
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable Integer id, Model model) {
        Car car = carService.findById(id);
        model.addAttribute("car", car);
        return "cars/edit";
    }

    // 車両更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@Validated Car car, BindingResult res, Model model) {

        if (res.hasErrors()) {
            model.addAttribute("car", car);
            return "cars/edit";
        }

        ErrorKinds result = carService.updateCar(car);
        if (result == ErrorKinds.SUCCESS) {
            carService.updateCarSave(car);
        }

        return "redirect:/cars";
    }

    // 車両削除処理
    @PostMapping(value = "/delete")
    public String delete() {
        return "redirect:/cars";
    }

    // テンプレート一覧画面表示
    @GetMapping(value = "/template")
    public String template() {
        return "cars/template";
    }

    // テンプレート
    @PostMapping(value = "/template")
    public String confirmedTemplate(@RequestParam String priceCardName) {

        PriceCard priceCard = priceCardService.findByEmployeeId(1);
        Employee employee = employeeService.findById(1);
        if (priceCard == null) {
            PriceCard newPriceCard = new PriceCard();
            newPriceCard.setPriceCardName(priceCardName);
            newPriceCard.setEmployee(employee);
            priceCardService.save(newPriceCard);
        } else {
            priceCard.setPriceCardName(priceCardName);
            priceCardService.save(priceCard);
        }
        return "redirect:/cars";
    }

    // データ取込画面表示
    @GetMapping(value = "/intake")
    public String intake() {
        return "cars/intake";
    }

    // データ取込処理
    @PostMapping(value = "/intake")
    public String regi(String url, MultipartFile file, Model model) throws IOException, GeneralSecurityException {

        if (file != null && !file.isEmpty()) {
            registerExcelFileDataVehicle(file);
            if (url.isEmpty()) {
                return "redirect:/cars";
            }
        }

        try {
            if (url.isEmpty()) {
                return "redirect:/cars/intake";
            }

            String spreadSheetId = getSpreadSheetId(url);
            String spreadSheetGid = getSpreadSheetGid(url);
            String sheetName = getSheetNameFromGid(spreadSheetId, spreadSheetGid);
            String range = getDynamicRange(spreadSheetId, sheetName);

            List<List<Object>> rows = getRows(spreadSheetId, range);

            for (List<Object> row : rows) {
                if (row.get(0).equals("") || row.get(1).equals("") || row.get(2).equals("") || row.get(3).equals("")
                        || row.get(4).equals("") || row.get(5).equals("") || row.get(9).equals("")
                        || row.get(10).equals("") || row.get(11).equals("") || row.get(12).equals("")
                        || row.get(13).equals("") || row.get(14).equals("")) {
                    continue;
                }
                Car car = new Car();
                car.setMaker(String.valueOf(row.get(4)));
                car.setCarModel(String.valueOf(row.get(5)));
                car.setGrade(String.valueOf(row.get(9)));
                for (Classification classification : Car.Classification.values()) {
                    if (String.valueOf(classification.getValue()).equals(String.valueOf(row.get(0)))) {
                        car.setClassification(classification);
                    }
                }
                for (JapaneseCalendar japaneseCalendar : Car.JapaneseCalendar.values()) {
                    if (String.valueOf(japaneseCalendar.getValue()).equals(String.valueOf(row.get(1)))) {
                        car.setFirstJc(japaneseCalendar);
                    }
                    if (String.valueOf(japaneseCalendar.getValue()).equals(String.valueOf(row.get(6)))) {
                        car.setViJc(japaneseCalendar);
                    }
                }

                car.setRegistrationYear(Integer.parseInt(String.valueOf(row.get(2))));
                car.setRegistrationMonth(Integer.parseInt(String.valueOf(row.get(3))));
                car.setViYear(Integer.parseInt(String.valueOf(row.get(7))));
                car.setViMonth(Integer.parseInt(String.valueOf(row.get(8))));
                car.setPrice(Integer.parseInt(String.valueOf(row.get(10))));
                car.setPriceDpf(Integer.parseInt(String.valueOf(row.get(11))));
                car.setTotalPrice(Integer.parseInt(String.valueOf(row.get(12))));
                car.setTotalPriceDpf(Integer.parseInt(String.valueOf(row.get(13))));
                car.setMileage(Integer.parseInt(String.valueOf(row.get(14))));

                ErrorKinds result = carService.createCar(car);
                if (result == ErrorKinds.SUCCESS) {
                    carService.createCarSave(car);
                }
            }

            return "redirect:/cars";

        } catch (GoogleJsonResponseException e) { // スプレッドシートの権限エラー処理

            model.addAttribute("error", ErrorMessage.getErrorValue(ErrorKinds.GOOGLESPREADSHEET_AUTHORITY_ERROR));
            return "/cars/intake";
        }
    }

    @PostMapping("/generate-pdf")
    public ResponseEntity<InputStreamResource> generatePdf(Model model) throws IOException {
        System.out.println("test");
        // テンプレートエンジンにデータを渡す
        Context context = new Context();
//        context.setVariable("message", "こんにちは、PDFBox!");

        // HTMLを生成
        String html = templateEngine.process("pricecard1", context);
        System.out.println(html);

        // HTMLをPDFに変換
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.withHtmlContent(html, null);

        builder.useFont(() -> getClass().getResourceAsStream("/fonts/NotoSansJP-Regular.ttf"), "Noto Sans CJK JP");
        builder.toStream(outputStream);
        builder.run();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=example.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

//    @PostMapping("/generateAndSave")
//    @ResponseBody
//    public String generateAndSavePdf(String priceCardName, Model model) throws Exception {
//
//        if (priceCardName == null) {
//            return "redirect:/cars";
//        }
//
//        System.out.println(priceCardName);
//        String priceCardNameNum = priceCardName.substring(priceCardName.lastIndexOf("-") + 1);
//        System.out.println(priceCardNameNum);
//
////        model.addAttribute("maker", "ダイハツ");
//
//        byte[] pdfContent = pdfGenerationService.generatePdfFromTemplate("pricecard1.html", model);
//        String outputPath = "/path/to/save/directory/" + priceCardName + ".pdf";
//
//        pdfGenerationService.savePdfToFile(pdfContent, outputPath);
//
//
//
//
//
//        return "redirect:/cars";
//
////        return "/pricecards/pricecard" + priceCardNameNum;
//    }

    // GoogleSpreadSheetのURLからIDを取得
    public static String getSpreadSheetId(String spreadSheetUrl) {
        int index = spreadSheetUrl.indexOf("/d/");
        spreadSheetUrl = spreadSheetUrl.substring(index + 3);
        index = spreadSheetUrl.indexOf("/edit");
        spreadSheetUrl = spreadSheetUrl.substring(0, index);

        return spreadSheetUrl;
    }

    // GoogleSpreadSheetのURLからシートGIDを取得
    public static String getSpreadSheetGid(String spreadSheetUrl) {
        String spreadSheetId = spreadSheetUrl.substring(spreadSheetUrl.lastIndexOf("gid=") + 4);
        return spreadSheetId;
    }

    // 対象のシート名を取得
    public static String getSheetNameFromGid(String spreadSheetId, String targetGid)
            throws IOException, GeneralSecurityException {
        Spreadsheet response = getSpreadsheets().spreadsheets().get(spreadSheetId).execute();
        List<Sheet> sheets = response.getSheets();
        if (sheets != null) {
            for (Sheet sheet : sheets) {
                String sheetGid = String.valueOf(sheet.getProperties().getSheetId());
                if (sheetGid.equals(targetGid)) {
                    return sheet.getProperties().getTitle();
                }
            }
        }
        return null;
    }

    // シートの最終行を動的に取得して範囲を指定
    public static String getDynamicRange(String spreadsheetId, String sheetName)
            throws IOException, GeneralSecurityException {
        Sheets service = getSpreadsheets();
        String range = sheetName + "!A:Z";
        ValueRange result = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = result.getValues();

        int lastRowIndex = values.size() - 1;
        List<Object> lastRow = values.get(lastRowIndex);
        int lastColumnIndex = lastRow.size() - 1;

        System.out.println("lastRowIndex : " + lastRowIndex);
        System.out.println("lastColumnIndex : " + lastColumnIndex);

//        int lastRow = values != null ? values.size() : 0;
        lastRowIndex = lastRowIndex + 1; // 3行目から取得したい
        String dynamicRange = sheetName + "!A" + 3 + ":" + lastColumnIndex;
        System.out.println(dynamicRange);
        return dynamicRange;
    }

    // 行データをリストとして取得するメソッド
    public static List<List<Object>> getRows(String spreadSheetId, String range)
            throws IOException, GeneralSecurityException {
        Sheets service = getSpreadsheets();
        // スプレッドシートのデータを取得
        ValueRange response = service.spreadsheets().values().get(spreadSheetId, range).execute();
        // 行データをリストで取得
        List<List<Object>> rows = response.getValues();
        for (List<Object> row : rows) {
            System.out.println("row : " + row);
        }
        return rows;
    }

    // Sheetsインスタンスの取得
    public static Sheets getSpreadsheets() throws IOException, GeneralSecurityException {
        InputStream input = new FileInputStream("src/main/resources/plated-course-424107-c4-ae0a645e74f3.json");
        GoogleCredentials credential = ServiceAccountCredentials.fromStream(input)
                .createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credential);

        Sheets service = new Sheets.Builder(transport, jsonFactory, httpRequestInitializer).build();
        return service;
    }

    // fileから車両を登録する
    public void registerExcelFileDataVehicle(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        Workbook wb = WorkbookFactory.create(inputStream);
        org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheet("車両一覧");

        int lastRowN = sheet.getLastRowNum();
        int actualRowCount = 0;

        // 実際にデータが入力されている行数を取得
        for (int i = 0; i <= lastRowN; i++) {
            Row r = sheet.getRow(i);
            if (r != null) {
                boolean isEmpty = true;
                for (int j = 0; j < r.getPhysicalNumberOfCells(); j++) {
                    Cell cell = r.getCell(j);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        isEmpty = false;
                        break;
                    }
                }
                if (!isEmpty) {
                    actualRowCount++;
                }
            }
        }

        // 見出し行の最終列の取得
        Row headerRow = sheet.getRow(0);
        int headerRowSize = 0;
        if (headerRow != null) {
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    headerRowSize = i;
                }
            }
        }

        for (int i = 2; i < actualRowCount; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Car car = new Car();
                if (row.getCell(0).getCellType() == CellType.BLANK || row.getCell(1).getCellType() == CellType.BLANK
                        || row.getCell(2).getCellType() == CellType.BLANK
                        || row.getCell(3).getCellType() == CellType.BLANK
                        || row.getCell(4).getCellType() == CellType.BLANK
                        || row.getCell(5).getCellType() == CellType.BLANK
                        || row.getCell(9).getCellType() == CellType.BLANK
                        || row.getCell(10).getCellType() == CellType.BLANK
                        || row.getCell(11).getCellType() == CellType.BLANK
                        || row.getCell(12).getCellType() == CellType.BLANK
                        || row.getCell(13).getCellType() == CellType.BLANK
                        || row.getCell(14).getCellType() == CellType.BLANK) {
                    continue;
                }
                car.setMaker(String.valueOf(row.getCell(4)));
                car.setCarModel(String.valueOf(row.getCell(5)));
                car.setGrade(String.valueOf(row.getCell(9)));
                for (Classification classification : Car.Classification.values()) {
                    if (String.valueOf(classification.getValue()).equals(String.valueOf(row.getCell(0)))) {
                        car.setClassification(classification);
                    }
                }
                for (JapaneseCalendar japaneseCalendar : Car.JapaneseCalendar.values()) {
                    if (String.valueOf(japaneseCalendar.getValue()).equals(String.valueOf(row.getCell(1)))) {
                        car.setFirstJc(japaneseCalendar);
                    }
                    if (String.valueOf(japaneseCalendar.getValue()).equals(String.valueOf(row.getCell(6)))) {
                        car.setViJc(japaneseCalendar);
                    }
                }

                car.setRegistrationYear((int) (Double.parseDouble(String.valueOf(row.getCell(2)))));
                car.setRegistrationMonth((int) Double.parseDouble(String.valueOf(row.getCell(3))));
                car.setViYear((int) Double.parseDouble(String.valueOf(row.getCell(7))));
                car.setViMonth((int) Double.parseDouble(String.valueOf(row.getCell(8))));
                car.setPrice((int) Double.parseDouble(String.valueOf(row.getCell(10))));
                car.setPriceDpf((int) Double.parseDouble(String.valueOf(row.getCell(11))));
                car.setTotalPrice((int) Double.parseDouble(String.valueOf(row.getCell(12))));
                car.setTotalPriceDpf((int) Double.parseDouble(String.valueOf(row.getCell(13))));
                car.setMileage((int) Double.parseDouble(String.valueOf(row.getCell(14))));

                ErrorKinds result = carService.createCar(car);
                if (result == ErrorKinds.SUCCESS) {
                    carService.createCarSave(car);
                }
            }
        }
    }
}
