package com.everkeep.config;

import com.everkeep.dto.NoteDto;
import com.everkeep.dto.UserDto;
import com.everkeep.model.Note;
import com.everkeep.model.User;
import ma.glasnost.orika.MapperFactory;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig implements OrikaMapperFactoryConfigurer {

    @Override
    public void configure(MapperFactory mapperFactory) {
        mapperFactory.classMap(NoteDto.class, Note.class)
                .byDefault()
                .register();

        mapperFactory.classMap(UserDto.class, User.class)
                .byDefault()
                .register();
    }
}
