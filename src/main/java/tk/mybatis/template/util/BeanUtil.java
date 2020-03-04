package tk.mybatis.template.util;

import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import tk.mybatis.template.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxiaopeng
 * @description: bean操作类
 * @date 2020/1/6 18:43
 */
public class BeanUtil {
    /**
     * list类转换
     *
     * @param entities
     * @param modelBeanClass
     * @param <T>
     * @param <M>
     * @return
     * @throws Exception
     */
    public static <T, M> ArrayList<M> toModelList(List<T> entities, Class<M> modelBeanClass) {
        try {
            ArrayList<M> modelList = new ArrayList<M>();
            if (!EmptyUtil.isEmpty(entities)) {
                for (T entity : entities) {
                    M currentModel = modelBeanClass.newInstance();
                    modelList.add(toModel(entity, currentModel));
                }
            }
            return modelList;
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * list类转换带分页
     *
     * @param entities
     * @param modelBeanClass
     * @param <T>
     * @param <M>
     * @return
     * @throws Exception
     */
    public static <T, M> Page<M> toModelListPages(List<T> entities, Class<M> modelBeanClass) {
        try {
            ArrayList<M> modelList = new ArrayList<M>();
            Page<M> page = new Page<M>();
            if (!EmptyUtil.isEmpty(entities)) {
                for (T entity : entities) {
                    M currentModel = modelBeanClass.newInstance();
                    modelList.add(toModel(entity, currentModel));
                }
            }
            if (entities instanceof Page) {
                page = (Page) entities;
                page.clear();
                for (M mode : modelList) {
                    page.add(mode);
                }
            }else {
                throw new ServiceException("没有分页信息");
            }
            return page;
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public static <M, E> M toModel(E entity, M model) {
        if (model != null && entity != null) {
            BeanUtils.copyProperties(entity, model);
        } else {
            model = null;
        }
        return model;
    }

    public static <M, E> M toModelClass(E entity, Class<M> model) {
        try {
            M currentModel = model.newInstance();
            if (model != null && entity != null) {
                BeanUtils.copyProperties(entity, currentModel);
            } else {
                currentModel = null;
            }
            return currentModel;
        } catch (IllegalAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
