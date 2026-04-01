package com.empik.couponservice.infrastructure.geo;

public interface IpCountryResolver {

    String resolveCountry(String ipAddress);
}