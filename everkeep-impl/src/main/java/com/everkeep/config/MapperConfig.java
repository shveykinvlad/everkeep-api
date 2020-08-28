package com.everkeep.config;

import ma.glasnost.orika.MapperFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.context.annotation.Configuration;

import com.everkeep.dto.NoteDto;
import com.everkeep.dto.RegistrationRequest;
import com.everkeep.model.Note;
import com.everkeep.model.security.User;

@Configuration
public class MapperConfig implements OrikaMapperFactoryConfigurer {

    @Override
    public void configure(MapperFactory mapperFactory) {
        mapperFactory.classMap(NoteDto.class, Note.class)
                .byDefault()
                .register();

        mapperFactory.classMap(RegistrationRequest.class, User.class)
                .byDefault()
                .register();
    }
}
