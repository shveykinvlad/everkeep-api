package com.everkeep.config.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.everkeep.dto.RegistrationRequest;
import com.everkeep.model.security.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User map(RegistrationRequest registrationRequest);
}
