package org.example.beans

import scala.collection.mutable.ListBuffer
import scala.util.Random

case class EmployeeFinanceInfo(empId: Int, ctc: Int, basic: Int, pf: Int, gratuity: Int)

object EmployeeFinanceInfo {
  val BASIC_CALC = 0.20
  val PF = 0.10
  val GRATUITY = 0.05
  val empFinInfos = ListBuffer[EmployeeFinanceInfo]()

  def generateEmployeeFinInfo(empIds: Seq[Int], fromCTC: Int, toCTC: Int) = {

    val ctcs = fromCTC to toCTC by 500

    for (empId <- empIds) yield {
      val ctc = ctcs.toVector(Random.nextInt(ctcs.size))
      val basic = (ctc * BASIC_CALC).toInt
      val pf = (basic * PF).toInt
      val gratuity = (basic * GRATUITY).toInt
      empFinInfos.+= (EmployeeFinanceInfo(empId, ctc, basic, pf, gratuity))
    }

    empFinInfos.toSeq
  }
}

