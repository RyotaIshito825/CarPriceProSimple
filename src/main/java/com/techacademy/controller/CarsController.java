package com.techacademy.controller;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Car;
import com.techacademy.entity.Car.Classification;
import com.techacademy.entity.Car.JapaneseCalendar;
import com.techacademy.entity.PriceCard;
import com.techacademy.service.CarService;
//import com.techacademy.service.PdfGenerationService;
import com.techacademy.service.PriceCardService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("cars")
public class CarsController {

    private final CarService carService;
    private final PriceCardService priceCardService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    public CarsController(CarService carService, PriceCardService priceCardService) {
        this.carService = carService;
        this.priceCardService = priceCardService;
    }

    // トップ画面表示
    @GetMapping(value = "/top")
    public String pageTop() {
        return "cars/top";
    }

    // 車両一覧画面表示
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/list")
    public String top(@PageableDefault(page = 0, size = 8) Pageable pageable, String keyword, Integer minPrice, Integer maxPrice, Model model, HttpSession session) {

        List<Car> carList = (List<Car>) session.getAttribute("carList");

        String shopName = (String) session.getAttribute("shopName");
        String shopImageBase64 = (String) session.getAttribute("shopImageBase64");

        System.out.println(shopImageBase64);
        System.out.println(shopName);

        if (keyword != null) {
            int min_price  = (minPrice != null) ? minPrice : 0;
            int max_price = (maxPrice != null) ? maxPrice : 9999;
            carList = filterCarsByKeyword(carList, keyword, min_price, max_price);
            Page<Car> page = paginateList(carList, pageable);

            model.addAttribute("keyword", keyword);
            if (minPrice != null && minPrice != 0) {
                model.addAttribute("minPrice", min_price);
            }
            if (maxPrice != null && maxPrice != 9999) {
                model.addAttribute("maxPrice", max_price);
            }

            model.addAttribute("page", page);

            if (carList != null) {
//                session.setAttribute("carList", carList);
            } else {
                session.setAttribute("carList", new ArrayList<>());
            }
            model.addAttribute("carList", carList);
            model.addAttribute("car", new Car());

            PriceCard priceCard = (PriceCard) session.getAttribute("priceCard");
            if (priceCard != null) {
                session.setAttribute("priceCardName", priceCard.getPriceCardName());
                model.addAttribute("priceCardName", priceCard.getPriceCardName());
            }

            return "cars/list";
        }

        Page<Car> page = paginateList(carList, pageable);

        if (carList != null) {
            session.setAttribute("carList", carList);
        } else {
            session.setAttribute("carList", new ArrayList<>());
        }

        model.addAttribute("page", page);
        model.addAttribute("carList", carList);

        PriceCard priceCard = (PriceCard) session.getAttribute("priceCard");
        if (priceCard != null) {
            session.setAttribute("priceCardName", priceCard.getPriceCardName());
            model.addAttribute("priceCardName", priceCard.getPriceCardName());
        }


        return "cars/list";
    }

    // 車両詳細画面表示
//    @GetMapping(value = "/{id}")
//    public String detail(@PathVariable Integer id, Model model) {
//
////        Car car = carService.findById(id);
//        boolean viExits = String.valueOf(car.getViYear()) == null ? false : true;
//
//        model.addAttribute("viExits", viExits);
//        model.addAttribute("car", car);
//
//        return "cars/detail";
//    }

