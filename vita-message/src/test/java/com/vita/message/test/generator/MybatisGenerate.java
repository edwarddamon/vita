package com.vita.message.test.generator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * mybatis plus 类生成器
 *
 * @author Edward
 * @date 2024-02-28 15:07
 */
public class MybatisGenerate {

    public static final String URL = "jdbc:mysql://127.0.0.1:3306/demo?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";
    public static final List<String> TABLE_NAME_LIST = Arrays.asList("biz_email_record");

    public static void main(String[] args) {
        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> {
                    builder.author("Edward") // 设置作者
                            .dateType(DateType.TIME_PACK) // 指定日期类型为 LocalDateTime
//                            .fileOverride() // 覆盖已生成文件
                            .disableOpenDir()
                            .outputDir(System.getProperty("user.dir") + "/vita-message/src/test/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.vita.message.test.generator") // 设置父包名
                            .moduleName("generate") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/vita-message/src/test/resources/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude(TABLE_NAME_LIST) // 设置需要生成的表名
                            .addTablePrefix("sys_", "inf_", "biz_") // 设置过滤表前缀

                            .controllerBuilder()
                            .enableRestStyle() // 开启生成 RestController 控制器
                            .formatFileName("%sController")

                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")

                            .mapperBuilder()
                            .superClass(BaseMapper.class)
                            .enableMapperAnnotation()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")

                            .entityBuilder()
                            .enableLombok()
                            .disableSerialVersionUID()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableTableFieldAnnotation();
                })
                .templateEngine(new VelocityTemplateEngine()) // 使用Velocity引擎模板
                .execute();
    }

}
