package com.ubic.shop.service;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import com.ubic.shop.domain.ProductCategory;
import com.ubic.shop.dto.ProductResponseDto;
import com.ubic.shop.dto.ProductSaveRequestDto;
import com.ubic.shop.repository.ProductCategoryRepository;
import com.ubic.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryService productCategoryService;
//    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public ProductResponseDto saveProduct(ProductSaveRequestDto productDto, Category category) {
        Product product = productDto.toEntity(category);
        if(!validateDuplicateProduct(product)){ // false 이면 중복상품
            return null;
        } //중복 상품 검증
        // 여기까지 dto 끌고 들어와서, category list 처리 ?
//        product.se
//        ProductResponseDto productResponseDto;
//        Product product = ;
//        productCategoryService.saveCategoryList(productDto.getCategoryList(), product);
        return new ProductResponseDto(productRepository.save(product));//productResponseDto;
    }

    private boolean validateDuplicateProduct(Product product) {
        List<Product> findProductList = productRepository.findByName(product.getName());
        if (!findProductList.isEmpty()) {
            return false;
//            throw new IllegalStateException("이미 존재하는 상품입니다.");
        }
        return true;
    }

    public List<Product> findAllProducts(/*Pageable pageable*/) {
        return (List)productRepository.findDefaultProducts/*findAll*/();
    }

    public Product findById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if(optionalProduct.isPresent()){
            return optionalProduct.get();
        }
        return null;
    }
}
