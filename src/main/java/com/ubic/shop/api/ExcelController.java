package com.ubic.shop.api;

import com.ubic.shop.config.UbicConfig;
import com.ubic.shop.config.UbicSecretConfig;
import com.ubic.shop.dto.CategorySaveRequestDto;
import com.ubic.shop.dto.ProductSaveRequestDto;
import com.ubic.shop.dto.SearchResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
//@Log4j
@Slf4j
public class ExcelController {

    @Autowired
    RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UbicConfig ubicConfig;
    private final UbicSecretConfig ubicSecretConfig;

    @PostMapping("/excel/read/products") //categories
    public void excelProductsNew(@RequestBody/*("file")*/ MultipartFile file) throws IOException {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!extension.equals("xlsx")) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }

        Workbook workbook = null;
        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else {
            throw new IOException("확장자가 xlsx 가 아니군요!");
        }

        Sheet worksheet = workbook.getSheetAt(0);
        String productName = "";
        String productDesc = "";
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            Row row = worksheet.getRow(i);
            if (row == null)
                continue;
            if (row.getCell(2) == null)
                continue;

            productName = row.getCell(3).getStringCellValue();
            productDesc = row.getCell(5) == null ? "" : row.getCell(5).getStringCellValue();
            ProductSaveRequestDto requestDto = ProductSaveRequestDto.builder()
                    .kurlyId((long) row.getCell(1).getNumericCellValue())
                    .name(productName)
                    .price((int) row.getCell(4).getNumericCellValue())
                    .stockQuantity(50)
                    .description(productDesc)
                    .imgUrl(row.getCell(6).getStringCellValue())
                    .build();

            // requestToSaveProduct
            requestToSaveProduct(requestDto);
//            if (result != null)
//                logger.info("\n" + result.toString());

            // 형태소 분석 - 상품 이름 : productName -- 제발 비동기 처리하자!
            // 형태소 분석 - 상품 설명 : productDesc
        }
    }//end handler

    @Async
    public void requestToSaveProduct(ProductSaveRequestDto requestDto) {
        try{
            ProductSaveRequestDto result = restTemplate.postForObject(
                    ubicConfig.baseUrl + "/api/products/new",
                    requestDto, ProductSaveRequestDto.class);

        }catch (Exception e){
            return;
        }
        return;
    }

    @GetMapping("/api/search/test") // 취소
    public Object searchTest(@RequestParam(value = "text") String text) {
//        ubicSecretConfig.etriApiKey;
        log.info("\ntext: " + text);

        // request body 구성
//        SearchRequestDto searchDto = new SearchRequestDto(ubicSecretConfig.etriApiKey, new Argument("morp", text));

        // url 요청 구성
        SearchResponseDto result = restTemplate.getForObject(
                "http://127.0.0.1:8000/search/test/?text=" + text,
                SearchResponseDto.class);

        // 스트림 처리하며 lemma 부분을 태그로 등록하기
        // Product 객체 필요한데! : ProductService 에서 처리!

        return result;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class SearchRequestDto { // request % response
        String accessKey;
        Argument argument;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class Argument {
        String analysisCode;
        String text;
    }



    @PostMapping("/excel/read/categories")
    public void excelCategoriesNew(@RequestBody/*("file")*/ MultipartFile file) throws IOException {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!extension.equals("xlsx")) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }

        Workbook workbook = null;
        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else {
            throw new IOException("확장자가 xlsx 가 아니군요!");
        }

        Sheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            Row row = worksheet.getRow(i);

            CategorySaveRequestDto requestDto = CategorySaveRequestDto.builder()
                    .kurlyId((long) row.getCell(2).getNumericCellValue())
                    .name(row.getCell(1).getStringCellValue())
                    .build();

//            log.info("\n"+requestDto.toString());
            CategorySaveRequestDto result = restTemplate.postForObject(
                    ubicConfig.baseUrl + "/api/categories/new",
                    /*body*/ requestDto, CategorySaveRequestDto.class);

            logger.info("\n" + result.toString());
        }

    }
}