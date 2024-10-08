1. Spring Testing Annotations
    1. @ContextConfiguration
        - Defines class-level metadata that is used to determine how to load and configure ApplicationContext for integration tests. Specifically @ContextConfiguration declares the application context resource locations or the component classes used to load the context.
    
    2. @WebAppConfiguration
        - @WebAppConfiguration declares that the ApplicationContext loaded for an integration test should be WebApplicationContext
        - must be used in conjunction with @ContextConfiguration

    3. @TestPropertySource
        - used to configure locations of properties files, e.g. @TestPropertySource("/test.properties") 
        - you can also inline properties @TestPropertySource(properties = { "timezone = GMT", "port: 4242" }) 

    4. @DynamicPropertySource

    5. @DirtiesContext
        - indicates that the underlying Spring ApplicationContext has been dirtied during the execution of a test (that is, the test modified or corrupted it in some manner — for example, by changing the state of a singleton bean) and should be closed. When an application context is marked as dirty, it is removed from the testing framework’s cache and closed. As a consequence, the underlying Spring container is rebuilt for any subsequent test that requires a context with the same configuration metadata.

    6. @Commit
        - indicates that the transaction for a transactional test method should be committed after the test method has completed
        - You can use @Commit as a direct replacement for @Rollback(false) to more explicitly convey the intent of the code

    7. @Rollback
        - indicates whether the transaction for a transactional test method should be rolled back after the test method has completed
        - if true, the transaction is rolled back
        - if false, the transaction is committed
        - Rollback for integration tests in the Spring TestContext Framework defaults to true even if @Rollback is not explicitly declared

    8. @BeforeTransaction / @AfterTransaction
        - @BeforeTransaction indicates that the annotated void method should be run before a transaction is started, for test methods that have been configured to run within a transaction by using Spring’s @Transactional annotation
    
2. Spring JUnit 4 Testing Annotations
    The following annotations are supported only when used in conjunction with the SpringRunner (@RunWith(SpringJUnit4ClassRunner.class), @RunWith(SpringRunner.class)), Spring’s JUnit 4 rules (SpringClassRule, SpringMethodRule), or Spring’s JUnit 4 support classes (AbstractJUnit4SpringContextTests, AbstractTransactionalJUnit4SpringContextTests)

    1. @IfProfileValue
        - You can apply @IfProfileValue at the class- level, the method level, or both.

    2. @Timed
        - @Timed indicates that the annotated test method must finish execution in a specified time period (in milliseconds). If the text execution time exceeds the specified time period, the test fails.
        - The time period includes running the test method itself, any repetitions of the test (see @Repeat), as well as any setting up or tearing down of the test fixture. 
        - Spring’s @Timed annotation has different semantics than JUnit 4’s @Test(timeout=…​) support. Specifically, due to the manner in which JUnit 4 handles test execution timeouts (that is, by executing the test method in a separate Thread), @Test(timeout=…​) preemptively fails the test if the test takes too long. Spring’s @Timed, on the other hand, does not preemptively fail the test but rather waits for the test to complete before failing.

    3. @Repeat
        - @Repeat indicates that the annotated test method must be run repeatedly. The number of times that the test method is to be run is specified in the annotation.
        - The scope of execution to be repeated includes execution of the test method itself as well as any setting up or tearing down of the test fixture. 

3. Spring JUnit Jupiter Testing Annotations
    The following annotations are supported when used in conjunction with the SpringExtension and JUnit Jupiter (that is, the programming model in JUnit 5)

    1. @SpringJUnitConfig
        - @SpringJUnitConfig is a composed annotation that combines @ExtendWith(SpringExtension.class) from JUnit Jupiter with @ContextConfiguration from the Spring TestContext Framework. It can be used at the class- level as a drop-in replacement for @ContextConfiguration

    2. @SpringJUnitWebConfig
        - @SpringJUnitWebConfig is a composed annotation that combines @ExtendWith(SpringExtension.class) from JUnit Jupiter with @ContextConfiguration and @WebAppConfiguration from the Spring TestContext Framework. 

    3. @TestConstructor
        - @TestConstructor is a type-level annotation that is used to configure how the parameters of a test class- constructor are autowired from components in the test’s ApplicationContext

    4. @EnabledIf / @DisabledIf
        - Expressions can be any of the following:
            * Spring Expression Language (SpEL) expression. For example: @EnabledIf("#{systemProperties['os.name'].toLowerCase().contains('mac')}")
            * Placeholder for a property available in the Spring Environment. For example: @EnabledIf("${smoke.tests.enabled}")
            * Text literal. For example: @EnabledIf("true")
        - You can use @EnabledIf as a meta-annotation to create custom composed annotations. For example, you can create a custom @EnabledOnMac annotation as follows:
            @Target({ElementType.TYPE, ElementType.METHOD})
            @Retention(RetentionPolicy.RUNTIME)
            @EnabledIf(
                expression = "#{systemProperties['os.name'].toLowerCase().contains('mac')}",
                reason = "Enabled on Mac OS"
            )
            public @interface EnabledOnMac {}

