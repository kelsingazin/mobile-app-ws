package com.appsdeveloperblog.app.ws.ui.model.request;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailsRequestModel {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private List<AddressRequestModel> addresses;
}
