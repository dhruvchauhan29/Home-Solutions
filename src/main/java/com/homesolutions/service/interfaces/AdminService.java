package com.homesolutions.service.interfaces;

import com.homesolutions.dto.CategoryRequest;
import com.homesolutions.dto.CategoryResponse;
import com.homesolutions.dto.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface AdminService {
    Page<UserProfileResponse> getAllUsers(Pageable pageable);
    UserProfileResponse updateUserRoles(Long userId, Set<String> roles);
    CategoryResponse createCategory(CategoryRequest request);
    UserProfileResponse approveExpert(Long expertId);
    UserProfileResponse rejectExpert(Long expertId);
}
