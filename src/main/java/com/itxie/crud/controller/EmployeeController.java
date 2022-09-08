package com.itxie.crud.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itxie.crud.entity.Employee;
import com.itxie.crud.entity.Msg;
import com.itxie.crud.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理员工crud
 *
 * @author 小谢同学
 * @version 1.0
 * @date 2022/09/06  9:02:22
 **/

@Controller
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;


    /**
     * 单个批量二合一删除
     * 批量删除 ：1-2-3
     * 单个删除:1
     *
     * @param ids id
     * @return {@link Msg}
     */
    @ResponseBody
    @RequestMapping(value = "/emp/{ids}", method = RequestMethod.DELETE)
    public Msg deleteEmpById(@PathVariable("ids") String ids) {
        if (ids.contains("-")) {
            //批量删除
            List<Integer> del_ids = new ArrayList<>();
            String[] str_ids = ids.split("-");
            for (String id : str_ids) {
                del_ids.add(Integer.parseInt(id));
            }
            employeeService.deleteBatch(del_ids);

        } else {
            //单个删除
            Integer id = Integer.parseInt(ids);
            employeeService.deleteEmp(id);
        }
        return Msg.success();
    }


    /**
     * 如果直接发送ajax请求ajax=PUT形式请求
     * 将要更新的员工数据：Employee{empId=1023, empName='null', gender='null', email='null', dId=null}
     * 问题：
     * 请求体中有数据
     * 的是Employee对象封装不上：
     * update t_emp  where emp_id = 1023;
     * <p>
     * 原因：
     * Tomcat：
     * 1.将请求体中的数据，封装一个map
     * 2.request。getParameter("empName")就会从这个map中取值
     * 3.springMVC封装pojo对象时，会把pojo每个属性的值，request.getParameter("eamil"）；
     * <p>
     * Ajax发送put请求引起血案：
     * put请求，请求体中的数据，request.getParameter("gender")拿不到
     * Tomcat一看是put不会封装请求体中的数据为map，只有post形式才封装请求体数据为map
     * org.apache.catalina.connector.Request -- parseParameters()
     * if( !getConnector().isParseBodyMethod(getMethod()) ) {
     * success = true;
     * return;
     * }
     * <p>
     * 要能1支持直接put请求还要封装请求体数据
     * 配置HttpPutFormContentFilter（但是过时了）
     * 作用：将请求体中数据解析包装一个map，
     * request被重新包装，request.getParameter()被重写，就会从直接封装的map中取数据
     * 员工更新方法
     *
     * @param employee 员工
     * @return {@link Msg}
     */
    @ResponseBody
    @RequestMapping(value = "/emp/{empId}", method = RequestMethod.PUT)
    public Msg saveEmp(Employee employee, HttpServletRequest request) {
//        System.out.println(request.getParameter("gender"));
//        System.out.println("将要更新的员工数据：" + employee);
        employeeService.updateEmp(employee);
        return Msg.success();
    }


    /**
     * 根据id查询员工
     *
     * @param id id
     * @return {@link Msg}
     */
    @RequestMapping(value = "/emp/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Msg getEmp(@PathVariable("id") Integer id) {
        Employee employee = employeeService.getEmp(id);
        return Msg.success().add("emp", employee);
    }

    /**
     * 检查用户
     * 检验用户名是否可用
     *
     * @param empName emp名字
     * @return {@link Msg}
     */
    @ResponseBody
    @RequestMapping("/checkUser")
    public Msg checkUser(@RequestParam("empName") String empName) {
        //先判断用户名是否合法的表达式
        String regex = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
        if (!empName.matches(regex)) {
            return Msg.fail().add("va_msg", "用户名必须是2-5位中文或6-16英文和数字组合！");
        }
        //数据库用户名1重复校验
        boolean b = employeeService.checkUser(empName);
        if (b) {
            return Msg.success();
        } else {
            return Msg.fail().add("va_msg", "用户名不可用");
        }
    }

    /**
     * 员工保存
     * 1.支持JSR303校验
     * 2.导入Hibernate-Validator
     *
     * @param employee
     * @return
     */
    @RequestMapping(value = "/emp", method = RequestMethod.POST)
    @ResponseBody
    public Msg saveEmp(@Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            //校验失败，应该返回失败，在静态框中显示校验失败错误信息
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                System.out.println("错误字段名：" + error.getField());
                System.out.println("错误信息：" + error.getDefaultMessage());
                map.put(error.getField(), error.getDefaultMessage());
            }
            return Msg.fail().add("errorField", map);
        } else {
            employeeService.saveEmp(employee);
            return Msg.success();
        }
    }

    /**
     * 导入jackson
     *
     * @param pn
     * @return page
     */
    @RequestMapping("/emps")
    @ResponseBody
    public Msg getEmpsWithJson(@RequestParam(value = "pn", defaultValue = "201") Integer pn) {
        //引入分页插件
        //在查询之前只需调用,传入页码以及分页的条数（大小）
        PageHelper.startPage(pn, 5);
        //startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        //使用pageInfo包装查询后的结果，只需将pageInfo交给页面就行
        //封装了详细的分页信息，包括我们查询出来的数据,传入连续显示页数
        PageInfo page = new PageInfo(emps, 5);
        return Msg.success().add("pageInfo", page);
    }

    /**
     * 查询员工数据(分页查询）
     *
     * @return
     */
    //@RequestMapping("/emps")
    public String getEmps(@RequestParam(value = "pn", defaultValue = "1") Integer pn, Model model) {
        //引入分页插件
        //在查询之前只需调用,传入页码以及分页的条数（大小）
        PageHelper.startPage(pn, 5);
        //startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        //使用pageInfo包装查询后的结果，只需将pageInfo交给页面就行
        //封装了详细的分页信息，包括我们查询出来的数据,传入连续显示页数
        PageInfo page = new PageInfo(emps, 5);
        model.addAttribute("pageInfo", page);
        return "list";
    }

}
