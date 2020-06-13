package org.example.beans

import scala.collection.mutable.ListBuffer

case class Department(id:Int, name:String)

object Department {
  var depListBuffer = ListBuffer[Department]()
  def createDepartments(departments : List[String]) ={
      for (department <-  departments) yield depListBuffer.+= (Department(department.hashCode, department))

    depListBuffer.toSeq
  }
}
