package com.example.timetracker.auth.mapper;

import com.example.timetracker.auth.dto.UserResponse;
import com.example.timetracker.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toUserResponse(User user);

}
