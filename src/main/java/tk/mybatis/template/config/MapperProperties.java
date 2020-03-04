package tk.mybatis.template.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author wuxiaopeng
 * @description:
 * @date 2020/3/4 17:57
 */
@EnableConfigurationProperties(MapperProperties.class)
public class MapperProperties {
    public static final String PREFIX = "mapper";
}
