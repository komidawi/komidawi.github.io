26. Testing
Spring Boot provides a number of utilities and annotations to help when testing your application. Test support is provided by two modules: spring-boot-test contains core items, and spring-boot-test-autoconfigure supports auto-configuration for tests.

Most developers use the spring-boot-starter-test “Starter”, which imports both Spring Boot test modules as well as JUnit Jupiter, AssertJ, Hamcrest, and a number of other useful libraries.

If you have tests that use JUnit 4, JUnit 5’s vintage engine can be used to run them. To use the vintage engine, add a dependency on junit-vintage-engine

26.3. Testing Spring Boot Applications
    1. External properties, logging, and other features of Spring Boot are installed in the context by default only if you use SpringApplication to create it. 

    2. If you are using JUnit 4, don’t forget to also add @RunWith(SpringRunner.class) to your test, otherwise the annotations will be ignored. If you are using JUnit 5, there’s no need to add the equivalent @ExtendWith(SpringExtension.class) as @SpringBootTest and the other @…Test annotations are already annotated with it. 

    3. By default, @SpringBootTest will not start a server. You can use the webEnvironment attribute of @SpringBootTest to further refine how your tests run:
        - MOCK(Default) : Loads a web ApplicationContext and provides a mock web environment. Embedded servers are not started when using this annotation. If a web environment is not available on your classpath, this mode transparently falls back to creating a regular non-web ApplicationContext. It can be used in conjunction with @AutoConfigureMockMvc or @AutoConfigureWebTestClient for mock-based testing of your web application.
        - RANDOM_PORT | DEFINED_PORT: Loads a WebServerApplicationContext and provides a real web environment. Embedded servers are started and listen on a random port, on a defined port (from your application.properties) or on the default port of 8080.
        - NONE: Loads an ApplicationContext by using SpringApplication but does not provide any web environment (mock or otherwise).

    4. If your test is @Transactional, it rolls back the transaction at the end of each test method by default. However, as using this arrangement with either RANDOM_PORT or DEFINED_PORT implicitly provides a real servlet environment, the HTTP client and server run in separate threads and, thus, in separate transactions. Any transaction initiated on the server does not roll back in this case.

    26.3.1. Detecting Web Application Type
        If Spring MVC is available, a regular MVC-based application context is configured.

    26.3.2. Detecting Test Configuration
        1. - If you are familiar with the Spring Test Framework, you may be used to using @ContextConfiguration(classes=…​) in order to specify which Spring @Configuration to load. Alternatively, you might have often used nested @Configuration classes within your test.
        - When testing Spring Boot applications, this is often not required. Spring Boot’s @*Test annotations search for your primary configuration automatically whenever you do not explicitly define one.
        - The search algorithm works up from the package, that contains the test until it finds a class: annotated with @SpringBootApplication or @SpringBootConfiguration. As long as you structured your code in a sensible way, your main configuration is usually found.

        2. - If you use a test annotation to test a more specific slice of your application, you should avoid adding configuration settings that are specific to a particular area on the main method’s application class.
        - The underlying component scan configuration of @SpringBootApplication defines exclude filters that are used to make sure slicing works as expected. If you are using an explicit @ComponentScan directive on your @SpringBootApplication-annotated class, be aware that those filters will be disabled. If you are using slicing, you should define them again.

        3. If you want to customize the primary configuration, you can use a nested @TestConfiguration class. Unlike a nested @Configuration class, which would be used instead of your application’s primary configuration, a nested @TestConfiguration Class is used in addition to your application’s primary configuration.
        
    26.3.3. Excluding Test Configuration
        1. - If your application uses component scanning (for example, if you use @SpringBootApplication or @ComponentScan), you may find top-level configuration classes that you created only for specific tests accidentally get picked up everywhere.
        - As we have seen earlier, @TestConfiguration can be used on an inner Class of a test to customize the primary configuration. When placed on a top-level class, @TestConfiguration indicates that classes in src/test/java should not be picked up by scanning. You can then Import that Class explicitly where it is required, as shown in the following example:

            @SpringBootTest
            @Import(MyTestsConfiguration.class)
            class MyTests {
                @Test
                void exampleTest() {}
            }

        - If you directly use @ComponentScan (that is, not through @SpringBootApplication) you need to register the TypeExcludeFilter with it. See the Javadoc for details. 

    26.3.4. Using Application Arguments
        If your application expects arguments, you can have @SpringBootTest inject them using the args attribute.

            @SpringBootTest(args = "--app.test=one")
            class ApplicationArgumentsExampleTests {
                @Test
                void applicationArgumentsPopulated(@Autowired ApplicationArguments args) {
                    assertThat(args.getOptionNames()).containsOnly("app.test");
                    assertThat(args.getOptionValues("app.test")).containsOnly("one");
                }
            }

    26.3.5. Testing with a mock environment
        1. By default, @SpringBootTest does not start the server. If you have web endpoints that you want to test against this mock environment, you can additionally configure MockMvc as shown in the following example:

            @SpringBootTest
            @AutoConfigureMockMvc
            class MockMvcExampleTests {
                @Test
                void exampleTest(@Autowired MockMvc mvc) throws Exception {
                    mvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string("Hello World"));
                }
            }

        2. If you want to focus only on the web layer and not start a complete ApplicationContext, consider using @WebMvcTest instead. 

        3. - Testing within a mocked environment is usually faster than running with a full Servlet container. However, since mocking occurs at the Spring MVC layer, code that relies on lower-level Servlet container behavior cannot be directly tested with MockMvc.
        - For example, Spring Boot’s error handling is based on the “error page” support provided by the Servlet container. This means that, whilst you can test your MVC layer throws and handles exceptions as expected, you cannot directly test that a specific custom error page is rendered. If you need to test these lower-level concerns, you can start a fully running server as described in the next section.

    26.3.6. Testing with a running server
        1. If you need to start a full running server, we recommend that you use random ports. If you use @SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT), an available port is picked at random each time your test runs.

            @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
            class RandomPortTestRestTemplateExampleTests {
                @Test
                void exampleTest(@Autowired TestRestTemplate restTemplate) {
                    String body = restTemplate.getForObject("/", String.class);
                    assertThat(body).isEqualTo("Hello World");
                }
            }

    26.3.7. Customizing WebTestClient
        To customize the WebTestClient bean, configure a WebTestClientBuilderCustomizer bean. Any such beans are called with the WebTestClient.Builder that is used to create the WebTestClient.

    26.3.8. Using JMX
    26.3.9. Using Metrics

    26.3.10. Mocking and Spying Beans
        1. Spring Boot includes a @MockBean annotation that can be used to define a Mockito mock for a bean inside your ApplicationContext. You can use the annotation to add new beans or replace a single existing bean definition.

        2. @MockBean cannot be used to mock the behavior of a bean that’s exercised during application context refresh. By the time the test is executed, the application context refresh has completed and it is too late to configure the mocked behavior. We recommend using a @Bean method to create and configure the mock in this situation.

        3. Additionally, you can use @SpyBean to wrap any existing bean with a Mockito spy. See the Javadoc for full details.

        4. (CGLib proxies, such as those created for scoped beans, declare the proxied methods as final. This stops Mockito from functioning correctly as it cannot mock or spy on 
        final methods in its default configuration. If you want to mock or spy on such a bean, configure Mockito to use its inline mock maker by adding org.mockito:mockito-inline to your application’s test dependencies. This allows Mockito to mock and spy on final methods.) 

        5. While Spring’s test framework caches application contexts between tests and reuses a context for tests sharing the same configuration, the use of @MockBean or @SpyBean influences the cache key, which will most likely increase the number of contexts. 

        6. (If you are using @SpyBean to spy on a bean with @Cacheable methods that refer to parameters by name, your application must be compiled with -parameters. This ensures that the parameter names are available to the caching infrastructure once the bean has been spied upon.)

        7. When you are using @SpyBean to spy on a bean that is proxied by Spring, you may need to remove Spring’s proxy in some situations, for example when setting expectations using given or when. Use AopTestUtils.getTargetObject(yourProxiedSpy) to do so. 

    26.3.11. Auto-configured Tests
        1. - Spring Boot’s auto-configuration system works well for applications but can sometimes be a little too much for tests. It often helps to load only the parts of the configuration that are required to test a “slice” of your application. For example, you might want to test that Spring MVC controllers are mapping URLs correctly, and you do not want to involve database calls in those tests, or you might want to test JPA entities, and you are not interested in the web layer when those tests run.
        - The spring-boot-test-autoconfigure Module includes a number of annotations that can be used to automatically configure such “slices”. Each of them works in a similar way, providing a @…​Test annotation that loads the ApplicationContext and one or more @AutoConfigure…​ annotations that can be used to customize auto-configuration settings.

        2. Each slice restricts component scan to appropriate components and loads a very restricted set of auto-configuration classes. If you need to exclude one of them, most @…​Test annotations provide an excludeAutoConfiguration attribute. Alternatively, you can use @ImportAutoConfiguration#exclude. 

        3. Including multiple “slices” by using several @…​Test annotations in one test is not supported. If you need multiple “slices”, pick one of the @…​Test annotations and include the @AutoConfigure…​ annotations of the other “slices” by hand. 

        4. It is also possible to use the @AutoConfigure…​ annotations with the standard @SpringBootTest annotation. You can use this combination if you are not interested in “slicing” your application but you want some of the auto-configured test beans. 

    26.3.12. Auto-configured JSON Tests
        // Do poczytania w dokumentacji

    26.3.13. Auto-configured Spring MVC Tests
        - To test whether Spring MVC controllers are working as expected, use the @WebMvcTest annotation. @WebMvcTest auto-configures the Spring MVC infrastructure and limits scanned beans to @Controller, @ControllerAdvice, @JsonComponent, Converter, GenericConverter, Filter, HandlerInterceptor, WebMvcConfigurer, and HandlerMethodArgumentResolver. Regular @Component and @ConfigurationProperties beans are not scanned when the @WebMvcTest annotation is used. @EnableConfigurationProperties can be used to include @ConfigurationProperties beans.
        - A list of the auto-configuration settings that are enabled by @WebMvcTest can be found in the appendix. 
        - If you need to register extra components, such as the Jackson Module, you can Import additional configuration classes by using @Import on your test. 
        - Often, @WebMvcTest is limited to a single controller and is used in combination with @MockBean to provide mock implementations for required collaborators.
        - @WebMvcTest also auto-configures MockMvc. Mock MVC offers a powerful way to quickly test MVC controllers without needing to start a full HTTP server.
        - You can also auto-configure MockMvc in a non-@WebMvcTest (such as @SpringBootTest) by annotating it with @AutoConfigureMockMvc.
        - If you need to configure elements of the auto-configuration (for example, when servlet filters should be applied) you can use attributes in the @AutoConfigureMockMvc annotation. 

            @WebMvcTest(UserVehicleController.class)
            class MyControllerTests {

                @Autowired
                private MockMvc mvc;

                @MockBean
                private UserVehicleService userVehicleService;

                @Test
                void testExample() throws Exception {
                    given(this.userVehicleService.getVehicleDetails("sboot"))
                            .willReturn(new VehicleDetails("Honda", "Civic"));
                    this.mvc.perform(get("/sboot/vehicle").accept(MediaType.TEXT_PLAIN))
                            .andExpect(status().isOk()).andExpect(content().string("Honda Civic"));
                }

    26.3.14. Auto-configured Spring WebFlux Tests
    26.3.15. Auto-configured Data Cassandra Tests

    26.3.16. Auto-configured Data JPA Tests
        1. You can use the @DataJpaTest annotation to test JPA applications. By default, it scans for @Entity classes and configures Spring Data JPA repositories. If an embedded database is available on the classpath, it configures one as well. Regular @Component and @ConfigurationProperties beans are not scanned when the @DataJpaTest annotation is used. @EnableConfigurationProperties can be used to include @ConfigurationProperties beans. By default, data JPA tests are transactional and roll back at the end of each test.
        2. Data JPA tests may also inject a TestEntityManager bean, which provides an alternative to the standard JPA EntityManager that is specifically designed for tests. If you want to use TestEntityManager outside of @DataJpaTest instances, you can also use the @AutoConfigureTestEntityManager annotation. A JdbcTemplate is also available if you need that. The following example shows the @DataJpaTest annotation in use:

            @DataJpaTest
            class ExampleRepositoryTests {

                @Autowired
                private TestEntityManager entityManager;

                @Autowired
                private UserRepository repository;

                @Test
                void testExample() throws Exception {
                    this.entityManager.persist(new User("sboot", "1234"));
                    User user = this.repository.findByUsername("sboot");
                    assertThat(user.getUsername()).isEqualTo("sboot");
                    assertThat(user.getVin()).isEqualTo("1234");
                }
            }

        3. In-memory embedded databases generally work well for tests, since they are fast and do not require any installation. If, however, you prefer to run tests against a real database you can use the @AutoConfigureTestDatabase annotation, as shown in the following example:
            @DataJpaTest
            @AutoConfigureTestDatabase(replace=Replace.NONE)
            class ExampleRepositoryTests {}

    26.3.17. Auto-configured JDBC Tests
        1. @JdbcTest is similar to @DataJpaTest but is for tests that only require a DataSource and do not use Spring Data JDBC. By default, it configures an in-memory embedded database and a JdbcTemplate. Regular @Component and @ConfigurationProperties beans are not scanned when the @JdbcTest annotation is used. @EnableConfigurationProperties can be used to include @ConfigurationProperties beans. By default, JDBC tests are transactional and roll back at the end of each test. 
    
    26.3.18. Auto-configured Data JDBC Tests
        1. @DataJdbcTest is similar to @JdbcTest but is for tests that use Spring Data JDBC repositories. By default, it configures an in-memory embedded database, a JdbcTemplate, and Spring Data JDBC repositories. Regular @Component and @ConfigurationProperties beans are not scanned when the @DataJdbcTest annotation is used. @EnableConfigurationProperties can be used to include @ConfigurationProperties beans. By default, Data JDBC tests are transactional and roll back at the end of each test

    26.3.19. Auto-configured jOOQ Tests
    26.3.20. Auto-configured Data MongoDB Tests
    26.3.21. Auto-configured Data Neo4j Tests
    26.3.22. Auto-configured Data Redis Tests
    26.3.23. Auto-configured Data LDAP Tests

    26.3.24. Auto-configured REST Clients
        1. You can use the @RestClientTest annotation to test REST clients. By default, it auto-configures Jackson, GSON, and Jsonb support, configures a RestTemplateBuilder, and adds support for MockRestServiceServer. Regular @Component and @ConfigurationProperties beans are not scanned when the @RestClientTest annotation is used. @EnableConfigurationProperties can be used to include @ConfigurationProperties beans.

        2. The specific beans that you want to test should be specified by using the value or components attribute of @RestClientTest, as shown in the following example:

            @RestClientTest(RemoteVehicleDetailsService.class)
            class ExampleRestClientTest {

                @Autowired
                private RemoteVehicleDetailsService service;

                @Autowired
                private MockRestServiceServer server;

                @Test
                void getVehicleDetailsWhenResultIsSuccessShouldReturnDetails()
                        throws Exception {
                    this.server.expect(requestTo("/greet/details"))
                            .andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
                    String greeting = this.service.callRestService();
                    assertThat(greeting).isEqualTo("hello");
                }
            }

    26.3.25. Auto-configured Spring REST Docs Tests

    26.3.26. Auto-configured Spring Web Services Tests
        1. You can use @WebServiceClientTest to test applications that use call web services using the Spring Web Services project. By default, it configures a mock WebServiceServer bean and automatically customizes your WebServiceTemplateBuilder. (For more about using Web Services with Spring Boot, see "Web Services", earlier in this chapter.)
        2. The following example shows the @WebServiceClientTest annotation in use:
            @WebServiceClientTest(ExampleWebServiceClient.class)
            class WebServiceClientIntegrationTests {

                @Autowired
                private MockWebServiceServer server;

                @Autowired
                private ExampleWebServiceClient client;

                @Test
                void mockServerCall() {
                    this.server.expect(payload(new StringSource("<request/>"))).andRespond(
                            withPayload(new StringSource("<response><status>200</status></response>")));
                    assertThat(this.client.test()).extracting(Response::getStatus).isEqualTo(200);
                }
            }

    26.3.27. Additional Auto-configuration and Slicing
        1. Each slice provides one or more @AutoConfigure…​ annotations that namely defines the auto-configurations that should be included as part of a slice. Additional auto-configurations can be added on a test-by-test basis by creating a custom @AutoConfigure…​ annotation or by adding @ImportAutoConfiguration to the test as shown in the following example:

            @JdbcTest
            @ImportAutoConfiguration(IntegrationAutoConfiguration.class)
            class ExampleJdbcTests {}

        2. Make sure to not use the regular @Import annotation to Import auto-configurations as they are handled in a specific way by Spring Boot. 

        3. A slice or @AutoConfigure…​ annotation can be customized this way as long as it is meta-annotated with @ImportAutoConfiguration. 

    26.3.28. User Configuration and Slicing
        1. If you structure your code in a sensible way, your @SpringBootApplication Class is used by default as the configuration of your tests.
        It then becomes important not to litter the application’s main Class with configuration settings that are specific to a particular area of its functionality.

        2. - Assume that you are using Spring Batch and you rely on the auto-configuration for it. You could define your @SpringBootApplication as follows:

            @SpringBootApplication
            @EnableBatchProcessing
            public class SampleApplication { ... }

        - Because this class: is the source configuration for the test, any slice test actually tries to start Spring Batch, which is definitely not what you want to do. A recommended approach is to move that area-specific configuration to a separate @Configuration Class at the same level as your application, as shown in the following example:

            @Configuration(proxyBeanMethods = false)
            @EnableBatchProcessing
            public class BatchConfiguration { ... }

        - Depending on the complexity of your application, you may either have a single @Configuration Class for your customizations or one Class per domain area. The latter approach lets you enable it in one of your tests, if necessary, with the @Import annotation. 

        3. - Test slices exclude @Configuration classes from scanning. For example, for a @WebMvcTest, the following configuration will not include the given WebMvcConfigurer bean in the application context loaded by the test slice:
            @Configuration
            public class WebConfiguration {
                @Bean
                public WebMvcConfigurer testConfigurer() {
                    return new WebMvcConfigurer() {};
                }
            }
        - The configuration below will, however, cause the custom WebMvcConfigurer to be loaded by the test slice.
            @Component
            public class TestWebMvcConfigurer implements WebMvcConfigurer {}

        4. Another source of confusion is classpath scanning. Assume that, while you structured your code in a sensible way, you need to scan an additional Package. Your application may resemble the following code:

            @SpringBootApplication
            @ComponentScan({ "com.example.app", "org.acme.another" })
            public class SampleApplication { ... }

        - Doing so effectively overrides the default component scan directive with the side effect of scanning those two packages regardless of the slice that you chose. For instance, a @DataJpaTest seems to suddenly scan components and user configurations of your application. Again, moving the custom directive to a separate Class is a good way to fix this issue.

        5. If this is not an option for you, you can create a @SpringBootConfiguration somewhere in the hierarchy of your test so that it is used instead. Alternatively, you can specify a source for your test, which disables the behavior of finding a default one. 

26.4. Test Utilities

    26.4.1. ConfigFileApplicationContextInitializer
        1. ConfigFileApplicationContextInitializer is an ApplicationContextInitializer that you can apply to your tests to load Spring Boot application.properties files. You can use it when you do not need the full set of features provided by @SpringBootTest, as shown in the following example:
            @ContextConfiguration(classes = Config.class,
                initializers = ConfigFileApplicationContextInitializer.class)

        2. Using ConfigFileApplicationContextInitializer alone does not provide support for @Value("${…​}") injection. Its only job is to ensure that application.properties files are loaded into Spring’s Environment. For @Value support, you need to either additionally configure a PropertySourcesPlaceholderConfigurer or use @SpringBootTest, which auto-configures one for you. 

    26.4.2. TestPropertyValues
        TestPropertyValues lets you quickly add properties to a ConfigurableEnvironment or ConfigurableApplicationContext. You can call it with key=value strings, as follows:
            TestPropertyValues.of("org=Spring", "name=Boot").applyTo(env);

    26.4.3. OutputCapture
        OutputCapture is a JUnit Extension that you can use to capture System.out and System.err output. To use add @ExtendWith(OutputCaptureExtension.class) and inject CapturedOutput as an argument to your test Class constructor or test method as follows:

            @ExtendWith(OutputCaptureExtension.class)
            class OutputCaptureTests {
                @Test
                void testName(CapturedOutput output) {
                    System.out.println("Hello World!");
                    assertThat(output).contains("World");
                }
            }

    26.4.4. TestRestTemplate
        1. TestRestTemplate is a convenience alternative to Spring’s RestTemplate that is useful in integration tests. You can get a vanilla template or one that sends Basic HTTP authentication (with a username and password). In either case, the template behaves in a test-friendly way by not throwing exceptions on server-side errors.

        2. (Spring Framework 5.0 provides a new WebTestClient that works for WebFlux integration tests and both WebFlux and MVC end-to-end testing. It provides a fluent API for assertions, unlike TestRestTemplate.)

        3. TestRestTemplate can be instantiated directly in your integration tests, as shown in the following example:

            public class MyTest {
                private TestRestTemplate template = new TestRestTemplate();

                @Test
                public void testRequest() throws Exception {
                    HttpHeaders headers = template
                        .getForEntity("https://myhost.example.com/example", String.class).getHeaders();
                    assertThat(headers.getLocation()).hasHost("other.example.com");
                }
            }

        4. Alternatively, if you use the @SpringBootTest annotation with WebEnvironment.RANDOM_PORT or WebEnvironment.DEFINED_PORT, you can inject a fully configured TestRestTemplate and start using it. If necessary, additional customizations can be applied through the RestTemplateBuilder bean. Any URLs that do not specify a host and port automatically connect to the embedded server, as shown in the following example:

            @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
            class SampleWebClientTests {

                @Autowired
                private TestRestTemplate template;

                @Test
                void testRequest() {
                    HttpHeaders headers = this.template.getForEntity("/example", String.class).getHeaders();
                    assertThat(headers.getLocation()).hasHost("other.example.com");
                }

                @TestConfiguration(proxyBeanMethods = false)
                static class Config {
                    @Bean
                    RestTemplateBuilder restTemplateBuilder() {
                        return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(1))
                                .setReadTimeout(Duration.ofSeconds(1));
                    }
                }
            }