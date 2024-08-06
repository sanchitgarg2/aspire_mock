# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.2/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.2/maven-plugin/build-image.html)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.3.2/reference/htmlsingle/index.html#appendix.configuration-metadata.annotation-processor)

### Maven Parent overrides



This is a Spring Boot application and the easiest way to run the app as well as to review the code is to import it into IntelliJ or any other Java IDE.
to run it from the CLI, you can use the following command :
The
```shell
java -jar MockAspireLoanApplication-0.0.1-SNAPSHOT.jar takehomeassignments.aspire.mockaspireloanapplication.MockAspireLoanApplication
```

Using this section to document the design decisions and the reasons behind them.

Why did I choose to create AdminUserController and AdminService as separate classes
    Functionally it is a very different business logic to create the admin users - this will be done by HR along with the Finance team
    Whereas the Operations such as approve or reject is quite different. This is why I chose to separate the two classes.
    As the app would expand, any operations that acted ON approvers(admin usrs) would go into the AdminUserController and any operations that are acted BY the approvers would go into the AdminController

Why did I choose H2 as the database and why in memory implementation of it? 
    H2 is lightweight and easy to use. It also has an easily accessible console accessible at 
    http://localhost:8080/h2-console
        jdbc url :jdbc:h2:mem:mock_aspire_loan_application_db
        username: soda
        password: pierogi
    I used in memory because in such applications it is best to test different cases from the beginning and this way we don't have to worry about stale DB data.
    If persistence is still needed though, then it is easy to implement via a simple change in the application.properties file.

User service authentication -
    Implemented via bearer tokens
    To keep it simple I have implemented a DB based auth -
    Nut in a real application I'd use a 3P provider like Auth0 or Cognito -
    They provide real time traffic analysis as well which is a really useful feature.

A Fancy add Repayment API 
    In this app , i have tried to make it as user friendly towards borrowers as possible.
    They can choose to repay any amount they want to, and the system will automatically recalculate the amounts on the remaining EMIs.
    If they pay more than needed, the future installments will become cheaper
    If they pay less, the future installments will become more expensive.
    Since we are not a bank, I do not allow users to pay us money more than the present value of all future Installments. 
        This also lets us get away without having to build a refund logic in the system ( although for operational purposes, we should still have one)

Notifications Service