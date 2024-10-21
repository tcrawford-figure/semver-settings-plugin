/*
 * Copyright (C) 2024 Tyler Crawford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
pluginManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://nexus.figure.com/repository/mirror")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PASS")
            }
        }
        maven {
            url = uri("https://nexus.figure.com/repository/figure")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PASS")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://nexus.figure.com/repository/mirror")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PASS")
            }
        }
        maven {
            url = uri("https://nexus.figure.com/repository/figure")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PASS")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("com.figure.gradle.semver") version "2.0.1"
}
