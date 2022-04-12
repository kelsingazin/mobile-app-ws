package com.appsdeveloperblog.app.ws.controller;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();
        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel) throws UserServiceException {

        if (userDetailsRequestModel.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetailsRequestModel, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        return modelMapper.map(createdUser, UserRest.class);
    }

    @PutMapping(value = "/{userId}",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel,
                               @PathVariable String userId) {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequestModel, userDto);

        UserDto updatedUser = userService.updateUser(userId, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{userId}")
    public OperationStatusModel deleteUser(@PathVariable String userId) {
        OperationStatusModel returnValue = new OperationStatusModel();

        userService.deleteUser(userId);

        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> userDtoList = userService.getUsers(page, limit);

        for (UserDto userDto : userDtoList) {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(userDto, userRest);
            returnValue.add(userRest);
        }

        return returnValue;
    }
}
