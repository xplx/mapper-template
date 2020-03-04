package tk.mybatis.template.core;

import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service 层 基础接口，其他Service 接口 请继承该接口
 * @author wuxiaopeng
 */
public interface Service<T> {
    /**
     * 主键查询
     * @param id
     * @return
     */
    T findById(Object id);

    /**
     * 主键查询
     * @param id
     * @param m
     * @param <M>
     * @return 自定义类
     */
    <M> M findById(Object id, Class<M> m);

    /**
     * 单个实体类查询(表对象类)
     * @param model
     * @return
     */
    T findOneTb(T model);

    /**
     * 单个实体类查询(自定义对象类)
     * @param obj
     * @return
     */
    T findOneObject(Object obj);

    /**
     * 单个实体类查询(自定义对象类)
     * @param obj
     * @param m
     * @param <M>
     * @return 自定义返回类
     */
    <M> M findOneObject(Object obj, Class<M> m);

    /**
     * 通过map查询（表属性字段）
     * @param map
     * @return
     */
    T findOneMap(Map<String, Object> map);

    /**
     * //通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束
     * @param fieldName
     * @param value
     * @return
     */
    T findOneBy(String fieldName, Object value);

    /**
     * //通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束
     * @param fieldName
     * @param value
     * @return 返回自定类
     */
    <M> M findOneBy(String fieldName, Object value, Class<M> m);

    /**
     * 查询数据条数（表对象类）
     * @param model
     * @return
     */
    int findCountTb(T model);

    /**
     * 查询数据条数（自定义属性）
     * @param fieldName
     * @param value
     * @return
     */
    int findCountBy(String fieldName, Object value);

    /**
     * 查询数据条数（自定义对象类）
     * @param obj
     * @return
     */
    int findCountObject(Object obj);

    /**
     * 查询数量(表condition)
     * @param condition
     * @return
     */
    int findCountByCondition(Condition condition);

    /**
     * 查询数量(自定义对象类)
     * @param obj
     * @return
     */
    int findCountByConditionObject(Object obj);

    /**
     * 查询数量(表属性字段)
     * @param map
     * @return
     */
    int findCountByConditionMap(Map<String, Object> map);

    /**
     * 通过多个ID查找//eg：ids -> “1,2,3,4”
     * @param ids
     * @return
     */
    List<T> findListByIds(String ids);

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    List<T> findListByCondition(Condition condition);

    /**
     * 根据条件查询
     * @param condition
     * @param m
     * @param <M>
     * @return 自定义返回类
     */
    <M> ArrayList<M> findListByCondition(Condition condition, Class<M> m);

    /**
     * 根据条件查询(自定义对象类)
     * @param obj
     * @return
     */
    List<T> findListByObject(Object obj);

    /**
     * 根据条件查询(自定义对象类)
     * @param obj
     * @param m
     * @param <M>
     * @return 自定义返回类
     */
    <M> ArrayList<M> findListByObject(Object obj, Class<M> m);

    /**
     * 根据条件查询(表属性字段)
     * @param map
     * @return
     */
    List<T> findListByMap(Map<String, Object> map);

    /**
     * 根据条件查询(表属性字段)
     * @param fieldName
     * @param value
     * @return
     */
    List<T> findListByOnly(String fieldName, Object value);

    /**
     * 根据条件查询(表属性字段)
     * @param fieldName
     * @param value
     * @param m
     * @param <M>
     * @return 自定义返回类
     */
    <M> ArrayList<M> findListByOnly(String fieldName, Object value, Class<M> m);

    /**
     * 获取所有
     * @return
     */
    List<T> findListAll();

    /**
     * 有选择实体类保存(表对象类，主键id自动生成)
     * @param model
     * @return id
     */
    String saveSelectiveId(T model);

    /**
     * 有选择实体类保存(自定义类，主键id自动生成)
     * @param obj
     * @return id
     */
    String saveSelectiveIdObject(Object obj);

    /**
     * 有选择实体类保存(表对象类)
     * @param model
     */
    void saveSelectiveTb(T model);

    /**
     * 有选择实体类保存(自定义类)
     * @param obj
     */
    void saveSelectiveObject(Object obj);

    /**
     * 保存或更新
     * @param obj
     */
    void saveOrUpdateKeySelective(Object obj);

    /**
     * 批量保存或更新
     * @param models
     */
    void saveOrUpdateKeyList(List<T> models);

    /**
     * 批量保存或更新
     * @param models
     * @param t
     */
    void saveOrUpdateKeyList(List<Object> models, Class<T> t);

    /**
     * 批量保存（表对象类）
     * @param models
     */
    void saveListTb(List<T> models);

    /**
     * 通过主键id更新（表对象类）
     * @param model
     */
    void updateByKeyTb(T model);

    /**
     * 选择性更新表数据（通过主键，表对象类）
     * @param model
     */
    void updateByKeySelectiveTb(T model);

    /**
     * 选择性更新表数据（通过主键，自定义类）
     * @param obj
     */
    void updateByKeySelectiveObject(Object obj);

    /**
     * 有条件选择性更新（表对象类）
     * @param model
     * @param condition
     */
    void updateBySelectiveTb(T model, Condition condition);

    /**
     * 有条件选择性更新（自定义类）
     * @param tbO
     * @param obj
     */
    void updateBySelectiveObject(Object tbO, Object obj);

    /**
     * 有条件选择性更新（表字段属性）
     * @param obj
     * @param map
     */
    void updateBySelectiveMap(Object obj, Map<String, Object> map);

    /**
     * 通过主鍵刪除
     * @param id
     */
    void deleteById(Object id);

    /**
     * 有条件删除
     * @param condition
     */
    void deleteByCondition(Condition condition);

    /**
     * 有条件删除(属性参数)
     * @param fieldName
     * @param value
     */
    void deleteBy(String fieldName, Object value);

    /**
     * 有条件删除（对象类）
     * @param obj
     */
    void deleteByObject(Object obj);

    /**
     * 有条件删除（表字段属性）
     * @param map
     */
    void deleteByMap(Map<String, Object> map);

    /**
     * //批量刪除 eg：ids -> “1,2,3,4”(数组形式)
     * @param ids
     */
    void deleteByIds(String ids);

    /**
     * 通过查询返回条件删除表信息
     * @param condition
     */
    void deleteBySelectCondition(Condition condition);
}
