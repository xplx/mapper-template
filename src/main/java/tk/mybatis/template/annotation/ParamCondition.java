package tk.mybatis.template.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * tk查询条件设置
 * @author wuxiaopeng
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamCondition {
    /**
     * @value: 表字段名称
     * @return
     */
    String entityName() default "";

    /**
     * 查询条件: >、=、<、!=、like、in
     * @return
     */
    String pattern();

    /**
     * 连接类型: and、or （和、或）
     * @return
     */
    String patternType() default "and";

    /**
     * 匹配模糊：before前匹配:"%" + param，
     *            after后匹配:param + "%"，
     *            all前后匹配:"%" + param + "%"
     * @return
     */
    String fuzzyPosition() default "";

    /**
     * 排序
     * @return
     */
    String order() default "";

    /**
     * 排序类型：desc：倒序, asc：正序
     * @return
     */
    String orderType() default "desc";
}
