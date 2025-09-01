# teamsoAutomation

Bu proje Cucumber ve Selenium WebDriver kullanarak web otomasyon testleri yapmak için oluşturulmuştur.

## Proje Yapısı

```
src/
├── main/java/org/example/
│   └── Main.java
└── test/java/
    ├── features/
    │   └── ATS/
    │       ├── 01_ResponsibilityManagement/
    │       ├── 02_ApprovalProcess/
    │       ├── 03_RecruitmentRequest/
    │       ├── 04_JobPostings/
    │       ├── 05_CVPool/
    │       ├── 06_Reports/
    │       └── 07_Settings/
    ├── runner/
    │   ├── TestRunner.java
    │   └── ParallelTestRunner.java
    ├── stepDefinitions/
    │   └── AuthenticationStepDefinitions.java
    └── utilities/
        ├── BaseTest.java
        ├── MyDriver.java
        └── TestUtils.java
```

## Gereksinimler

- Java 11+
- Maven 3.6+
- Chrome Browser
- Selenium WebDriver

## Kurulum

1. Projeyi klonlayın
2. Maven dependencies'leri yükleyin: `mvn clean install`
3. Testleri çalıştırın: `mvn test`

## Test Çalıştırma

```bash
mvn test
```

## Raporlar

Test raporları `target/cucumber-report.html` dosyasında oluşturulur.

## Özellikler

- **ATS (Applicant Tracking System)** otomasyon testleri
- **Cucumber BDD** framework
- **Selenium WebDriver** ile web otomasyon
- **Parallel test execution** desteği
- **Detailed logging** ve raporlama
