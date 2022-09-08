
package com.itxie.crud.test;


import com.itxie.crud.dao.DepartmentMapper;
import com.itxie.crud.dao.EmployeeMapper;
import com.itxie.crud.entity.Department;
import com.itxie.crud.entity.Employee;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;

/**
 * 测试dao层工作
 *
 * @author 小谢同学
 * @version 1.0
 * @date 2022/09/05  19:01:52
 * 1 推荐使用spring项目就可以使用Spring单元测试，可以自动注入我们需要组件
 * 2 导入Spring-test模块
 * 3 @ContextConfiguration指定spring配置文件位置
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class MapperTest {
    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    SqlSession sqlSession;

    //测试DepartmentMapper


    @Test
    public void TestCRUD() {


//创建1springIOC容器
        /*ApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
        //2.从容器中获取mapper
        DepartmentMapper bean = ioc.getBean(DepartmentMapper.class);*/

        System.out.println(departmentMapper);

        //插入几个部门
//        departmentMapper.insertSelective(new Department(null, "开发部"));
//        departmentMapper.insertSelective(new Department(null, "测试部"));

        //2.生成员工数据，测试员工插入
//        employeeMapper.insertSelective(new Employee(null, "Mike", "男", "Mike@qq.com", 1));
        //3.批量插入多个员工：批量使用可以执行操作的sqlSession
        //可以使用for循环,不太好
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        for (int i = 0; i < 1000; i++) {
            String uid = UUID.randomUUID().toString().substring(0, 5) + i;
            mapper.insertSelective(new Employee(null, uid, "女",uid + "@qq.com",1));
        }
        System.out.println("批量完成！");

    }

}
