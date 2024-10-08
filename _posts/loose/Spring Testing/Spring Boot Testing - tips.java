If you are using JUnit 4, don’t forget to also add @RunWith(SpringRunner.class) to your test, otherwise the annotations will be ignored. If you are using JUnit 5, there’s no need to add the equivalent @ExtendWith(SpringExtension.class) as @SpringBootTest and the other @…Test annotations are already annotated with it. 


If you are using an explicit @ComponentScan directive on your @SpringBootApplication-annotated class, be aware that those filters will be disabled. If you are using slicing, you should define them again.

If you want to customize the primary configuration, you can use a nested @TestConfiguration class. Unlike a nested @Configuration class, which would be used instead of your application’s primary configuration, a nested @TestConfiguration Class is used in addition to your application’s primary configuration.


If your application uses component scanning (for example, if you use @SpringBootApplication or @ComponentScan), you may find top-level configuration classes that you created only for specific tests accidentally get picked up everywhere.

As we have seen earlier, @TestConfiguration can be used on an inner Class of a test to customize the primary configuration. When placed on a top-level class, @TestConfiguration indicates that classes in src/test/java should not be picked up by scanning. You can then Import that Class explicitly where it is required, as shown in the following example:
    @SpringBootTest
    @Import(MyTestsConfiguration.class)
    class MyTests {
        @Test
        void exampleTest() {}
    }

If you directly use @ComponentScan (that is, not through @SpringBootApplication) you need to register the TypeExcludeFilter with it. See the Javadoc for details. 


Spring Boot’s auto-configuration system works well for applications but can sometimes be a little too much for tests. It often helps to load only the parts of the configuration that are required to test a “slice” of your application. For example, you might want to test that Spring MVC controllers are mapping URLs correctly, and you do not want to involve database calls in those tests, or you might want to test JPA entities, and you are not interested in the web layer when those tests run.

The spring-boot-test-autoconfigure Module includes a number of annotations that can be used to automatically configure such “slices”. Each of them works in a similar way, providing a @…​Test annotation that loads the ApplicationContext and one or more @AutoConfigure…​ annotations that can be used to customize auto-configuration settings.

Each slice restricts component scan to appropriate components and loads a very restricted set of auto-configuration classes. If you need to exclude one of them, most @…​Test annotations provide an excludeAutoConfiguration attribute. Alternatively, you can use @ImportAutoConfiguration#exclude. 

Including multiple “slices” by using several @…​Test annotations in one test is not supported. If you need multiple “slices”, pick one of the @…​Test annotations and include the @AutoConfigure…​ annotations of the other “slices” by hand. 

It is also possible to use the @AutoConfigure…​ annotations with the standard @SpringBootTest annotation. You can use this combination if you are not interested in “slicing” your application but you want some of the auto-configured test beans. 


If you structure your code in a sensible way, your @SpringBootApplication Class is used by default as the configuration of your tests.
It then becomes important not to litter the application’s main Class with configuration settings that are specific to a particular area of its functionality.

Assume that you are using Spring Batch and you rely on the auto-configuration for it. You could define your @SpringBootApplication as follows:
    @SpringBootApplication
    @EnableBatchProcessing
    public class SampleApplication { ... }

Because this Class is the source configuration for the test, any slice test actually tries to start Spring Batch, which is definitely not what you want to do. A recommended approach is to move that area-specific configuration to a separate @Configuration Class at the same level as your application, as shown in the following example:
    @Configuration(proxyBeanMethods = false)
    @EnableBatchProcessing
    public class BatchConfiguration { ... }

Depending on the complexity of your application, you may either have a single @Configuration Class for your customizations or one Class per domain area. The latter approach lets you enable it in one of your tests, if necessary, with the @Import annotation. 


Test slices exclude @Configuration classes from scanning. For example, for a @WebMvcTest, the following configuration will not include the given WebMvcConfigurer bean in the application context loaded by the test slice:
    @Configuration
    public class WebConfiguration {
        @Bean
        public WebMvcConfigurer testConfigurer() {
            return new WebMvcConfigurer() {};
        }
    }

The configuration below will, however, cause the custom WebMvcConfigurer to be loaded by the test slice.
    @Component
    public class TestWebMvcConfigurer implements WebMvcConfigurer {}


Another source of confusion is classpath scanning. Assume that, while you structured your code in a sensible way, you need to scan an additional Package. Your application may resemble the following code:

    @SpringBootApplication
    @ComponentScan({ "com.example.app", "org.acme.another" })
    public class SampleApplication { ... }

Doing so effectively overrides the default component scan directive with the side effect of scanning those two packages regardless of the slice that you chose. For instance, a @DataJpaTest seems to suddenly scan components and user configurations of your application. Again, moving the custom directive to a separate Class is a good way to fix this issue.

If this is not an option for you, you can create a @SpringBootConfiguration somewhere in the hierarchy of your test so that it is used instead. Alternatively, you can specify a source for your test, which disables the behavior of finding a default one. 