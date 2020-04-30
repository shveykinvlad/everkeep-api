package com.everkeep.config;

import com.everkeep.dto.NoteDto;
import com.everkeep.model.Note;
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
    }
}
