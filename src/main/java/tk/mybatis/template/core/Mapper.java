package tk.mybatis.template.core;



import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;
import tk.mybatis.template.annotation.DeleteBySelectMapper;
import tk.mybatis.template.annotation.InsertOrUpdateMapper;

/**
 * 定制版MyBatis Mapper插件接口，如需其他接口参考官方文档自行添加。
 * @author wuxiaopeng
 * @date 2020-03-03
 */
public interface Mapper<T>
        extends
        BaseMapper<T>,
        ConditionMapper<T>,
        IdsMapper<T>,
        InsertListMapper<T>,
        DeleteBySelectMapper<T>,
        InsertOrUpdateMapper<T> {
}
