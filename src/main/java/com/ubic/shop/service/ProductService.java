package com.ubic.shop.service;

import com.ubic.shop.domain.Category;
import com.ubic.shop.domain.Product;
import com.ubic.shop.dto.ProductResponseDto;
import com.ubic.shop.dto.ProductSaveRequestDto;
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
    private final TagService tagService;

    @Transactional
    public ProductResponseDto saveProduct(ProductSaveRequestDto productDto, Category category) {
        Product product = productDto.toEntity(category);
        if (!validateDuplicateProduct(product)) { // false 이면 중복상품
            return null;
        } //중복 상품 검증

        // 여기서 생성된 product 를 가지고 Tag 등록을 해야겠다
        product = productRepository.save(product);
        tagService.stemmingAndRegisterTag(product);

        return new ProductResponseDto(product);//productResponseDto;
    }

    private boolean validateDuplicateProduct(Product product) {
        List<Product> findProductList = productRepository.findByName(product.getName());
        if (!findProductList.isEmpty()) {
            return false;
        }
        return true;
    }

    public List<Product> findPagingProducts(Pageable pageable) {
        return productRepository.findProductsCountBy(pageable).getContent();
    }

    public Product findById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        return null;
    }
}
