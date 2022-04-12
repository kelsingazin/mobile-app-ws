package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {
        log.info("User created");
        if (Objects.nonNull(userRepository.findByEmail(user.getEmail())))
            throw new RuntimeException("Email is already taken!");

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDto addressDto = user.getAddresses().get(i);
            addressDto.setUserDetails(user);
            addressDto.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i,addressDto);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = getUserEntityByEmail(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public UserDto getUser(String email) {
        log.info("Specific user retrieved");
        UserEntity userEntity = getUserEntityByEmail(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = getUserEntityByUserId(userId);
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {
        log.info("UpdateUser method was called!");
        UserDto returnValue = new UserDto();
        UserEntity userEntity = getUserEntityByUserId(userId);

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        log.info("DeleteUser method was called");
        UserEntity userEntity = getUserEntityByUserId(userId);

        userRepository.delete(userEntity);
        log.info("User with userId {} was deleted", userId);
    }

    private UserEntity getUserEntityByUserId(String userId) throws UserServiceException {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (Objects.isNull(userEntity)) throw new UserServiceException("User with userId: " + userId + " is not found");
        return userEntity;
    }

    private UserEntity getUserEntityByEmail(String email) throws UserServiceException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (Objects.isNull(userEntity)) throw new UserServiceException("User with email: " + email + " is not found");
        return userEntity;
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        if(page>0) page--;

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }
}
