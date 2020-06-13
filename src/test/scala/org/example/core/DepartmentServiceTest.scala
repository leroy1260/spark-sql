package org.example.core

import org.apache.spark.sql.Row
import org.example.beans.{Department, EmployeeDepartmentInfo}
import org.scalatest.FunSuite

import scala.util.Random

class DepartmentServiceTest extends FunSuite{

  val departmentService = new DepartmentService(TestUtil.generateExecutionContext())
  val employeeService = new EmployeeService(TestUtil.generateExecutionContext())
  val departments = Department.createDepartments(List("IT","INFRA","FIN"))
  val depIds = Random.shuffle(departments.toStream.map(x => x.id).toList)
  var deptIToCheck = 0
  val emps = TestUtil.generateCustomEmployees()
  val empIds = Random.shuffle(emps.toStream.map(x => x.id).toList)

  test("DepartmentService.writeDepartments") {
    departmentService.writeDepartments(departments, "department")
    val df = departmentService.ctx.spark.table("department")
    assert(df.count() === 3)
  }

  test("DepartmentService.writeDepartmentEmps") {
    employeeService.writeEmployees(emps, "employee")
    val distributedEmps = distributeEmpInDepts()
    departmentService.writeDepartmentEmps(distributedEmps, "department_employee")
    val df = departmentService.ctx.spark.table("department_employee")

    assert(df.count() === 5)
  }

  test("DepartmentService.findDeptWithAgeGratuity") {
    val emps = TestUtil.generateCustomEmployees()
    val empsFinInfo = TestUtil.generateCustomEmployeesFinInfo()

    departmentService.writeDepartments(departments, "departments")
    employeeService.writeEmployees(emps,"employee")
    employeeService.writeEmployeeFinInfo(empsFinInfo,"emp_finance_info")
    departmentService.writeDepartments(departments, "department")

    val distributedEmps = distributeEmpInDepts()
    departmentService.writeDepartmentEmps(distributedEmps, "department_employee")

    val res = departmentService.findDeptWithAgeGratuity(20, 800)
    val x = res.getOrElse(0).asInstanceOf[Row](0)
    assert(x === deptIToCheck)

    val res2 = departmentService.findDeptWithAgeGratuity(20, 100)
    assert(res2 === None)
  }

  def distributeEmpInDepts() = {
    // Divide employees into 2 parts and distribute departments as 2 in part1 / 3 in part2
    val part1Departments = List(depIds.head) // 1 depts
    deptIToCheck = part1Departments(0)

    val part2Departments = List(depIds(1), depIds.last) // 2 depts

    val tempPart1Emps = EmployeeDepartmentInfo.generateDepartmentsForEmp(empIds.slice(0,3), part1Departments)
    val tempPart2Emps = EmployeeDepartmentInfo.generateDepartmentsForEmp(empIds.slice(3, 5), part2Departments)

    // final EmployeeDepartment Info after distribution
    (tempPart1Emps ++ tempPart2Emps)
  }
}
