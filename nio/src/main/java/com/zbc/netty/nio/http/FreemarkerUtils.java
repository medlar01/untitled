package com.zbc.netty.nio.http;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;

public class FreemarkerUtils {
    private static final Configuration configuration = new Configuration(Configuration.getVersion());

    static {
        configuration.setClassForTemplateLoading(FreemarkerUtils.class, "/ftl");
        configuration.setDefaultEncoding("utf-8");
    }

    public static Template getTemplate(String target) throws IOException {
        return configuration.getTemplate(target);
    }
}
