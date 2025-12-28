import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish") version "0.35.0"
}

group = "io.github.aziztinwala"
version = "1.0.0"

android {
    namespace = "com.saifee.permissionmanagement"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()
    configure(AndroidSingleVariantLibrary(
        // the published variant
        variant = "release",
        // whether to publish a sources jar
        sourcesJar = true,
        // whether to publish a javadoc jar
        publishJavadocJar = true,
    ))

    coordinates(group.toString(), "permission-management", version.toString())

    pom {
        name.set("Permission Management")
        description.set("A simple and flexible Android runtime permission management system")
        inceptionYear.set("2025")
        url.set("https://github.com/AzizTinwala/permissioncontrolsystem")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("aziztinwala")
                name.set("Aziz Tinwala")
                email.set("mraziz5251@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/AzizTinwala/permissioncontrolsystem.git")
            developerConnection.set("scm:git:ssh://git@github.com/AzizTinwala/permissioncontrolsystem.git")
            url.set("https://github.com/AzizTinwala/permissioncontrolsystem")
        }
    }
}
