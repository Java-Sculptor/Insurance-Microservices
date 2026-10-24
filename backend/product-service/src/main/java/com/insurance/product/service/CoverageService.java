package com.insurance.product.service;

import com.insurance.product.dto.CoverageRequest;
import com.insurance.product.dto.CoverageResponse;

import java.util.List;

public interface CoverageService {

    CoverageResponse create(Long productId, CoverageRequest request);

    List<CoverageResponse> getByProduct(Long productId);

    CoverageResponse update(Long coverageId, CoverageRequest request);

    void delete(Long coverageId);
}