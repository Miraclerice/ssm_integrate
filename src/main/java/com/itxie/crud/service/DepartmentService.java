package com.itxie.crud.service;

import com.itxie.crud.dao.DepartmentMapper;
import com.itxie.crud.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 小谢同学
 * @version 1.0
 * @date 2022/09/06  18:28:06
 **/

@Service
public class DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    public List<Department> getDepts() {
        List<Department> departments = departmentMapper.selectByExample(null);
        return departments;
    }
}