    // 車両新規登録
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Car car, Model model) {
        return "cars/new";
    }

    // 車両新規登録処理
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/add/submit")
    public String add(@Validated Car car, BindingResult res, Model model, HttpSession session) {

        try {

            Car newCar = car;
            int carPrice = Integer.parseInt(String.valueOf(car.getPrice()) + String.valueOf(car.getPriceDpf()));
            int carTotalPrice = Integer.parseInt(String.valueOf(car.getTotalPrice()) + String.valueOf(car.getTotalPriceDpf()));

            int calcPrice = carTotalPrice - carPrice;

            int calcPriceOfInt = Integer.parseInt(String.valueOf(calcPrice).substring(0, String.valueOf(calcPrice).length() - 1));
            int calcPriceOfDpf = Integer.parseInt(String.valueOf(calcPrice).substring(String.valueOf(calcPrice).length() - 1, String.valueOf(calcPrice).length()));

            newCar.setCalcPriceOfInt(calcPriceOfInt);
            newCar.setCalcPriceOfDpf(calcPriceOfDpf);

            if (res.hasErrors()) {
                model.addAttribute("car", newCar);
                return "cars/new";
            }

            List<Car> carList = (List<Car>) session.getAttribute("carList");
            ErrorKinds result = carService.createCar(newCar);
            if (result == ErrorKinds.SUCCESS) {
                if (carList == null) {
                    carList = new ArrayList<>();
                    carList.add(newCar);
                } else {
                    carList.add(newCar);
                }

            }
            session.setAttribute("carList", carList);

            return "redirect:/cars/list";

        } catch (NumberFormatException e) {
            return "cars/new";
        }
    }

    // 車両更新画面表示
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/update")
    public String edit(@RequestParam int index, Model model, HttpSession session) {
        List<Car> carList = (List<Car>) session.getAttribute("carList");
        if (carList == null || index < 0 || index >= carList.size()) {
            return "redirect:/cars/list";
        }
        Car car = carList.get(index);
        model.addAttribute("index", index);

        model.addAttribute("car", car);
        return "cars/edit";
    }

    // 車両更新処理
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/update/submit")
    public String update(@Validated Car car, int index, BindingResult res, Model model, HttpSession session) {
        List<Car> carList = (List<Car>) session.getAttribute("carList");
        if (carList == null || index < 0 || index >= carList.size()) {
            return "redirect:/cars/list";
        }

        Car updateCar = car;
        int carPrice = Integer.parseInt(String.valueOf(car.getPrice()) + String.valueOf(car.getPriceDpf()));
        int carTotalPrice = Integer.parseInt(String.valueOf(car.getTotalPrice()) + String.valueOf(car.getTotalPriceDpf()));

        int calcPrice = carTotalPrice - carPrice;

        int calcPriceOfInt = Integer.parseInt(String.valueOf(calcPrice).substring(0, String.valueOf(calcPrice).length() - 1));
        int calcPriceOfDpf = Integer.parseInt(String.valueOf(calcPrice).substring(String.valueOf(calcPrice).length() - 1, String.valueOf(calcPrice).length()));

        updateCar.setCalcPriceOfInt(calcPriceOfInt);
        updateCar.setCalcPriceOfDpf(calcPriceOfDpf);

        carList.set(index, car);
        session.setAttribute("carList", carList);

        return "redirect:/cars/list";
    }

    // テンプレート一覧画面表示
    @GetMapping(value = "/template")
    public String template(Model model) {
        return "cars/template";
    }
    // テンプレート
    @GetMapping(value = "/template/submit")
    public String confirmedTemplate(@RequestParam String priceCardName, HttpSession session) {

        PriceCard priceCard = new PriceCard();
        priceCard.setPriceCardName(priceCardName);
        session.setAttribute("priceCard", priceCard);

        return "redirect:/cars/list";
    }

    // データ取込画面表示
    @GetMapping(value = "/intake")
    public String intake(Model model) {
        return "cars/intake";
    }

    // データ取込処理
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/intake/submit")
    public String regi(String url, MultipartFile file, Model model, HttpSession session) throws IOException, GeneralSecurityException {

        if (file != null && !file.isEmpty()) {
            registerExcelFileDataVehicle(file, session);
            if (url.isEmpty()) {
                return "redirect:/cars/list";
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
            List<Car> lists = (List<Car>) session.getAttribute("carList");
            if (lists == null) {
                lists = new ArrayList<>();
            }

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


                int carPrice = Integer.parseInt(String.valueOf((int)(Double.parseDouble(String.valueOf(row.get(10))))) + String.valueOf((int)(Double.parseDouble(String.valueOf(row.get(11))))));
                int carTotalPrice = Integer.parseInt(String.valueOf((int)(Double.parseDouble(String.valueOf(row.get(12))))) + String.valueOf((int)(Double.parseDouble(String.valueOf(row.get(13))))));
                int calcPrice = carTotalPrice - carPrice;

                Integer calcPriceOfInt = Integer.parseInt(String.valueOf(calcPrice).substring(0, String.valueOf(calcPrice).length() - 1));
                Integer calcPriceOfDpf = Integer.parseInt(String.valueOf(calcPrice).substring(String.valueOf(calcPrice).length() - 1, String.valueOf(calcPrice).length()));

                car.setCalcPriceOfInt(calcPriceOfInt);
                car.setCalcPriceOfDpf(calcPriceOfDpf);

                car.setMileage(Integer.parseInt(String.valueOf(row.get(14))));

                lists.add(car);
            }

            session.setAttribute("carList", lists);

            return "redirect:/cars/list";

        } catch (GoogleJsonResponseException e) { // スプレッドシートの権限エラー処理

            model.addAttribute("error", ErrorMessage.getErrorValue(ErrorKinds.GOOGLESPREADSHEET_AUTHORITY_ERROR));
            return "cars/intake";
        } catch (ArrayIndexOutOfBoundsException e) {
            return "cars/intake";
        } catch (NumberFormatException e) {
            return "cars/intake";
        }
    }

    @GetMapping(value = "/shop")
    public String shop() {
        return "cars/shop_registration";
    }

    @GetMapping(value = "/shop/submit")
    public String shopRegi(@RequestParam MultipartFile file, @RequestParam String shopName, HttpSession session, Model model) throws IOException {

        if (!file.isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            session.setAttribute("shopImageBase64", base64Image);
        }
        session.setAttribute("shopName", shopName);
        return "redirect:/cars/list";
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
    @SuppressWarnings("unchecked")
    public void registerExcelFileDataVehicle(MultipartFile file, HttpSession session) throws IOException {
        try {


            byte[] bytes = file.getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            Workbook wb = WorkbookFactory.create(inputStream);
            org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheet("車両一覧");

            List<Car> carList = (List<Car>) session.getAttribute("carList");
            if (carList == null) {
                carList = new ArrayList<>();
                session.setAttribute("carList", carList);
            }

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
                            || row.getCell(14).getCellType() == CellType.BLANK ) {
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

                    int carPrice = Integer.parseInt(String.valueOf((int)(Double.parseDouble(String.valueOf(row.getCell(10))))) + String.valueOf((int)(Double.parseDouble(String.valueOf(row.getCell(11))))));
                    int carTotalPrice = Integer.parseInt(String.valueOf((int)(Double.parseDouble(String.valueOf(row.getCell(12))))) + String.valueOf((int)(Double.parseDouble(String.valueOf(row.getCell(13))))));
                    int calcPrice = carTotalPrice - carPrice;

                    Integer calcPriceOfInt = Integer.parseInt(String.valueOf(calcPrice).substring(0, String.valueOf(calcPrice).length() - 1));
                    Integer calcPriceOfDpf = Integer.parseInt(String.valueOf(calcPrice).substring(String.valueOf(calcPrice).length() - 1, String.valueOf(calcPrice).length()));

                    car.setCalcPriceOfInt(calcPriceOfInt);
                    car.setCalcPriceOfDpf(calcPriceOfDpf);
                    car.setMileage((int) Double.parseDouble(String.valueOf(row.getCell(14))));

                    ErrorKinds result = carService.createCar(car);
                    carList.add(car);
                }
            }
            session.setAttribute("carList", carList);
        } catch (Exception e) {
            return;
        }
    }

    // Listをキーワードでフィルタリング
    public List<Car> filterCarsByKeyword(List<Car> carList, String keyword, Integer minPrice, Integer maxPrice) {

        return carList.stream()
                .filter(car -> {
                    boolean priceMatch = car.getTotalPrice() != null
                            && car.getTotalPrice() >= minPrice
                            && car.getTotalPrice() <= maxPrice;

                    // キーワードが空なら常に true
                    boolean keywordMatch = keyword == null || keyword.isBlank()
                            || containsIgnoreCase(car.getMaker(), keyword)
                            || containsIgnoreCase(car.getCarModel(), keyword)
                            || containsIgnoreCase(car.getGrade(), keyword);

                    return priceMatch && keywordMatch;
                })
                .collect(Collectors.toList());
    }

    // 大文字小文字を無視して含まれているかチェック
    private boolean containsIgnoreCase(String source, String keyword) {
        if (source == null || keyword == null) return false;
        return source.toLowerCase().contains(keyword.toLowerCase());
    }

//    public Page<Car> paginateList(List<Car> carList, Pageable pageable) {
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), carList.size());
//        List<Car> sublist = carList.subList(start, end);
//        return new PageImpl<>(sublist, pageable, carList.size());
//    }
    public Page<Car> paginateList(List<Car> carList, Pageable pageable) {
        if (carList == null || carList.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), carList.size());

        if (start >= carList.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, carList.size());
        }

        List<Car> sublist = carList.subList(start, end);
        return new PageImpl<>(sublist, pageable, carList.size());
    }

}
