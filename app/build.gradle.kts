import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.24"
    id( "org.openapi.generator") version "7.5.0"
}

group = "com.app"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0") 
    implementation("ch.qos.logback:logback-classic:1.5.6")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

openApiGenerate {
    // 使用するジェネレーターを指定します。この場合、「kotlin-spring」を使用して、KotlinとSpringに適したコードを生成します。
    generatorName.set("kotlin-spring")

    // OpenAPI仕様ファイルのパスを指定します。$projectDirはプロジェクトのルートディレクトリを指し、openapi/openapi.yamlファイルを指定しています。
    inputSpec.set("$projectDir/openapi/openapi.yaml")

    // 生成されたコードの出力ディレクトリを指定します。$buildDirはビルドディレクトリを指し、generatedフォルダに出力されます。
    outputDir.set("$buildDir/generated")

    // 生成されたAPIコードのパッケージ名を指定します。ここではcom.app.apiパッケージに生成されます。
    apiPackage.set("com.app.api")

    // 生成されたインボーカーコード（APIリクエストを行うコード）のパッケージ名を指定します。ここではcom.app.invokerパッケージに生成されます。
    invokerPackage.set("com.app.invoker")

    // 生成されたモデルクラスのパッケージ名を指定します。ここではcom.app.modelパッケージに生成されます。
    modelPackage.set("com.app.model")

    // 追加の設定オプションを指定します。ここでは、日付ライブラリに「java8」を使用するように設定しています。
    configOptions.put("dateLibrary", "java8")

    // カスタムテンプレートのディレクトリを指定
    templateDir.set("$projectDir/openapi/templates")
}
