package tk.mybatis.template.annotation;

import org.apache.ibatis.annotations.DeleteProvider;

/**
 * @author wuxiaoepng
 * @date 2020-03-02
 */
@tk.mybatis.mapper.annotation.RegisterMapper
public interface DeleteBySelectMapper<T> {
    /**
     * 根据Condition条件进行查询，删除获取到的id数据
     *
     * @param condition
     * @return
     */
    @DeleteProvider(type = DeleteBySelect.class, method = "dynamicSQL")
    int deleteBySelectCondition(Object condition);
}
