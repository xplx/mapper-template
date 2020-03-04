package tk.mybatis.template.annotation;


import cn.hutool.core.bean.BeanUtil;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.template.util.EmptyUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

/**
 * @author wuxiaopeng
 * @date  22020-03-02
 */
public class ConditionRewrite {
    /**
     * 解析mp注解条件
     *
     * @param condition
     * @param param
     * @return
     */
    public static Condition equalToCondition(Condition condition, Object param) {
        Condition.Criteria criteria = condition.createCriteria();
        Map<String, Object> map = BeanUtil.beanToMap(param);
        // 得到类对象
        Class userCla = (Class) param.getClass();
        //得到类中的所有属性集合
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            String property;
            Field f = fs[i];
            f.setAccessible(true);
            //获取成员变量上的注解
            ParamCondition mapperParam = f.getAnnotation(ParamCondition.class);
            if (mapperParam != null && (EmptyUtil.isNotEmpty(map.get(f.getName())) || "order".equals(mapperParam.order()))) {
                if (StringUtil.isEmpty(mapperParam.entityName())) {
                    property = f.getName();
                } else {
                    property = mapperParam.entityName();
                }
                if ("and".equals(mapperParam.patternType())) {
                    criteria = andJudgePattern(criteria, mapperParam.pattern(), property,
                            map.get(f.getName()), mapperParam.fuzzyPosition());
                } else if ("or".equals(mapperParam.patternType())) {
                    criteria = orJudgePattern(criteria, mapperParam.pattern(), property,
                            map.get(f.getName()), mapperParam.fuzzyPosition());
                } else {
                    throw new RuntimeException("查询连接符号【" + mapperParam.patternType() + "】不存在！");
                }

                //排序
                if ("order".equals(mapperParam.order())) {
                    if ("desc".equals(mapperParam.orderType())) {
                        condition.orderBy(property).desc();
                    }else if("asc".equals(mapperParam.orderType())) {
                        condition.orderBy(property).asc();
                    }else {
                        throw new RuntimeException("排序类型【" + mapperParam.orderType() + "】不存在！");
                    }
                }else if(EmptyUtil.isNotEmpty(mapperParam.order())) {
                    throw new RuntimeException("排序符号【" + mapperParam.order() + "】不存在！");
                }
                map.remove(f.getName());
            }
        }
        criteria.andEqualTo(map);
        return condition;
    }

    private static Example.Criteria getAnnotationCustom(Condition.Criteria criteria, Object param) {

        return criteria;
    }

    private static Example.Criteria andJudgePattern(Example.Criteria criteria, String pattern,
                                                    String property, Object value, String fuzzyPosition) {
        switch (pattern) {
            case "is not null":
                criteria.andIsNotNull(property);
                break;
            case "=":
                criteria.andEqualTo(property, value);
                break;
            case "!=":
                criteria.andNotEqualTo(property, value);
                break;
            case ">":
                criteria.andGreaterThan(property, value);
                break;
            case ">=":
                criteria.andGreaterThanOrEqualTo(property, value);
                break;
            case "<":
                criteria.andLessThan(property, value);
                break;
            case "<=":
                criteria.andLessThanOrEqualTo(property, value);
                break;
            case "in":
                criteria.andIn(property, Arrays.asList((Object[]) value));
                break;
            case "not in":
                criteria.andNotIn(property, Arrays.asList((Object[]) value));
                break;
            case "like":
                criteria.andLike(property, String.valueOf(getFuzzyPosition(value, fuzzyPosition)));
                break;
            case " not like":
                criteria.andNotLike(property, String.valueOf(getFuzzyPosition(value, fuzzyPosition)));
                break;
            default:
                throw new RuntimeException("查询符号:【" + pattern + "】不存在！");
        }
        return criteria;
    }

    private static Example.Criteria orJudgePattern(Example.Criteria criteria, String pattern,
                                                   String property, Object value, String fuzzyPosition) {
        switch (pattern) {
            case "is not null":
                criteria.orIsNotNull(property);
                break;
            case "=":
                criteria.orEqualTo(property, value);
                break;
            case "!=":
                criteria.orNotEqualTo(property, value);
                break;
            case ">":
                criteria.orGreaterThan(property, value);
                break;
            case ">=":
                criteria.orGreaterThanOrEqualTo(property, value);
                break;
            case "<":
                criteria.orLessThan(property, value);
                break;
            case "<=":
                criteria.orLessThanOrEqualTo(property, value);
                break;
            case "in":
                criteria.orIn(property, Arrays.asList((Object[]) value));
                break;
            case "not in":
                criteria.orNotIn(property, Arrays.asList((Object[]) value));
                break;
            case "like":
                criteria.orLike(property, String.valueOf(String.valueOf(getFuzzyPosition(value, fuzzyPosition))));
                break;
            case " not like":
                criteria.orNotLike(property, String.valueOf(String.valueOf(getFuzzyPosition(value, fuzzyPosition))));
                break;
            default:
                throw new RuntimeException("查询符号:【" + pattern + "】不存在！");
        }
        return criteria;
    }

    private static Object getFuzzyPosition(Object value, String fuzzyPosition) {
        if ("before".equals(fuzzyPosition)) {
            value = "%" + value;
        } else if ("after".equals(fuzzyPosition)) {
            value = value + "%";
        } else if ("all".equals(fuzzyPosition)) {
            value = "%" + value + "%";
        } else {
            throw new RuntimeException("模糊查询符号【" + fuzzyPosition + "】不存在！");
        }
        return value;
    }

    private static Object getOrderType(Object value, String fuzzyPosition) {
        if ("before".equals(fuzzyPosition)) {
            value = "%" + value;
        } else if ("after".equals(fuzzyPosition)) {
            value = value + "%";
        } else if ("all".equals(fuzzyPosition)) {
            value = "%" + value + "%";
        } else {
            throw new RuntimeException("模糊查询符号【" + fuzzyPosition + "】不存在！");
        }
        return value;
    }
}
