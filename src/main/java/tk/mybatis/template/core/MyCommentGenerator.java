package tk.mybatis.template.core;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.internal.util.StringUtility;
import tk.mybatis.mapper.generator.MapperCommentGenerator;

import java.util.Iterator;

/**
 * 重写MapperCommentGenerator类的方法
 * @author wuxiaopeng
 */
public class MyCommentGenerator extends MapperCommentGenerator {
    private String beginningDelimiter = "";
    private String endingDelimiter = "";
    private boolean forceAnnotation;
    private boolean needsSwagger;

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();

        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedColumn.getRemarks());
        field.addJavaDocLine(sb.toString());

        //addJavadocTag(field, false);

        field.addJavaDocLine(" */");
        String defaultValue = introspectedColumn.getDefaultValue();
        if(defaultValue==null || defaultValue.length()==0){
            field.addJavaDocLine("@ApiModelProperty(value = \""+introspectedColumn.getRemarks()+"\")");
        }else{
            field.addJavaDocLine("@ApiModelProperty(value = \""+introspectedColumn.getRemarks()+"\", example=\""+defaultValue+"\")");
        }

        if(!introspectedColumn.isNullable()){
            if(introspectedColumn.getFullyQualifiedJavaType().toString().equals("java.lang.String")){
                field.addJavaDocLine("@NotEmpty(message=\""+introspectedColumn.getRemarks()+"不能为空\")");
            }else{
                field.addJavaDocLine("@NotNull(message=\""+introspectedColumn.getRemarks()+"不能为空\")");
            }
        }

        Iterator var7 = introspectedTable.getPrimaryKeyColumns().iterator();

        while(var7.hasNext()) {
            IntrospectedColumn column = (IntrospectedColumn)var7.next();
            if (introspectedColumn == column) {
                field.addAnnotation("@Id");
                break;
            }
        }

        String column = introspectedColumn.getActualColumnName();
        if (StringUtility.stringContainsSpace(column) || introspectedTable.getTableConfiguration().isAllColumnDelimitingEnabled()) {
            column = introspectedColumn.getContext().getBeginningDelimiter() + column + introspectedColumn.getContext().getEndingDelimiter();
        }

        if (!column.equals(introspectedColumn.getJavaProperty())) {
            field.addAnnotation("@Column(name = \"" + this.getDelimiterName(column) + "\")");
        } else if (!StringUtility.stringHasValue(this.beginningDelimiter) && !StringUtility.stringHasValue(this.endingDelimiter)) {
            if (this.forceAnnotation) {
                field.addAnnotation("@Column(name = \"" + this.getDelimiterName(column) + "\")");
            }
        } else {
            field.addAnnotation("@Column(name = \"" + this.getDelimiterName(column) + "\")");
        }
    }
}