package com.bingco.basic;

import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@SpringBootConfiguration
public class DbConfig {

    @Bean
    public ConfigurationCustomizer dbConfigurationCustomizer() {
        return configuration -> configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());
    }

    @Component
    @ConfigurationPropertiesBinding
    public static final class ObjectWrapperFactoryConverter implements Converter<String, ObjectWrapperFactory> {
        @Override
        public ObjectWrapperFactory convert(String source) {
            try {
                return (ObjectWrapperFactory) Class.forName(source).getConstructor()
                        .newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
