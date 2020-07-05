package com.ubic.shop.service;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ProductCategory;
import com.ubic.shop.dto.ProductResponseDto;
import com.ubic.shop.repository.CategoryRepository;
import com.ubic.shop.repository.ProductCategoryRepository;
import com.ubic.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryRepository categoryRepository;

    public void saveCategoryList(List<String> categoryList, Product product) {

//        for(String categoryName : categoryList){
//            // category name 으로 entity 가져오기
//            List<Category> findCategoryList = categoryRepository.findByName(categoryName);
//            // product 에 add category
//            if (!findCategoryList.isEmpty()) {
//                ProductCategory productCategory = ProductCategory.createProductCategory(
//                        product, findCategoryList.get(0));
//                productCategoryRepository.save(productCategory);
////                em.persist(productCategory);
//                product.addProductCategory(productCategory);
//            }
//        }
    }

//    @Transactional
//    public void saveProduct(ProductCategory productCategory) {
//        productCategoryRepository.save(productCategory);
//    }

}
