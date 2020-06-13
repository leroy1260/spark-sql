package org.example

import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.example.beans.{Department, Employee, EmployeeDepartmentInfo, EmployeeFinanceInfo}
import org.example.core.{DepartmentService, EmployeeService}

import scala.util.Random

object App {
  def main(args: Array[String]): Unit = {
    new App(args).start()
  }
}

case class ExecutionContext(val spark: SparkSession)

class App(args: Array[String]) {
  private val log = Logger.getLogger(classOf[App])

  private def start(): Unit = {

    val start = System.currentTimeMillis
    log.info("Running Spark-sql Job")

    val filePath = "some/path/from/HDFS/or/AWS-S3"

    // Set the context(SparkContext)
    val spark = SparkSession.builder.master("local[*]").appName(" Spark-sql")
      .getOrCreate

    spark.sparkContext.setLogLevel("ERROR")

    val executionContext = ExecutionContext(spark)
    val employeeService = new EmployeeService(executionContext)
    val departmentService = new DepartmentService(executionContext)

      try {
        // Create Departments
        val departments = Department.createDepartments(List("IT","INFRA","FIN","HR","ADMIN"))
        val depIds = Random.shuffle(departments.toStream.map(x => x.id).toList)

        // Generate 1000 Employees with age between 18 and 60
        val emps = Employee.generateEmployees(1000, 18, 60)
        val empIds = Random.shuffle(emps.toStream.map(x => x.id).toList)

        // Generate Employee Finance information with CTC between 10k and 100k
        val empFinInfoList = EmployeeFinanceInfo.generateEmployeeFinInfo(empIds, 10000, 100000)

        // Divide employees into 2 parts and distribute departments as 2 in part1 / 3 in part2
        val part1Departments = List(depIds.head, depIds(1)) // 2 depts
        val part2Departments = List(depIds(2),depIds(3), depIds.last) // 3 depts

        val tempPart1Emps = EmployeeDepartmentInfo.generateDepartmentsForEmp(empIds.slice(0,500), part1Departments)
        val tempPart2Emps = EmployeeDepartmentInfo.generateDepartmentsForEmp(empIds.slice(500, 1000), part2Departments)

        // final EmployeeDepartment Info after distribution
        val employeeDepartmentInfo = (tempPart1Emps ++ tempPart2Emps)

        // SaveAll as Tables
        departmentService.writeDepartments(departments, "department")
        departmentService.writeDepartmentEmps(employeeDepartmentInfo, "department_employee")
        employeeService.writeEmployees(emps, "employee")
        employeeService.writeEmployeeFinInfo(empFinInfoList, "emp_finance_Info")

        // Query 1: find emp with age > 40 & ctc > 30,000
        val employees = employeeService.findEmpWithAgeAndCTC( 40, 30000)

        println("Employees with age and CTC\n==========================")
        employees.foreach(println)

        // Query 2: find dept with max emp with age > 35 & gratuity < 800
        val department = departmentService.findDeptWithAgeGratuity(35, 800)

        println("Department with maximum employees : " + department.getOrElse(0))
      } finally {
      spark.close()
    }
    val end = System.currentTimeMillis
    log.info("Finished Spark-sql Job in: " + (end - start) / 1000 + " seconds")

  }
}