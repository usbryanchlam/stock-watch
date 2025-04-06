package dev.bryanlam.stockwatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyProfileApiResponse {
    private String country;     // Country of company's headquarter
    private String currency;    // Currency used in company filings
    private String exchange;    // Listed exchange
    private String ipo;         // IPO date
    private Integer marketCapitalization;   // Market Capitalization
    private String name;        // Company name
    private String phone;       // Company phone number
    private Double shareOutstanding;    // Number of oustanding shares
    private String ticker;      // Company symbol/ticker as used on the listed exchange
    private String weburl;      // Company website
    private String logo;        // Logo image
    private String finnhubIndustry; // Finnhub industry classification
    
}
