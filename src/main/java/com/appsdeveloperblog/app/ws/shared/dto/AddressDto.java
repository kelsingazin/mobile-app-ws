package com.appsdeveloperblog.app.ws.shared.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

@Data
public class AddressDto {
    private long id;
    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
    @JsonBackReference
    private UserDto userDetails;
}