4. Meta-Annotation Support for Testing
    1. You can use most test-related annotations as meta-annotations to create custom composed annotations and reduce configuration duplication across a test suite:
        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration({"/app-config.xml", "/test-data-access-config.xml"})
        @ActiveProfiles("dev")
        @Transactional
        public @interface TransactionalDevTestConfig { }

    and then use it:
        @TransactionalDevTestConfig
        class OrderRepositoryTests { }

    2. Since JUnit Jupiter supports the use of @Test, @RepeatedTest, @ParameterizedTest, and others as meta-annotations, you can also create custom composed annotations at the test method level.
        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @Transactional
        @Tag("integration-test") // org.junit.jupiter.api.Tag
        @Test // org.junit.jupiter.api.Test
        public @interface TransactionalIntegrationTest { }

    and then use it:
        @TransactionalIntegrationTest
        void saveOrder() { }

5. Spring TestContext Framework
    1. Context Configuration with Component Classes
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = {AppConfig.class, TestConfig.class}) 
        class MyTest {}

    2. Context Configuration Inheritance
        @SpringJUnitConfig(BaseConfig.class) 
        class BaseTest {}

        // ApplicationContext will be loaded from BaseConfig and ExtendedConfig
        @SpringJUnitConfig(ExtendedConfig.class) 
        class ExtendedTest extends BaseTest {}

    3. Context Configuration with Environment Profiles
        1. By annotating TransferServiceTest with @ActiveProfiles("dev"), we instruct the Spring TestContext Framework to load the ApplicationContext with the active profiles set to {"dev"}
        2. It is sometimes useful to assign beans to a default profile. Beans within the default profile are included only when no other profile is specifically activated. You can use this to define “fallback” beans to be used in the application’s default state.

    3. Declaring Test Property Sources
        @TestPropertySource("/test.properties") 
        @TestPropertySource(properties = {"timezone = GMT", "port: 4242"}) 

    4. Test suites and forked processes
        - The Spring TestContext framework stores application contexts in a static cache. This means that the context is literally stored in a static variable. In other words, if tests run in separate processes, the static cache is cleared between each test execution, which effectively disables the caching mechanism.
        - To benefit from the caching mechanism, all tests must run within the same process or test suite. This can be achieved by executing all tests as a group within an IDE. Similarly, when executing tests with a build framework such as Ant, Maven, or Gradle, it is important to make sure that the build framework does not fork between tests. For example, if the forkMode for the Maven Surefire plug-in is set to always or pertest, the TestContext framework cannot cache application contexts between test classes, and the build process runs significantly more slowly as a result.

    3.5.9. Transaction Management
        1. Preemptive timeouts and test-managed transactions
            - Specifically, Spring’s testing support binds transaction state to the current thread (via a java.lang.ThreadLocal variable) before the current test method is invoked. If a testing framework invokes the current test method in a new thread in order to support a preemptive timeout, any actions performed within the current test method will not be invoked within the test-managed transaction. Consequently, the result of any such actions will not be rolled back with the test-managed transaction. On the contrary, such actions will be committed to the persistent store — for example, a relational database — even though the test-managed transaction is properly rolled back by Spring.
            - Situations in which this can occur include but are not limited to the following.
                * JUnit 4’s @Test(timeout = …​) support and TimeOut rule
                * JUnit Jupiter’s assertTimeoutPreemptively(…​) methods in the org.junit.jupiter.api.Assertions class
        2. Method-level lifecycle methods 
            — for example, methods annotated with JUnit Jupiter’s @BeforeEach or @AfterEach — are run within a test-managed transaction. 
            - On the other hand, suite-level and class-level lifecycle methods — for example, methods annotated with JUnit Jupiter’s @BeforeAll or @AfterAll and methods annotated with TestNG’s @BeforeSuite, @AfterSuite, @BeforeClass, or @AfterClass — are not run within a test-managed transaction.

        3. Programmatic Transaction Management
            - You can use TestTransaction for manipulating transactions during the test

        4. Running Code Outside of a Transaction
            - Occasionally, you may need to run certain code before or after a transactional test method but outside the transactional context — for example, to verify the initial database state prior to running your test or to verify expected transactional commit behavior after your test runs (if the test was configured to commit the transaction). 
            - TransactionalTestExecutionListener supports the @BeforeTransaction and @AfterTransaction annotations for exactly such scenarios.

        5. Avoid false positives when testing ORM code
            - When you test application code that manipulates the state of a Hibernate session or JPA persistence context, make sure to flush the underlying unit of work within test methods that run that code. Failing to flush the underlying unit of work can produce false positives: Your test passes, but the same code throws an exception in a live, production environment. Note that this applies to any ORM framework that maintains an in-memory unit of work.

    3.5.10. Executing SQL Scripts
        1. Executing SQL scripts programmatically: ResourceDatabasePopulator
            @Test
            void databaseTest() {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScripts(
                        new ClassPathResource("test-schema.sql"),
                        new ClassPathResource("test-data.sql"));
                populator.setSeparator("@@");
                populator.execute(this.dataSource);
            }

        2. Executing SQL scripts declaratively with @Sql
            - Method-level @Sql declarations override class-level declarations by default. As of Spring Framework 5.2, however, this behavior may be configured per test class- or per test method via @SqlMergeMode. See Merging and Overriding Configuration with @SqlMergeMode for further details. 

        3. Default Script Detection
            - If no SQL scripts or statements are specified, an attempt is made to detect a default script, depending on where @Sql is declared. If a default cannot be detected, an IllegalStateException is thrown.
            - Class-level declaration: If the annotated test class- is com.example.MyTest, the corresponding default script is classpath:com/example/MyTest.sql.
            - Method-level declaration: If the annotated test method is named testMethod() and is defined in the class- com.example.MyTest, the corresponding default script is classpath:com/example/MyTest.testMethod.sql.

        4. Script Execution Phases
            - By default, SQL scripts are run before the corresponding test method. However, if you need to run a particular set of scripts after the test method (for example, to clean up database state), you can use the executionPhase attribute in @Sql

        5. Merging and Overriding Configuration with @SqlMergeMode
            - As of Spring Framework 5.2, it is possible to merge method-level @Sql declarations with class-level declarations
            - To enable @Sql merging, annotate either your test class- or test method with @SqlMergeMode(MERGE). To disable merging for a specific test method (or specific test subclass), you can switch back to the default mode via @SqlMergeMode(OVERRIDE)

    3.5.11. Parallel Test Execution
        1. Do not run tests in parallel if the tests:
            - Use Spring Framework’s @DirtiesContext support.
            - Use Spring Boot’s @MockBean or @SpyBean support.
            - Use JUnit 4’s @FixMethodOrder support or any testing framework feature that is designed to ensure that test methods run in a particular order. Note, however, that this does not apply if entire test classes are run in parallel.
            - Change the state of shared services or systems such as a database, message broker, filesystem, and others. This applies to both embedded and external systems.

        2. If parallel test execution fails with an exception stating that the ApplicationContext for the current test is no longer active, this typically means that the ApplicationContext was removed from the ContextCache in a different thread.
        This may be due to the use of @DirtiesContext or due to automatic eviction from the ContextCache. If @DirtiesContext is the culprit, you either need to find a way to avoid using @DirtiesContext or exclude such tests from parallel execution. If the maximum size of the ContextCache has been exceeded, you can increase the maximum size 
        of the cache. See the discussion on context caching for details.

        3. Parallel test execution in the Spring TestContext Framework is only possible if the underlying TestContext implementation provides a copy constructor, as explained in the javadoc for TestContext. The DefaultTestContext used in Spring provides such a constructor. However, if you use a third-party library that provides a custom TestContext implementation, you need to verify that it is suitable for parallel test execution. 
    
    3.5.12. TestContext Framework Support Classes 
        1. Spring JUnit 4 Runner
            1. The Spring TestContext Framework offers full integration with JUnit 4 through a custom runner (supported on JUnit 4.12 or higher). By annotating test classes with @RunWith(SpringJUnit4ClassRunner.class) or the shorter @RunWith(SpringRunner.class) variant, developers can implement standard JUnit 4-based unit and integration tests and simultaneously reap the benefits of the TestContext framework, such as support for loading application contexts, dependency injection of test instances, transactional test method execution, and so on. If you want to use the Spring TestContext Framework with an alternative runner (such as JUnit 4’s Parameterized runner) or third-party runners (such as the MockitoJUnitRunner), you can, optionally, use Spring’s support for JUnit rules instead.

            2. Some example:
                - Here @TestExecutionListeners is configured with an empty list, to disable the default listeners, which otherwise would require an ApplicationContext to be configured through @ContextConfiguration.

                @RunWith(SpringRunner.class)
                @TestExecutionListeners({})
                public class SimpleTest {
                    @Test
                    public void testMethod() {}
                }

        2. Spring JUnit 4 Rules
            - SpringClassRule
            - SpringMethodRule

        3. JUnit 4 Support Classes
            1. AbstractJUnit4SpringContextTests
                - When you extend AbstractJUnit4SpringContextTests, you can access a protected applicationContext instance variable that you can use to perform explicit bean lookups or to test the state of the context as a whole.
            2. AbstractTransactionalJUnit4SpringContextTests
                - When you extend AbstractTransactionalJUnit4SpringContextTests, you can access a protected jdbcTemplate instance variable that you can use to run SQL statements to query the database. You can use such queries to confirm database state both before and after running database-related application code, and Spring ensures that such queries run in the scope of the same transaction as the application code.

        4. SpringExtension for JUnit Jupiter
            1. The Spring TestContext Framework offers full integration with the JUnit Jupiter testing framework, introduced in JUnit 5. 
                - By annotating test classes with @ExtendWith(SpringExtension.class), you can implement standard JUnit Jupiter-based unit and integration tests and simultaneously reap the benefits of the TestContext framework, such as support for loading application contexts, dependency injection of test instances, transactional test method execution, and so on.
            2. Furthermore, thanks to the rich extension API in JUnit Jupiter, Spring provides the following features above and beyond the feature set that Spring supports for JUnit 4 and TestNG:
                - Dependency injection for test constructors, test methods, and test lifecycle callback methods. See Dependency Injection with SpringExtension for further details.
                - Powerful support for conditional test execution based on SpEL expressions, environment variables, system properties, and so on. See the documentation for @EnabledIf and @DisabledIf in Spring JUnit Jupiter Testing Annotations for further details and examples.
                - Custom composed annotations that combine annotations from Spring and JUnit Jupiter. See the @TransactionalDevTestConfig and @TransactionalIntegrationTest examples in Meta-Annotation Support for Testing for further details.
            3. Since you can also use annotations in JUnit 5 as meta-annotations, Spring provides the @SpringJUnitConfig and @SpringJUnitWebConfig composed annotations to simplify the configuration of the test ApplicationContext and JUnit Jupiter.
            4. Method Injection
                @Test
                void deleteOrder(@Autowired OrderService orderService) {}

