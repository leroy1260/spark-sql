# spark-sql
Project to demonstrate usage of Spark-sql along with partitioning and bucketing

# Requirement:

1) create 5 department(IT, INFRA, HR, ADMIN, FIN)
2) Write Code to generate 1000 Emp data(unique first name), with age between 18 - 60
3) Write code to generate emp finance info for each emp with CTC between 10,000 - 100,000, basic 20% CTC, PF 10% of basic, gratuity 5% of basic.
4) Distribute these 1000 emp to 5 departments, 500 emp works in 2 dept, rest 500 in 3 dept.
5) Write a program to find emp with age > 40 & ctc > 30,000.
6) Write a program to find dept with max emp with age > 35 & gratuity < 800.

### Prerequisites

```
Scala 2.11.12 with SBT 1.3.6
scalatest 3.0.1
Spark 2.3.0
```

### Design and Assumption
* **App** is the main class or the Client who requests for the operations to be performed.
* **EmployeeService** is a DTO that performs operations like create/filter Employees and EmployeeFinance information
* **DepartmentService** is a DTO that performs operations like create/filter Departments.

### What can be done better ?
1) Error handling.
2) Logging.
3) More verbose negative test-cases.