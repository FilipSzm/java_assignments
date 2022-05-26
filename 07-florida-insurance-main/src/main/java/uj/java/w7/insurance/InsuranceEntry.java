package uj.java.w7.insurance;

import java.math.BigDecimal;
import java.math.BigInteger;

public record InsuranceEntry(BigInteger policyID,
                             String statecode,
                             String county,
                             BigDecimal eq_site_limit,
                             BigDecimal hu_site_limit,
                             BigDecimal fl_site_limit,
                             BigDecimal fr_site_limit,
                             BigDecimal tiv_2011,
                             BigDecimal tiv_2012,
                             double eq_site_deductible,
                             double hu_site_deductible,
                             double fl_site_deductible,
                             double fr_site_deductible,
                             BigDecimal point_latitude,
                             BigDecimal point_longitude,
                             String line,
                             String construction,
                             int point_granularity) { }