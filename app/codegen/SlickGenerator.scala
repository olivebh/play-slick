package codegen

object SlickGenerator extends App {

  slick.codegen.SourceCodeGenerator.main(
    Array(
      "slick.driver.MySQLDriver", // slick driver
      "com.mysql.jdbc.Driver", // JDBC driver
      "jdbc:mysql://localhost:3306/persons", // JDBC db URL
      "app/", // output folder, Play uses "app"
      "dao", // output package
      "groot", // username
      "groot" // password
    )
  )

}