package org.example.core

import org.example.beans.{Employee, EmployeeFinanceInfo}
import org.scalatest.FunSuite

import scala.util.Random

class EmployeeServiceTest extends FunSuite{

  val employeeService = new EmployeeService(TestUtil.generateExecutionContext())
  val employees = Employee.generateEmployees(20,40,50)
  val empIds = Random.shuffle(employees.toStream.map(x => x.id).toList)
  val empFinInfoList = EmployeeFinanceInfo.generateEmployeeFinInfo(empIds, 5000, 10000)

  test("EmployeeService.writeEmployees") {
    employeeService.writeEmployees(employees,"test_employee")
    val df = employeeService.ctx.spark.table("test_employee")
    assert(df.count() === 20)
  }

  test("EmployeeService.writeEmployeeFinInfo") {
    employeeService.writeEmployeeFinInfo(empFinInfoList, "test_emp_fin_info")
    val df = employeeService.ctx.spark.table("test_emp_fin_info")
    assert(df.count() === 20)
  }

  test("EmployeeService.findEmpWithAgeAndCTC") {
    val emps = TestUtil.generateCustomEmployees()
    val empsFinInfo = TestUtil.generateCustomEmployeesFinInfo()

    employeeService.writeEmployees(emps,"employee")
    employeeService.writeEmployeeFinInfo(empsFinInfo,"emp_finance_info")

    // test when age > 40 and ctc > 30k
    val res = employeeService.findEmpWithAgeAndCTC(40, 30000)
    assert(res.size === 2)

    // test when age > 30 and ctc > 70k
    val res2 = employeeService.findEmpWithAgeAndCTC(30, 70000)
    assert(res2.size === 0)
  }
}
