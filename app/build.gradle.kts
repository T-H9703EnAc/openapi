import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.24"
    id( "org.openapi.generator") version "7.6.0"
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
    implementation("org.openapitools:openapi-generator-cli:7.6.0")
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
    inputSpec.set("$projectDir/src/main/resources/openapi.yaml")

    // 生成されたコードの出力ディレクトリを指定します。$buildDirはビルドディレクトリを指し、generatedフォルダに出力されます。
    outputDir.set("$buildDir/generated")

    // 生成されたAPIコードのパッケージ名を指定します。ここではcom.app.apiパッケージに生成されます。
    apiPackage.set("com.app.api")

    // 生成されたインボーカーコード（APIリクエストを行うコード）のパッケージ名を指定します。ここではcom.app.invokerパッケージに生成されます。
    invokerPackage.set("com.app.invoker")

    // 生成されたモデルクラスのパッケージ名を指定します。ここではcom.app.modelパッケージに生成されます。
    modelPackage.set("com.app.model")
    
    configOptions.putAll(
        mapOf(
            // 日付ライブラリに「java11」を使用するように設定しています。
            "dateLibrary" to "java11",
        )
    )

    // カスタムテンプレートのディレクトリを指定
    templateDir.set("$projectDir/src/main/resources/templates/kotlin-spring")

}

tasks.register<JavaExec>("generateTemplates") {
    // タスクのグループを設定
    group = "openapi"
    // タスクの説明を設定
    description = "Generate OpenAPI templates"
    classpath = configurations["runtimeClasspath"]
    mainClass.set("org.openapitools.codegen.OpenAPIGenerator")
    args = listOf("author", "template", "-g", "kotlin-spring", "-o", "$projectDir/tmp/templates")
}

tasks.register<Copy>("extractControllerTemplate") {
    dependsOn("generateTemplates")
    from("$projectDir/tmp/templates/kotlin-spring")
    include("**/apiController.mustache") // コントローラーのテンプレートファイルを指定
    into("$projectDir/src/main/resources/templates/kotlin-spring")
}

tasks.register<Delete>("cleanGeneratedTemplates") {
    group = "openapi"
    description = "Clean up downloaded templates"
    delete("$projectDir/tmp/templates")
}

tasks.register<JavaExec>("runOpenApiGenerator") {
    group = "openapi"
    description = "Run OpenAPI Generator CLI"
    classpath = configurations["runtimeClasspath"]
    mainClass.set("org.openapitools.codegen.OpenAPIGenerator")
    
    doFirst {
        // ログディレクトリを作成
        val logDir = file("$projectDir/logs")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
    }
    
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    // 標準出力と標準エラー出力をファイルにリダイレクト
    standardOutput = FileOutputStream(File("$projectDir/logs/openapi-generator-output-$timestamp.log")) as OutputStream
    errorOutput = FileOutputStream(File("$projectDir/logs/openapi-generator-error-$timestamp.log")) as OutputStream

    args = listOf(
        "generate",
        "-i", "$projectDir/src/main/resources/openapi.yaml",
        "-g", "kotlin-spring",
        "-o", "$buildDir/generated",
        "--template-dir", "$projectDir/src/main/resources/templates/kotlin-spring",
        "--verbose"
    )
}