3.6. WebTestClient
- WebTestClient is an HTTP client designed for testing server applications. It wraps Spring’s WebClient and uses it to perform requests but exposes a testing facade for verifying responses. WebTestClient can be used to perform end-to-end HTTP tests. It can also be used to test Spring MVC and Spring WebFlux applications without a running server via mock server request and response objects.

    3.6.1. Setup
        1. Bind to Controller
        - This setup allows you to test specific controller(s) via mock request and response objects, without a running server.
            WebTestClient client = MockMvcWebTestClient.bindToController(new TestController()).build();

        2. Bind to ApplicationContext
            @SpringJUnitConfig(WebConfig.class) 
            class MyTests {
                WebTestClient client;

                @BeforeEach
                void setUp(ApplicationContext context) { client = WebTestClient.bindToApplicationContext(context).build(); }
            }

            For Spring MVC, use the following where the Spring ApplicationContext is passed to MockMvcBuilders.webAppContextSetup to create a MockMvc instance to handle requests:
                @ExtendWith(SpringExtension.class)
                @WebAppConfiguration("classpath:META-INF/web-resources") 
                @ContextHierarchy({ @ContextConfiguration(classes = RootConfig.class), @ContextConfiguration(classes = WebConfig.class) })
                class MyTests {

                    @Autowired
                    WebApplicationContext wac; 

                    WebTestClient client;

                    @BeforeEach
                    void setUp() { client = MockMvcWebTestClient.bindToApplicationContext(this.wac).build(); }
                }

            3. Bind to Server
            This setup connects to a running server to perform full, end-to-end HTTP tests:
                client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();

            4. Client Config
            In addition to the server setup options described earlier, you can also configure client options, including base URL, default headers, client filters, and others. These options are readily available following bindToServer()

                client = WebTestClient.bindToController(new TestController())
                                .configureClient()
                                .baseUrl("/test")
                                .build();

        3.6.2. Writing Tests
            // TODO: Many examples. Need to read again when needed.

    3.7. MockMvc
        The Spring MVC Test framework, also known as MockMvc, provides support for testing Spring MVC applications. It performs full Spring MVC request handling but via mock request and response objects instead of a running server.
        MockMvc can be used on its own to perform requests and verify responses. It can also be used through the WebTestClient where MockMvc is plugged in as the server to handle requests with. The advantage of WebTestClient is the option to work with higher level objects instead of raw data as well as the ability to switch to full, end-to-end HTTP tests against a live server and use the same test API.

        3.7.1. Overview
            1. Setup Choices
                1. To set up MockMvc for testing a specific controller, use the following:
                    @BeforeEach
                    void setup() { 
                        this.mockMvc = MockMvcBuilders.standaloneSetup(new AccountController()).build(); 
                    }

                2. To set up MockMvc through Spring configuration, use the following:
                    @SpringJUnitWebConfig(locations = "my-servlet-context.xml")
                    class MyWebTests {

                        MockMvc mockMvc;

                        @BeforeEach
                        void setup(WebApplicationContext wac) {
                            this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
                        }
                    }

                3. Which setup option should you use?
                    1. The webAppContextSetup
                        - loads your actual Spring MVC configuration, resulting in a more complete integration test. Since the TestContext framework caches the loaded Spring configuration, it helps keep tests running fast, even as you introduce more tests in your test suite. Furthermore, you can inject mock services into controllers through Spring configuration to remain focused on testing the web layer. 

                    2. The standaloneSetup
                        - on the other hand, is a little closer to a unit test. It tests one controller at a time. You can manually inject the controller with mock dependencies, and it does not involve loading Spring configuration. Such tests are more focused on style and make it easier to see which controller is being tested, whether any specific Spring MVC configuration is required to work, and so on. The standaloneSetup is also a very convenient way to write ad-hoc tests to verify specific behavior or to debug an issue.
            2. Setup Features
                1. You can set up default request properties, as the following example shows:
                        @BeforeEach
                        void setup() {
                            mockMvc = standaloneSetup(new AccountController())
                                .defaultRequest(get("/")
                                .contextPath("/app").servletPath("/main")
                                .accept(MediaType.APPLICATION_JSON)).build();
                        }

            3. Defining Expectations

            4. Async Requests
                In Spring MVC Test, async requests can be tested by asserting the produced async value first, then manually performing the async dispatch, and finally verifying the response. Below is an example test for controller methods that return DeferredResult, Callable, or reactive type such as Reactor Mono:

                @Test
                void test() throws Exception {
                    MvcResult mvcResult = this.mockMvc.perform(get("/path"))
                            .andExpect(status().isOk()) 
                            .andExpect(request().asyncStarted()) 
                            .andExpect(request().asyncResult("body")) 
                            .andReturn();

                    this.mockMvc.perform(asyncDispatch(mvcResult)) 
                            .andExpect(status().isOk()) 
                            .andExpect(content().string("body"));
                }

            5. Examples
                https://github.com/spring-projects/spring-framework/tree/master/spring-test/src/test/java/org/springframework/test/web/servlet/samples
                https://github.com/spring-projects/spring-framework/tree/master/spring-test/src/test/java/org/springframework/test/web/client/samples

    3.8. Testing Client Applications
        You can use client-side tests to test code that internally uses the RestTemplate. The idea is to declare expected requests and to provide “stub” responses so that you can focus on testing the code in isolation (that is, without running a server).

            RestTemplate restTemplate = new RestTemplate();
            MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();
            mockServer.expect(requestTo("/greeting")).andRespond(withSuccess());
            // Test code that uses the above RestTemplate ...
            mockServer.verify();