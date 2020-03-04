package tk.mybatis.template.core;


import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.template.exception.ServiceException;
import tk.mybatis.template.util.BeanUtil;
import tk.mybatis.template.util.SnowflakeIdUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于通用MyBatis Mapper插件的Service接口的实现
 *
 * @author wuxiaopeng
 */
public abstract class AbstractService<T> implements Service<T> {

    @Autowired
    protected Mapper<T> mapper;

    /**
     * 当前泛型真实类型的Class
     */
    private Class<T> modelClass;

    public AbstractService() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public T findById(Object id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public <M> M findById(Object id, Class<M> m) {
        return BeanUtil.toModelClass(mapper.selectByPrimaryKey(id), m);
    }


    @Override
    public T findOneTb(T model) {
        return mapper.selectOne(model);
    }

    @Override
    public T findOneObject(Object obj) {
        return mapper.selectOne(getObjectTb(obj));
    }

    @Override
    public <M> M findOneObject(Object obj, Class<M> m) {
        return BeanUtil.toModelClass(mapper.selectOne(getObjectTb(obj)), m);
    }

    @Override
    public T findOneMap(Map<String, Object> map) {
        try {
            T tbModel = modelClass.newInstance();
            for (String key : map.keySet()) {
                Field field = modelClass.getDeclaredField(key);
                field.setAccessible(true);
                field.set(tbModel, map.get(key));
            }
            return mapper.selectOne(tbModel);
        } catch (InstantiationException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public T findOneBy(String fieldName, Object value) {
        return mapper.selectOne(getFiledT(fieldName, value));
    }

    @Override
    public <M> M findOneBy(String fieldName, Object value, Class<M> m) {
        return BeanUtil.toModelClass(mapper.selectOne(getFiledT(fieldName, value)), m);
    }

    @Override
    public int findCountTb(T model) {
        return mapper.selectCount(model);
    }

    @Override
    public int findCountBy(String fieldName, Object value) {
        return mapper.selectCount(getFiledT(fieldName, value));
    }

    @Override
    public int findCountObject(Object obj) {
        return mapper.selectCount(getObjectTb(obj));
    }

    @Override
    public int findCountByCondition(Condition condition) {
        return mapper.selectCountByCondition(condition);
    }

    @Override
    public int findCountByConditionObject(Object obj) {
        return mapper.selectCountByCondition(getConditionObject(0));
    }

    @Override
    public int findCountByConditionMap(Map<String, Object> map) {
        return mapper.selectCountByCondition(getConditionMap(map));
    }

    @Override
    public List<T> findListByIds(String ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<T> findListByCondition(Condition condition) {
        return mapper.selectByCondition(condition);
    }

    @Override
    public <M> ArrayList<M> findListByCondition(Condition condition, Class<M> m) {
        List<T> list = mapper.selectByCondition(condition);
        if (list instanceof Page) {
            return BeanUtil.toModelListPages(list, m);
        }
        return BeanUtil.toModelList(list, m);
    }

    @Override
    public List<T> findListByObject(Object obj) {
        return mapper.selectByCondition(getConditionObject(obj));
    }

    @Override
    public <M> ArrayList<M> findListByObject(Object obj, Class<M> m) {
        return BeanUtil.toModelList(mapper.selectByCondition(getConditionObject(obj)), m);
    }

    @Override
    public List<T> findListByMap(Map<String, Object> map) {
        return mapper.selectByCondition(getConditionMap(map));
    }

    @Override
    public List<T> findListByOnly(String fieldName, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(fieldName, value);
        return mapper.selectByCondition(getConditionMap(map));
    }

    @Override
    public <M> ArrayList<M> findListByOnly(String fieldName, Object value, Class<M> m) {
        Map<String, Object> map = new HashMap<>();
        map.put(fieldName, value);
        return BeanUtil.toModelList(mapper.selectByCondition(getConditionMap(map)), m);
    }

    @Override
    public List<T> findListAll() {
        return mapper.selectAll();
    }

    @Override
    public String saveSelectiveId(T model) {
        try {
            String id = SnowflakeIdUtil.getSnowId();
            Field field = modelClass.getDeclaredField("id");
            field.setAccessible(true);
            field.set(model, id);
            mapper.insertSelective(model);
            return id;
        } catch (NoSuchFieldException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public String saveSelectiveIdObject(Object obj) {
        try {
            String id = SnowflakeIdUtil.getSnowId();
            T model = getObjectTb(obj);
            Field field = modelClass.getDeclaredField("id");
            field.setAccessible(true);
            field.set(model, id);
            mapper.insertSelective(model);
            return id;
        } catch (NoSuchFieldException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void saveSelectiveTb(T model) {
        mapper.insertSelective(model);
    }

    @Override
    public void saveSelectiveObject(Object obj) {
        mapper.insertSelective(getObjectTb(obj));
    }

    @Override
    public void saveOrUpdateKeySelective(Object obj) {
        mapper.insertOrUpdateKeySelective(getObjectTb(obj));
    }

    @Override
    public void saveOrUpdateKeyList(List<T> models) {
        mapper.insertOrUpdateList(models);
    }

    @Override
    public void saveOrUpdateKeyList(List<Object> models, Class<T> t) {
        mapper.insertOrUpdateList(BeanUtil.toModelList(models, t));
    }

    @Override
    public void saveListTb(List<T> models) {
        mapper.insertList(models);
    }

    @Override
    public void updateByKeyTb(T model) {
        mapper.updateByPrimaryKey(model);
    }

    @Override
    public void updateByKeySelectiveTb(T model) {
        mapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public void updateByKeySelectiveObject(Object obj) {
        mapper.updateByPrimaryKeySelective(getObjectTb(obj));
    }


    @Override
    public void updateBySelectiveTb(T model, Condition condition) {
        mapper.updateByConditionSelective(model, condition);
    }

    @Override
    public void updateBySelectiveObject(Object tbO, Object obj) {
        mapper.updateByConditionSelective(getObjectTb(tbO), getConditionObject(obj));
    }

    @Override
    public void updateBySelectiveMap(Object obj, Map<String, Object> map) {
        mapper.updateByConditionSelective(getObjectTb(obj), getConditionMap(map));
    }

    @Override
    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteByCondition(Condition condition) {
        mapper.deleteByCondition(condition);
    }

    @Override
    public void deleteBy(String fieldName, Object value) {
        mapper.deleteByCondition(getConditionObject(getFiledT(fieldName, value)));
    }

    @Override
    public void deleteByObject(Object obj) {
        mapper.deleteByCondition(getConditionObject(obj));
    }

    @Override
    public void deleteByMap(Map<String, Object> map) {
        mapper.deleteByCondition(getConditionMap(map));
    }


    @Override
    public void deleteByIds(String ids) {
        mapper.deleteByIds(ids);
    }

    @Override
    public void deleteBySelectCondition(Condition condition) {
        mapper.deleteBySelectCondition(condition);
    }

    /**
     * 通过属性获取查询条件
     *
     * @param fieldName
     * @param value
     * @return
     */
    private T getFiledT(String fieldName, Object value) {
        try {
            T model = modelClass.newInstance();
            Field field = modelClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
            return model;
        } catch (ReflectiveOperationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }


    private T getObjectTb(Object obj) {
        try {
            T tbModel = modelClass.newInstance();
            BeanUtils.copyProperties(obj, tbModel);
            return tbModel;
        } catch (InstantiationException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private Condition getConditionObject(Object obj) {
        try {
            T tbModel = modelClass.newInstance();
            BeanUtils.copyProperties(obj, tbModel);
            Condition condition = new Condition(tbModel.getClass());
            condition.createCriteria().andEqualTo(tbModel);
            return condition;
        } catch (InstantiationException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private Condition getConditionMap(Map<String, Object> map) {
        try {
            T modelTb = modelClass.newInstance();
            for (String key : map.keySet()) {
                Field field = modelClass.getDeclaredField(key);
                field.setAccessible(true);
                field.set(modelTb, map.get(key));
            }
            Condition condition = new Condition(modelTb.getClass());
            condition.createCriteria().andEqualTo(modelTb);
            return condition;
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
