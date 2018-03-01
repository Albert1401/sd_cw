package org.jetbrains.kotlin.demo

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.kotlin.demo.model.MediaManager
import org.jetbrains.kotlin.demo.model.initDatabase
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Application

fun main(args: Array<String>) {
    initDatabase("/home/clitcommander/sd/kotlin-examples/tutorials/spring-boot-restful1/file.db")
    transaction { MediaManager.createSession()}
    SpringApplication.run(Application::class.java, *args)
}