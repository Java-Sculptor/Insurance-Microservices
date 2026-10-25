
package com.insurance.quote.service;


import com.insurance.quote.dto.QuoteRequestDto;
import com.insurance.quote.dto.QuoteResponseDto;

import java.util.List;

public interface QuoteService {

    QuoteResponseDto generateQuote(QuoteRequestDto quoteRequestDto);

    List<QuoteResponseDto> getAllQuotes();

    QuoteResponseDto getQuoteById(Long id);

    QuoteResponseDto updateQuote(Long id, QuoteRequestDto requestDto);

    void cancelQuote(Long id);


    //approve quote

    QuoteResponseDto approveQuote(Long id);

    QuoteResponseDto rejectQuote(Long id);

    QuoteResponseDto convertQuote(Long id);

}
