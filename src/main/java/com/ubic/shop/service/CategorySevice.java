package com.ubic.shop.service;

import com.ubic.shop.domain.Category;
import com.ubic.shop.dto.CategorySaveRequestDto;
import com.ubic.shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategorySevice {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category saveCategory(CategorySaveRequestDto requestDto) {
        Category category = requestDto.toEntity();
        if (!validateDuplicateCategory(category)) { // false 이면
            return null;
        } //중복 카테고리 검증 true 이면 정상
        return categoryRepository.save(category);
    }

    private boolean validateDuplicateCategory(Category category) {

        Category byKurlyId = categoryRepository.findByKurlyId(category.getKurlyId());
        if (byKurlyId != null) { // 존재한다
            log.info("\n존재하는 카테고리 {}", byKurlyId.getName());
            return false;//throw new IllegalStateException("이미 존재하는 카테고리입니다.");
        }
        return true;
    }

    public Category getCategoryById(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) { // 존재한다
            return optionalCategory.get();
        }
        return null;
    }

    public Category getCategoryByKurlyId(Long categoryId) {
        Category byKurlyId = categoryRepository.findByKurlyId(categoryId);
        if (byKurlyId != null) { // 존재한다
            return byKurlyId;
        }
        return null;
    }
}
