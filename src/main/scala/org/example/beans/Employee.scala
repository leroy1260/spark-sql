package org.example.beans

import scala.collection.mutable.ListBuffer
import scala.util.Random


case class Employee(id:Int, fname:String, lname:String = "NA", age:Int)


object Employee{

  def generateEmployees(total:Int, ageRangeFrom: Int, ageRangeTo:Int) = {

    val ages = ageRangeFrom to ageRangeTo
    var employeeList = new ListBuffer[Employee]()


    for (idx <- 1 to total) yield {
      employeeList.+= (Employee(idx, Random.alphanumeric.dropWhile(_.isDigit).take(7).mkString, age = ages.toVector(Random.nextInt(ages.size))))
    }

    employeeList.toSeq
  }


}