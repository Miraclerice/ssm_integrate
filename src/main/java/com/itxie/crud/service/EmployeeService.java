package com.itxie.crud.service;

import com.itxie.crud.dao.EmployeeMapper;
import com.itxie.crud.entity.Employee;
import com.itxie.crud.entity.EmployeeExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 小谢同学
 * @version 1.0
 * @date 2022/09/06  9:05:46
 **/

@Service
public class EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;

    /**
     * 根据员工id查询员工
     *
     * @param id
     * @return
     */
    public Employee getEmp(Integer id) {
        Employee employee = employeeMapper.selectByPrimaryKey(id);
        return employee;
    }

    /**
     * 查询所有员工
     *
     * @return
     */
    public List<Employee> getAll() {
        List<Employee> emps = employeeMapper.selectByExampleWithDept(null);
        return emps;
    }

    /**
     * 员工保存
     *
     * @param employee
     */
    public void saveEmp(Employee employee) {
        employeeMapper.insertSelective(employee);
    }

    /**
     * 校验用户名是否可用
     *
     * @param empName
     * @return true 代表可用
     */
    public boolean checkUser(String empName) {
        EmployeeExample employeeExample = new EmployeeExample();
        EmployeeExample.Criteria criteria = employeeExample.createCriteria();
        criteria.andEmpNameEqualTo(empName);
        long count = employeeMapper.countByExample(employeeExample);
        return count == 0;
    }


    /**
     * 更新employee
     *
     * @param employee 员工
     */
    public void updateEmp(Employee employee) {
        employeeMapper.updateByPrimaryKeySelective(employee);
    }

    /**
     * 删除emp
     *
     * @param id id
     */
    public void deleteEmp(Integer id) {
        employeeMapper.deleteByPrimaryKey(id);
    }

    /**
     * 删除批处理
     *
     * @param ids id
     */
    public void deleteBatch(List<Integer> ids) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        //删除集合的每个id对应员工
        criteria.andEmpIdIn(ids);
        employeeMapper.deleteByExample(example);
    }
}
