package com.ubic.shop.web;

import com.ubic.shop.dto.CategorySaveRequestDto;
import com.ubic.shop.dto.ProductSaveRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
//@Log4j
public class ExcelController {

    @Autowired
    RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/excel/read/products")
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
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            Row row = worksheet.getRow(i);
            if(row == null )
                continue;
            if(row.getCell(2)==null)
                continue;

            ProductSaveRequestDto requestDto = ProductSaveRequestDto.builder()
                    .categoryId((long) row.getCell(1).getNumericCellValue())
                    .name(row.getCell(3).getStringCellValue())
                    .price((int) row.getCell(4).getNumericCellValue())
                    .stockQuantity(50)
                    .description(row.getCell(5)==null? "":row.getCell(5).getStringCellValue())
                    .imgUrl(row.getCell(6).getStringCellValue())
                    .build();

            ProductSaveRequestDto result = restTemplate.postForObject(
                    "http://localhost:8080/products/new",
                    requestDto, ProductSaveRequestDto.class);
            if(result != null)
                logger.info("\n"+result.toString());

        }
    }//end handler

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

            CategorySaveRequestDto result = restTemplate.postForObject(
                    "http://localhost:8080/categories/new",
                    requestDto, CategorySaveRequestDto.class);

            logger.info("\n"+result.toString());
        }

    }
}