# teamsoAutomation

Bu proje Cucumber ve Selenium WebDriver kullanarak web otomasyon testleri yapmak için oluşturulmuştur.

## Proje Yapısı

```
src/
├── main/java/org/example/
│   └── Main.java
└── test/java/
    ├── features/
    │   └── ExampleScenario.feature
    ├── pages/
    │   └── LoginPage.java
    ├── runner/
    │   └── TestRunner.java
    ├── stepDefinitions/
    │   └── StepDefinations.java
    └── utilities/
        └── MyDriver.java
```

## Gereksinimler

- Java 11+
- Maven 3.6+
- Chrome Browser

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
