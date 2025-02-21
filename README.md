
# Module 1 Reflections

## Reflection 1

### Applied Principles
1. **Single Responsibility Principle**
   - Each class has a single, well-defined purpose
   - Clear separation between controllers, services, and repositories

2. **Interface Segregation**
   - Services defined through interfaces
   - Clear API contracts between layers

3. **Dependency Injection**
   - Spring's @Autowired for loose coupling
   - Easy to test and modify components

### Areas for Improvement
1. **Input Validation**
   - Need to add proper validation for product creation/updates
   - Implement form validation on both client and server side
   - Implementing a client and server side in general

2. **Error Handling**
   - Implement global exception handling
   - Create custom error pages

3. **Security**
   - Add CSRF protection
   - Implement input sanitization
   - Consider adding authentication

## Reflection 2

How do i feel in this reflection? defeated :(

### Unit Testing Insights
1. **Test Coverage Considerations**
   - While achieving 100% code coverage is valuable, it doesn't guarantee bug-free code
   - Coverage helps identify untested code paths but doesn't ensure all logical cases are tested
   - Our [`ProductServiceImplTest`](src/test/java/id/ac/ui/cs/advprog/eshop/service/ProductServiceImplTest.java) covers basic CRUD operations but could be expanded for edge cases

2. **Quality vs Quantity**
   - Unit tests should focus on testing meaningful scenarios rather than hitting an arbitrary number
   - Each test method should verify a specific behavior or use case
   - Current implementation includes positive and negative scenarios for product operations

3. **Test Organization**
   - Tests are organized following the AAA pattern (Arrange, Act, Assert)
   - Clear test method naming helps understand test purposes
   - Separate test classes for different components (Service, Repository, Controller)

### Functional Testing Improvements
1. **Code Duplication Issues**
   - Common setup code repeated across [`CreateProductFunctionalTest`](src/test/java/id/ac/ui/cs/advprog/eshop/functional/CreateProductFunctionalTest.java) and [`HomePageFunctionalTest`](src/test/java/id/ac/ui/cs/advprog/eshop/functional/HomePageFunctionalTest.java)
   - Similar instance variables and `@BeforeEach` methods duplicated

2. **Suggested Improvements**
   - Create a base functional test class with common setup code
   - Extract shared URL formatting logic
   - Use test fixtures for common test data
   - Example structure:
     ```java
     @TestInstance(TestInstance.Lifecycle.PER_CLASS)
     abstract class BaseFunctionalTest {
         @LocalServerPort
         protected int serverPort;
         
         @Value("${app.baseUrl:http://localhost}")
         protected String baseUrl;
         
         protected String createUrl(String path) {
             return String.format("%s:%d%s", baseUrl, serverPort, path);
         }
     }
     ```

3. **Benefits of Refactoring**
   - Reduced code duplication
   - Easier maintenance
   - More consistent testing approach
   - Better test organization

### Lessons Learned
1. High code coverage should not be the only goal of testing
2. Tests should be treated with the same code quality standards as production code
3. Regular refactoring of test code is important to maintain cleanliness
4. A good balance between unit and functional tests helps ensure application quality

# Module 2 Reflections 

## Reflection

### Code Quality Issues and Fixes

1. **SonarCloud Analysis Issues**
   - Implemented proper test coverage for CRUD operations in [`ProductRepositoryTest`](src/test/java/id/ac/ui/cs/advprog/eshop/repository/ProductRepositoryTest.java)
   - Added comprehensive test cases including positive and negative scenarios
   - Fixed code duplication in functional tests by following the suggested base test class pattern

2. **Docker Build Configuration**
   - Fixed permission issues with gradlew execution by adding `RUN chmod +x gradlew` in [Dockerfile](Dockerfile)
   - Implemented proper multi-stage build to reduce final image size
   - Configured correct working directory and file paths for the build process

### CI/CD Implementation Analysis

The current implementation meets the definition of CI/CD for the following reasons:

1. **Continuous Integration**
   - Automated test execution through GitHub Actions workflow in [sonarcloud.yml](.github/workflows/sonarcloud.yml)
   - Code quality checks using SonarCloud analysis
   - Immediate feedback on code quality and test results after each push

2. **Continuous Deployment**
   - Automated deployment to Koyeb PaaS platform
   - Docker container build and deployment on successful CI checks
   - Zero-touch deployment process from commit to production

3. **Quality Gates**
   - Code analysis through [scorecard.yml](.github/workflows/scorecard.yml) for security checks
   - Unit and functional tests run before deployment
   - Automated quality checks ensure only verified code reaches production
