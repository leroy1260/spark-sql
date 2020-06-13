package org.example.core

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._
import org.example.ExecutionContext
import org.example.beans.{Employee, EmployeeFinanceInfo}

class EmployeeService(context: ExecutionContext) {
  val ctx = context

  def findEmpWithAgeAndCTC(age: Int, ctc: Int) = {

    import context.spark.implicits._
    var empsFound = List.empty[String]

    // Step 1: Filter employee partitioned table by age > 40
    val employeesGt40 = ctx.spark.table("employee")
        .filter(col("age") > age)

    if (employeesGt40.count() > 0) {
      // Step 2: Filter employees from employee_financeInfo partitioned table by ctc > 30000
      val ctcGt30K = ctx.spark.table("emp_finance_info")
        .filter(col("ctc") > ctc)

      if (ctcGt30K.count() > 0) {
        // step 3: Join and get all employees based on employee Ids from both dataframe.
        empsFound = employeesGt40.join(ctcGt30K, expr("id = empid"))
          .select("fname","age", "ctc")
          .map(row => row.getString(0) + ", " + row.getInt(1) + ", " + row.getInt(2) )
          .collect().toList
      }
    }
    empsFound
  }

  def writeEmployeeFinInfo(empFinInfoList: Seq[EmployeeFinanceInfo], tableName: String) = {
    import context.spark.implicits._

    empFinInfoList.toDF()
      .write
      .mode(SaveMode.Overwrite)
      .partitionBy("ctc","gratuity")
      .bucketBy(5, "empId")
      .sortBy("empId")
      .format("parquet")
      .saveAsTable(tableName)
  }

  def writeEmployees(emps: Seq[Employee], tableName: String) = {
    import context.spark.implicits._

    emps.toDF()
      .write
      .mode(SaveMode.Overwrite)
      .partitionBy("age")
      .format("parquet")
      .saveAsTable(tableName)
  }

}