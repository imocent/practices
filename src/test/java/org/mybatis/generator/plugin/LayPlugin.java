package org.mybatis.generator.plugin;

import org.apache.shiro.util.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.io.File;
import java.sql.Types;
import java.util.*;

public class LayPlugin extends PluginAdapter {
    private final List<String> ignoreTables = Arrays.asList("id");
    private final List<String> likeColumns = Arrays.asList("content", "name", "username", "title", "subjectId");
    private String targetBusine = "";
    private String javaProject = "src/main/java";

    /**
     * 生成代码执行方法
     */
    public static void main(String[] args) {
        String config = LayPlugin.class.getClassLoader().getResource("generatorConfig.xml").getFile();
        String[] arg = {"-configfile", config, "-overwrite"};
        ShellRunner.main(arg);
    }

    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String mapperType = introspectedTable.getMyBatis3JavaMapperType();
        if (mapperType != null) {
            String daoType = mapperType.replace("Mapper", "Dao");
            introspectedTable.setMyBatis3JavaMapperType(daoType);
        }
    }

    @Override
    public boolean clientGenerated(Interface face, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 移除所有默认生成的方法
        face.getMethods().clear();
        // 只添加你需要的自定义接口
        face.addSuperInterface(new FullyQualifiedJavaType("BaseCrudDao<" + introspectedTable.getBaseRecordType() + ">"));
        face.addImportedType(new FullyQualifiedJavaType("com.fit.base.BaseCrudDao"));
        face.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        face.addAnnotation("@Mapper");
        return true;
    }

    /**
     * 处理Base_Where_List中有时间的查询条件
     */
    private void handleDate(StringBuilder sb, IntrospectedColumn introspectedColumn, XmlElement where, XmlElement isNotNullElement) {
        String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
        String javaProperty = introspectedColumn.getJavaProperty();
        // 大于开始时间
        isNotNullElement.addAttribute(new Attribute("test", String.format("%s != null ", javaProperty)));
        where.addElement(isNotNullElement);
        sb.setLength(0);
        sb.append(" and ").append(columnName).append(String.format(" &gt;= #{%s }", javaProperty));
        isNotNullElement.addElement(new TextElement(sb.toString()));
        // 小于结束时间
        isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test", String.format("%s != null ", javaProperty)));
        where.addElement(isNotNullElement);
        sb.setLength(0);
        sb.append(" and ").append(columnName).append(String.format(" &lt;= #{%s } ", javaProperty));
        isNotNullElement.addElement(new TextElement(sb.toString()));
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement rootElement = document.getRootElement();
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id", "Base_Where_List"));
        XmlElement where = new XmlElement("where");
        // 加入 逻辑删除 del_flag标识 根据选择是否添加
        // where.addElement(new TextElement(" DEL_FLAG != 1 "));
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : allColumns) {
            XmlElement isNotNullElement = new XmlElement("if");
            String javaProperty = introspectedColumn.getJavaProperty();// java字段名
            if (!ignoreTables.contains(javaProperty)) {
                switch (introspectedColumn.getJdbcType()) {
                    case Types.DATE:
                    case Types.TIMESTAMP:
                        handleDate(sb, introspectedColumn, where, isNotNullElement);
                        break;
                    default:
                        sb.setLength(0);
                        sb.append(javaProperty).append(" != null and ").append(javaProperty).append(" != ''");
                        isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                        where.addElement(isNotNullElement);
                        sb.setLength(0);
                        sb.append(" and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                        if (likeColumns.contains(javaProperty)) {
                            sb.append(" like CONCAT('%',");
                            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)).append(",'%')");
                        } else {
                            sb.append(" = ").append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                        }
                        isNotNullElement.addElement(new TextElement(sb.toString()));
                }
            }
        }
        sql.addElement(where);
        rootElement.addElement(sql);
        // 统一引入参数
        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "Base_Column_List"));
        // 获取主键列名
        String primaryKey = introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName();
        // 获取表名
        String tableNameKey = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        // 创建根据Map查询数据
        XmlElement e_select = new XmlElement("select");
        e_select.addAttribute(new Attribute("id", "findList"));
        e_select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        e_select.addAttribute(new Attribute("parameterType", "java.util.Map"));
        e_select.addElement(new TextElement("SELECT "));
        e_select.addElement(include);
        e_select.addElement(new TextElement("FROM " + tableNameKey));
        XmlElement include2 = new XmlElement("include");
        include2.addAttribute(new Attribute("refid", "Base_Where_List"));
        e_select.addElement(include2);
        e_select.addElement(new TextElement(String.format("order by %s desc", primaryKey)));
        XmlElement pageIf = new XmlElement("if");
        pageIf.addAttribute(new Attribute("test", "page != null and limit != null"));
        pageIf.addElement(new TextElement("<bind name='offset' value='(page != null and page > 1) ? ((page - 1) * limit) : 0'/>"));
        pageIf.addElement(new TextElement("limit ${offset.intValue()}, ${limit}"));
        e_select.addElement(pageIf);
        rootElement.addElement(e_select);
        // 根据主键获取对象
        XmlElement b_select = new XmlElement("select");
        b_select.addAttribute(new Attribute("id", "getByObjId"));
        b_select.addAttribute(new Attribute("resultType", "java.lang.Object"));
        b_select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        b_select.addElement(new TextElement("SELECT "));
        b_select.addElement(include);
        b_select.addElement(new TextElement("FROM " + tableNameKey));
        b_select.addElement(new TextElement("where " + primaryKey + " = #{obj}"));
        rootElement.addElement(b_select);
        // 获取查询数量
        XmlElement c_select = new XmlElement("select");
        c_select.addAttribute(new Attribute("id", "findCount"));
        c_select.addAttribute(new Attribute("resultType", "java.lang.Integer"));
        c_select.addElement(new TextElement("SELECT count(1) FROM " + tableNameKey));
        c_select.addElement(include2);
        rootElement.addElement(c_select);
        // 创建批量添加
        XmlElement adds = new XmlElement("insert");
        adds.addAttribute(new Attribute("id", "batchAdd"));
        adds.addAttribute(new Attribute("parameterType", "java.util.List"));
        sb.setLength(0);
        sb.append("insert into ").append(tableNameKey).append(" values ");
        adds.addElement(new TextElement(sb.toString()));
        XmlElement foreachAdd = new XmlElement("foreach");
        foreachAdd.addAttribute(new Attribute("collection", "list"));
        foreachAdd.addAttribute(new Attribute("item", "id"));
        foreachAdd.addAttribute(new Attribute("open", "("));
        foreachAdd.addAttribute(new Attribute("separator", ","));
        foreachAdd.addAttribute(new Attribute("close", ")"));
        StringBuilder sbAdd = new StringBuilder();
        for (int i = 0; i < allColumns.size(); i++) {
            if ((i + 1) % 5 == 0) {
                sbAdd.append("\n       ");
            }
            IntrospectedColumn column = allColumns.get(i);
            sbAdd.append("#{item.").append(column.getJavaProperty()).append("}").append(",");
        }
        sbAdd.deleteCharAt(sbAdd.length() - 1);
        foreachAdd.addElement(new TextElement(sbAdd.toString()));
        adds.addElement(foreachAdd);
        rootElement.addElement(adds);
        // 创建批量删除
        XmlElement removes = new XmlElement("delete");
        removes.addAttribute(new Attribute("id", "batchDelete"));
        removes.addAttribute(new Attribute("parameterType", "java.util.List"));
        sb.setLength(0);
        sb.append("delete from ").append(tableNameKey).append(" where ").append(primaryKey).append(" in");
        removes.addElement(new TextElement(sb.toString()));
        XmlElement foreachDel = new XmlElement("foreach");
        foreachDel.addAttribute(new Attribute("collection", "list"));
        foreachDel.addAttribute(new Attribute("item", "id"));
        foreachDel.addAttribute(new Attribute("open", "("));
        foreachDel.addAttribute(new Attribute("separator", ","));
        foreachDel.addAttribute(new Attribute("close", ")"));
        foreachDel.addElement(new TextElement("#{id}"));
        removes.addElement(foreachDel);
        rootElement.addElement(removes);
        XmlElement sql_update = new XmlElement("update");
        sql_update.addAttribute(new Attribute("id", "deleteTable"));
        sql_update.addElement(new TextElement("truncate table " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));
        rootElement.addElement(sql_update);
        return true;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Attribute> attributes = element.getAttributes();
        for (Iterator<Attribute> it = attributes.iterator(); it.hasNext(); ) {
            Attribute a = it.next();
            if (a.getName().trim().equals("id")) {
                it.remove();
            }
        }
        element.addAttribute(new Attribute("id", "get"));
        Collections.reverse(attributes);
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Attribute> attributes = element.getAttributes();
        for (Attribute a : attributes) {
            if (a.getName().trim().equals("id")) {
                attributes.remove(a);
            }
        }
        element.addAttribute(new Attribute("id", "save"));
        element.addAttribute(new Attribute("keyColumn", "id"));
        element.addAttribute(new Attribute("keyProperty", "id"));
        element.addAttribute(new Attribute("useGeneratedKeys", "true"));
        Collections.reverse(attributes);
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Attribute> attributes = element.getAttributes();
        for (Attribute a : attributes) {
            if (a.getName().trim().equals("id")) {
                attributes.remove(a);
            }
        }
        element.addAttribute(new Attribute("id", "update"));
        Collections.reverse(attributes);
        return true;
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Attribute> attributes = element.getAttributes();
        for (Attribute a : attributes) {
            if (a.getName().trim().equals("id")) {
                attributes.remove(a);
            }
        }
        element.addAttribute(new Attribute("id", "delete"));
        Collections.reverse(attributes);
        return true;
    }

    /**
     * 生成Service层
     */
    public CompilationUnit getServiceClazz(IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();// 获取bean
        String destPackage = domainObjectName + "Service";
        String clazzType = introspectedTable.getBaseRecordType();// 获取bean路径
        String clazzName = clazzType.substring(clazzType.lastIndexOf(".") + 1);
        String daoInterfaceType = introspectedTable.getDAOInterfaceType().replace("DAO", "Dao");//获取DAO路径
        String daoName = daoInterfaceType.substring(daoInterfaceType.lastIndexOf(".") + 1);
        targetBusine = clazzType.substring(0, clazzType.lastIndexOf(".")).replace("entity", "service").replace("bean", "service");
        FullyQualifiedJavaType superClassType = new FullyQualifiedJavaType("BaseCrudService<" + daoName + "," + clazzName + ">");
        FullyQualifiedJavaType impClassType = new FullyQualifiedJavaType("com.fit.base.BaseCrudService");
        FullyQualifiedJavaType impServiceType = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        FullyQualifiedJavaType beanType = new FullyQualifiedJavaType(clazzType);
        FullyQualifiedJavaType daoType = new FullyQualifiedJavaType(daoInterfaceType);
        TopLevelClass dto = new TopLevelClass(destPackage);
        dto.addFileCommentLine("package " + targetBusine + ";\n");
        dto.addImportedType(impServiceType);
        dto.setSuperClass(superClassType);
        dto.addImportedType(impClassType);
        dto.addImportedType(beanType);
        dto.addImportedType(daoType);
        dto.setVisibility(JavaVisibility.PUBLIC);
        dto.addAnnotation("@Service");
        return dto;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> list = new ArrayList<GeneratedJavaFile>();
        CompilationUnit addServiceClazz = getServiceClazz(introspectedTable);
        String targetProject = javaProject + File.separator + targetBusine.replace(".", "/");
        File file = new File(targetProject);
        // 文件不存在
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        list.add(new GeneratedJavaFile(addServiceClazz, targetProject, this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter()));

        return list;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.setSuperClass(new FullyQualifiedJavaType("BaseEntity<" + introspectedTable.getBaseRecordType() + ">"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.fit.base.BaseEntity"));
        Iterator<Field> fieldIterator = topLevelClass.getFields().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            if (ignoreTables.contains(field.getName())) {
                fieldIterator.remove();
            }
        }
        this.addLombokAnnotation(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        // 获取字段注释
        String columnComment = introspectedColumn.getRemarks();
        if (StringUtils.hasLength(columnComment)) {
            // 在字段上方添加 JavaDoc 注释
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + columnComment);
            field.addJavaDocLine(" */");
        }

        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.addLombokAnnotation(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.addLombokAnnotation(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.addLombokAnnotation(topLevelClass, introspectedTable);
        return true;
    }

    private void addLombokAnnotation(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("lombok.*");
        topLevelClass.addAnnotation("@Data");
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        //使用反射在运行时把'isMergeable'强制改成false
        java.lang.reflect.Field field = null;
        try {
            field = sqlMap.getClass().getDeclaredField("isMergeable");
            field.setAccessible(true);
            field.setBoolean(sqlMap, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }
}