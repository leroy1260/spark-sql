package org.example.core

import org.apache.spark.sql.SparkSession
import org.example.ExecutionContext
import org.example.beans.{Employee, EmployeeFinanceInfo}

class TestUtil {
  import TestUtil._
  val context = generateExecutionContext()
}

object TestUtil{
  def generateExecutionContext() = {
    val spark = SparkSession.builder.master("local[*]").appName(" Spark-sql")
      .getOrCreate
    spark.sparkContext.setLogLevel("ERROR")

    ExecutionContext(spark)
  }

  def generateCustomEmployees() = {
    val emps = Seq(
      (Employee(1,"Leroy","NA",41)),
      (Employee(2,"Aguero","NA",32)),
      (Employee(3,"Silva","NA",26)),
      (Employee(4,"Bravo","NA",45)),
      (Employee(5,"Daniel","NA",35))
    )
    emps
  }

  def generateCustomEmployeesFinInfo() = {
    val empsFinInfo = Seq(
      (EmployeeFinanceInfo(1,70000,(70000 * 0.20).toInt, (((70000 * 0.20).toInt)*0.10).toInt, (((70000 * 0.20).toInt)*0.05).toInt)),
      (EmployeeFinanceInfo(2,30000,(30000 * 0.20).toInt, (((30000 * 0.20).toInt)*0.10).toInt, (((30000 * 0.20).toInt)*0.05).toInt)),
      (EmployeeFinanceInfo(3,40500,(40500 * 0.20).toInt, (((40500 * 0.20).toInt)*0.10).toInt, (((40500 * 0.20).toInt)*0.05).toInt)),
      (EmployeeFinanceInfo(4,65000,(65000 * 0.20).toInt, (((65000 * 0.20).toInt)*0.10).toInt, (((65000 * 0.20).toInt)*0.05).toInt)),
      (EmployeeFinanceInfo(5,25000,(25000 * 0.20).toInt, (((25000 * 0.20).toInt)*0.10).toInt, (((25000 * 0.20).toInt)*0.05).toInt))
    )
    empsFinInfo
  }
}
