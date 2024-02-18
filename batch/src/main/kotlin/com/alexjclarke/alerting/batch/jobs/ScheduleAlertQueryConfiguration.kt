package com.alexjclarke.alerting.batch.jobs

import com.alexjclarke.alerting.batch.AutowiringSpringBeanJobFactory
import org.quartz.spi.JobFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import javax.sql.DataSource

@Configuration
@ComponentScan
@EnableAutoConfiguration
open class ScheduleAlertQueryConfiguration {
    @Bean
    open fun jobFactory(applicationContext: ApplicationContext): JobFactory {
        return AutowiringSpringBeanJobFactory().apply {
            setApplicationContext(applicationContext)
        }
    }

    @Bean
    open fun schedulerFactory(applicationContext: ApplicationContext, dataSource: DataSource, jobFactory: JobFactory): SchedulerFactoryBean {
        return SchedulerFactoryBean().apply {
            setOverwriteExistingJobs(true)
            isAutoStartup = true
            setDataSource(dataSource)
            setJobFactory(jobFactory)
        }
    }
}