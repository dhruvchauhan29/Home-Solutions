package com.homesolutions.mapper;

import com.homesolutions.dto.CategoryRequest;
import com.homesolutions.dto.CategoryResponse;
import com.homesolutions.entity.Category;

public class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .build();
    }

    public static Category toEntity(CategoryRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public static void updateEntityFromRequest(Category category, CategoryRequest request) {
        if (category == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
    }
}
