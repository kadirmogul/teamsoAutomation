package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features="src/test/java/features/ATS",
        glue="stepDefinitions",
        plugin={
            "pretty",
            "html:target/cucumber-reports/parallel-report.html",
            "json:target/cucumber-reports/parallel-report.json",
            "junit:target/cucumber-reports/parallel-report.xml"
        },
        tags="@ATS",
        monochrome=true,
        dryRun=false
)
public class ParallelTestRunner extends AbstractTestNGCucumberTests {
}
