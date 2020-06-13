package org.example.core

import org.apache.spark.sql.functions.{col, count, expr, max}
import org.apache.spark.sql.{Row, SaveMode}
import org.example.ExecutionContext
import org.example.beans.{Department, EmployeeDepartmentInfo}

class DepartmentService(context: ExecutionContext) {

  val ctx = context

  def findDeptWithAgeGratuity(age: Int, gratuity: Int) : Option[Row] = {
    val deptEmployeeInfo = ctx.spark.table("department_employee")
    val departments = ctx.spark.table("department")

    var depFound = None: Option[Row]

    // Step 1: Filter employee partitoned table by age > 35
    val employeesGt35  = ctx.spark.table("employee")
      .filter(col("age") > age)

    if (employeesGt35.count() > 0) {
      // Step 2: Filter employees from employee_financeInfo partitioned table by gratuity < 800
      val gratuityLt800 = ctx.spark.table("emp_finance_info")
        .filter(col("gratuity") < gratuity)
        .cache()

      if (gratuityLt800.count() > 0) {
        // Step 3: Get all employees with age > 35 and gratuity < 800
        val filteredEmployees = employeesGt35.join(gratuityLt800, expr("id = empid"))
          .drop("empid")
          .cache()

        // Step 4: get count of employees per department.
        val employeeCountPerDept = deptEmployeeInfo.join(filteredEmployees, expr("empId = id"))
          .groupBy("departmentId")
          .agg(count("empId").as("employee_count"))

        // Step 5: Get department with maximum employees
        val department = departments.join(employeeCountPerDept, expr("id = departmentId"))
          .drop("departmentId")
          .orderBy(col("employee_count").desc)
          .first()

        depFound = Some(department)
      }
    }
    depFound
  }

  def writeDepartmentEmps(employeeDepartmentInfo: Seq[EmployeeDepartmentInfo], tableName: String) = {
    import context.spark.implicits._

    employeeDepartmentInfo.toDF()
      .write
      .mode(SaveMode.Overwrite)
      .partitionBy("departmentId")
      .bucketBy(5, "empId")
      .sortBy("empId")
      .format("parquet")
      .saveAsTable(tableName)
  }

  def writeDepartments(departments: Seq[Department], tableName: String) = {
    import context.spark.implicits._

    departments.toDF()
      .write
      .mode(SaveMode.Overwrite)
      .format("parquet")
      .saveAsTable(tableName)
  }

}
