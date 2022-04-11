package com.appsdeveloperblog.app.ws.shared.dto;

import lombok.Data;

@Data
public class AddressDto {
    public long id;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
    private UserDto userDetails;
}
