package com.techacademy.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Car;
import com.techacademy.entity.Employee;
import com.techacademy.entity.PriceCard;
import com.techacademy.service.CarService;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.PriceCardService;

@Controller
@RequestMapping("cars")
public class CarsController {

    private final CarService carService;
    private final PriceCardService priceCardService;
    private final EmployeeService employeeService;

    @Autowired
    public CarsController(CarService carService, PriceCardService priceCardService, EmployeeService employeeService) {
        this.carService = carService;
        this.priceCardService = priceCardService;
        this.employeeService = employeeService;
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
    public String confirmedTemplate(@RequestParam("priceCardName") String priceCardName) {

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
    public String regi(String url, Model model) throws IOException, GeneralSecurityException {

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
                Car car = new Car();
                car.setCarModel(String.valueOf(row.get(4)));

                System.out.println(row.get(0));
                System.out.println(row.get(1));
                System.out.println(row.get(2));
                System.out.println(row.get(3));
                System.out.println(row.get(4));
                System.out.println(row.get(5));
                System.out.println(row.get(6));
                System.out.println(row.get(7));
                System.out.println(row.get(8));
                System.out.println(row.get(9));
                System.out.println(row.get(10));
                System.out.println(row.get(11));
                System.out.println(row.get(12));
                System.out.println(row.get(13));
                System.out.println(row.get(14));
            }

            System.out.println("id : " + getSpreadSheetId(url));
            System.out.println("gid : " + getSpreadSheetGid(url));

            return "redirect:/cars";

        } catch (GoogleJsonResponseException e) { // スプレッドシートの権限エラー処理

            model.addAttribute("error", ErrorMessage.getErrorValue(ErrorKinds.GOOGLESPREADSHEET_AUTHORITY_ERROR));
            return "/cars/intake";
        }
    }

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
    public static String getSheetNameFromGid(String spreadSheetId, String targetGid) throws IOException, GeneralSecurityException {
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
    public static String getDynamicRange(String spreadsheetId, String sheetName) throws IOException, GeneralSecurityException {
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
        String dynamicRange = sheetName + "!A" + lastRowIndex + ":" + lastColumnIndex;
        System.out.println(dynamicRange);
        return dynamicRange;
    }

    // 行データをリストとして取得するメソッド
    public static List<List<Object>> getRows(String spreadSheetId, String range) throws IOException, GeneralSecurityException {
        Sheets service = getSpreadsheets();
        // スプレッドシートのデータを取得
        ValueRange response = service.spreadsheets().values()
                .get(spreadSheetId, range)
                .execute();
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
        GoogleCredentials credential = ServiceAccountCredentials.fromStream(input).createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credential);

        Sheets service = new Sheets.Builder(transport, jsonFactory, httpRequestInitializer).build();

        return service;
    }

}
