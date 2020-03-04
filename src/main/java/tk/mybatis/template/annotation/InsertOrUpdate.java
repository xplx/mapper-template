package tk.mybatis.template.annotation;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.*;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Set;

/**
 * @author wuxiaopeng
 * @date 2020-03-02
 */
public class InsertOrUpdate extends MapperTemplate {
    public InsertOrUpdate(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String insertOrUpdateKeySelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        EntityColumn logicDeleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);
        processKey(sql, entityClass, ms, columnList);
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (column.isIdentity()) {
                sql.append(column.getColumn()).append(",");
            } else {
                if (logicDeleteColumn != null && logicDeleteColumn == column) {
                    sql.append(column.getColumn()).append(",");
                    continue;
                }
                sql.append(SqlHelper.getIfNotNull(column, column.getColumn() + ",", isNotEmpty()));
            }
        }
        sql.append("</trim>");
        sql.append("<trim prefix=\"VALUES(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (logicDeleteColumn != null && logicDeleteColumn == column) {
                sql.append(SqlHelper.getLogicDeletedValue(column, false)).append(",");
                continue;
            }
            //优先使用传入的属性值,当原属性property!=null时，用原属性
            //自增的情况下,如果默认有值,就会备份到property_cache中,所以这里需要先判断备份的值是否存在
            if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheNotNull(column, column.getColumnHolder(null, "_cache", ",")));
            } else {
                //其他情况值仍然存在原property中
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder(null, null, ","), isNotEmpty()));
            }
            //当属性为null时，如果存在主键策略，会自动获取值，如果不存在，则使用null
            //序列的情况
            if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheIsNull(column, column.getColumnHolder() + ","));
            }
        }
        getUpdateParam(entityClass, sql);
        return sql.toString();
    }

    private void getUpdateParam(Class<?> entityClass, StringBuilder sql) {
        sql.append("</trim>");
        sql.append(" on duplicate key update ");
        sql.append(" <trim suffixOverrides=\",\"> ");
        //update
        String updateSql = SqlHelper.updateSetColumns(entityClass, null, true, isNotEmpty());
        updateSql = updateSql.replace("<set>", "").replace("</set>", "");
        sql.append(updateSql);
        sql.append("</trim>");
    }

    private void processKey(StringBuilder sql, Class<?> entityClass, MappedStatement ms, Set<EntityColumn> columnList) {
        //Identity列只能有一个
        Boolean hasIdentityKey = false;
        //先处理cache或bind节点
        for (EntityColumn column : columnList) {
            if (column.isIdentity()) {
                //这种情况下,如果原先的字段有值,需要先缓存起来,否则就一定会使用自动增长
                //这是一个bind节点
                sql.append(SqlHelper.getBindCache(column));
                //如果是Identity列，就需要插入selectKey
                //如果已经存在Identity列，抛出异常
                if (hasIdentityKey) {
                    //jdbc类型只需要添加一次
                    if (column.getGenerator() != null && "JDBC".equals(column.getGenerator())) {
                        continue;
                    }
                    throw new MapperException(ms.getId() + "对应的实体类" + entityClass.getCanonicalName() + "中包含多个MySql的自动增长列,最多只能有一个!");
                }
                //插入selectKey
                SelectKeyHelper.newSelectKeyMappedStatement(ms, column, entityClass, isBEFORE(), getIDENTITY(column));
                hasIdentityKey = true;
            } else if (column.getGenIdClass() != null) {
                sql.append("<bind name=\"").append(column.getColumn()).append("GenIdBind\" value=\"@tk.mybatis.mapper.genid.GenIdUtil@genId(");
                sql.append("_parameter").append(", '").append(column.getProperty()).append("'");
                sql.append(", @").append(column.getGenIdClass().getCanonicalName()).append("@class");
                sql.append(", '").append(tableName(entityClass)).append("'");
                sql.append(", '").append(column.getColumn()).append("')");
                sql.append("\"/>");
            }
        }
    }

    /**
     * 批量插入或更新
     *
     * @param ms
     */
    public String insertOrUpdateList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);

        //开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"listNotEmptyCheck\" value=\"@tk.mybatis.mapper.util.OGNL@notEmptyCollectionCheck(list, '" + ms.getId() + " 方法参数为空')\"/>");
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass), "list[0]"));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));

        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            //其他情况值仍然存在原property中
            sql.append(getIfNotNull(column, column.getColumnHolder("record", null, ","), isNotEmpty()));
        }
        sql.append("</trim>");
        sql.append("</foreach>");

        // 反射把MappedStatement中的设置主键名
        EntityHelper.setKeyProperties(EntityHelper.getPKColumns(entityClass), ms);

        //update语句
        sql.append(" on duplicate key update ");
        sql.append("<trim suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            sql.append(this.getColumnHolder(column.getProperty(), ","));
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}+separator
     *
     * @param entityName
     * @param separator
     * @return
     */
    public String getUpdateColumnHolder(String entityName, String separator) {
        StringBuffer sb = new StringBuffer();
        if (StringUtil.isNotEmpty(entityName)) {
            sb.append(entityName + "=");
        }
        if (StringUtil.isNotEmpty(entityName)) {
            sb.append("#{");
            sb.append("record." + entityName);
            sb.append("} ");
        }
        if (StringUtil.isNotEmpty(separator)) {
            sb.append(separator);
        }
        return sb.toString();
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}+separator
     *
     * @param entityName
     * @param separator
     * @return
     */
    public String getColumnHolder(String entityName, String separator) {
        StringBuffer sb = new StringBuffer();
        if (StringUtil.isNotEmpty(entityName)) {
            sb.append(entityName + "=");
        }
        if (StringUtil.isNotEmpty(entityName)) {
            sb.append("VALUES (");
            sb.append(entityName);
            sb.append(" )");
        }
        if (StringUtil.isNotEmpty(separator)) {
            sb.append(separator);
        }
        return sb.toString();
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public static String getIfNotNull(EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        sql.append("record." + column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" and ");
            sql.append("record." + column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }
}
