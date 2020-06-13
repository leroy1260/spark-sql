package org.example.beans

import scala.collection.mutable.ListBuffer
import scala.util.Random

case class EmployeeDepartmentInfo(empId:Int, departmentId:Int)

object EmployeeDepartmentInfo{
    def generateDepartmentsForEmp(empIds: List[Int], departments: List[Int]) = {
      var employeeDeptList = new ListBuffer[EmployeeDepartmentInfo]()

      for (empId <- empIds) yield {
        employeeDeptList.+= (EmployeeDepartmentInfo(empId,  departments.toVector(Random.nextInt(departments.size))))
      }

      employeeDeptList.toSeq
    }
}
