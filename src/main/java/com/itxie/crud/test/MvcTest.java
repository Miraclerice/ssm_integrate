package com.itxie.crud.test;

import com.github.pagehelper.PageInfo;
import com.itxie.crud.entity.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

/**
 * 用spring测试模块提供的测试请求功能，测试crud请求的正确性
 *
 * @author 小谢同学
 * @version 1.0
 * @date 2022/09/06  9:27:11
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:springMVC.xml"})
public class MvcTest {
    //传入SpringMVC的ioc
    @Autowired
    WebApplicationContext context;
    //虚拟mvc请求，获取处理1结果
    MockMvc mockMvc;

    @Before
    public void initMockMvc() {
        //这里快速补全代码不要新创建一个对象，否则mockMvc会报空指针异常
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testPage() throws Exception {
        //模拟请求获得返回值
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/emps").param("pn", "4")).andReturn();

        //请求成功以后，请求域中会有pageInfo，我们可以取出pageInfo进行验证
        MockHttpServletRequest request = result.getRequest();
        PageInfo pi = (PageInfo) request.getAttribute("pageInfo");
        System.out.println("当前页码 ： " + pi.getPageNum());
        System.out.println("总页码 ： " + pi.getPages());
        System.out.println("总记录数 ： " + pi.getTotal());
        System.out.println("在页面需要连续显示的页码 ： ");
        int[] nums = pi.getNavigatepageNums();
        for (int i : nums) {
            System.out.println(" " + i);
        }
        //获取员工数据
        List<Employee> list = pi.getList();
        list.forEach(employee -> {
            System.out.println("Id: " + employee.getEmpId() + "/tName : " + employee.getEmpName());
        });
    }
}
