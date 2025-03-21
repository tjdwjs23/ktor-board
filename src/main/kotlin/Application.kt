package board.ktor

import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.schema.*

object Departments : Table<Nothing>("t_department") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val location = varchar("location")
}

object Employees : Table<Nothing>("t_employee") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val job = varchar("job")
    val managerId = int("manager_id")
    val hireDate = date("hire_date")
    val salary = long("salary")
    val departmentId = int("department_id")
}

//fun main(args: Array<String>) {
//    io.ktor.server.netty.EngineMain.main(args)
//}

fun main() {
    val database = Database.connect("jdbc:mysql://localhost:3306/template", user = "user", password = "user123")

    for (row in database.from(Employees).select()) {
        println(row[Employees.name])
    }
}

fun Application.module() {
    configureRouting()
}
