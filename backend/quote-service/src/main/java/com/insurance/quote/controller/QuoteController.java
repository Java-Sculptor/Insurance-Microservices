package com.insurance.quote.controller;

import com.insurance.quote.apiResponse.ApiResponse;
import com.insurance.quote.apiResponse.ErrorDetails;
import com.insurance.quote.dto.QuoteRequestDto;
import com.insurance.quote.dto.QuoteResponseDto;
import com.insurance.quote.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    // Generate a new quote
    @PostMapping
    public ResponseEntity<ApiResponse<QuoteResponseDto>> generateQuote(
            @RequestBody QuoteRequestDto quoteRequestDto) {

        try {
            QuoteResponseDto quote = quoteService.generateQuote(quoteRequestDto);

            ApiResponse<QuoteResponseDto> response = ApiResponse.<QuoteResponseDto>builder()
                    .success(true)
                    .message("Quote generated successfully")
                    .data(quote)
                    .error(null)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {

            ApiResponse<QuoteResponseDto> response = ApiResponse.<QuoteResponseDto>builder()
                    .success(false)
                    .message("Failed to generate quote")
                    .data(null)
                    .error(ErrorDetails.builder()
                            .error("QUOTE_GENERATION_FAILED")
                            .details(e.getMessage())
                            .build())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // Get all quotes
    @GetMapping
    public ResponseEntity<ApiResponse<List<QuoteResponseDto>>> getAllQuotes() {

        List<QuoteResponseDto> quotes = quoteService.getAllQuotes();

        ApiResponse<List<QuoteResponseDto>> response =
                ApiResponse.<List<QuoteResponseDto>>builder()
                        .success(true)
                        .message("Quotes fetched successfully")
                        .data(quotes)
                        .error(null)
                        .build();

        return ResponseEntity.ok(response);
    }

    // Get quote by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponseDto>> getQuoteById(
            @PathVariable Long id) {

        try {
            QuoteResponseDto quote = quoteService.getQuoteById(id);

            ApiResponse<QuoteResponseDto> response = ApiResponse.<QuoteResponseDto>builder()
                    .success(true)
                    .message("Quote fetched successfully")
                    .data(quote)
                    .error(null)
                    .build();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            ApiResponse<QuoteResponseDto> response = ApiResponse.<QuoteResponseDto>builder()
                    .success(false)
                    .message("Failed to fetch quote")
                    .data(null)
                    .error(ErrorDetails.builder()
                            .error("QUOTE_NOT_FOUND")
                            .details(e.getMessage())
                            .build())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
    }

    // Update quote
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuoteResponseDto>> updateQuote(
            @PathVariable Long id,
            @RequestBody QuoteRequestDto requestDto) {

        QuoteResponseDto quote = quoteService.updateQuote(id, requestDto);

        ApiResponse<QuoteResponseDto> response =
                ApiResponse.<QuoteResponseDto>builder()
                        .success(true)
                        .message("Quote updated successfully")
                        .data(quote)
                        .error(null)
                        .build();

        return ResponseEntity.ok(response);
    }

    // Cancel quote
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelQuote(
            @PathVariable Long id) {

        quoteService.cancelQuote(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Quote cancelled successfully")
                .data(null)
                .error(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<QuoteResponseDto>> approveQuote(
            @PathVariable Long id) {

        QuoteResponseDto quote = quoteService.approveQuote(id);

        ApiResponse<QuoteResponseDto> response =
                ApiResponse.<QuoteResponseDto>builder()
                        .success(true)
                        .message("Quote approved successfully")
                        .data(quote)
                        .error(null)
                        .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<QuoteResponseDto>> rejectQuote(
            @PathVariable Long id) {

        QuoteResponseDto quote = quoteService.rejectQuote(id);

        ApiResponse<QuoteResponseDto> response =
                ApiResponse.<QuoteResponseDto>builder()
                        .success(true)
                        .message("Quote rejected successfully")
                        .data(quote)
                        .error(null)
                        .build();

        return ResponseEntity.ok(response);
    }
    // Convert quote into policy (BRD: PUT /api/v1/quotes/{id}/convert)
    @PutMapping("/{id}/convert")
    public ResponseEntity<ApiResponse<QuoteResponseDto>> convertQuote(
            @PathVariable Long id) {

        QuoteResponseDto quote = quoteService.convertQuote(id);

        ApiResponse<QuoteResponseDto> response =
                ApiResponse.<QuoteResponseDto>builder()
                        .success(true)
                        .message("Quote converted into policy successfully")
                        .data(quote)
                        .error(null)
                        .build();

        return ResponseEntity.ok(response);
    }
}
