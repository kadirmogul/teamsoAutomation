package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features="src/test/java/features/ATS",
        glue="stepDefinitions",
        plugin={"pretty","html:target/cucumber-report.html"},
        tags="@ATS",
        monochrome=true,
        dryRun=false
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
