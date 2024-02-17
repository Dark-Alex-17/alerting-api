package com.poc.alerting.batch

import org.quartz.Job
import org.quartz.SchedulerContext
import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.MutablePropertyValues
import org.springframework.beans.PropertyAccessorFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.quartz.SpringBeanJobFactory


class AutowiringSpringBeanJobFactory : SpringBeanJobFactory(), ApplicationContextAware {
    private var ctx: ApplicationContext? = null
    private var schedulerContext: SchedulerContext? = null
    override fun setApplicationContext(context: ApplicationContext) {
        ctx = context
    }

    override fun createJobInstance(bundle: TriggerFiredBundle): Any {
        val job: Job = ctx!!.getBean(bundle.jobDetail.jobClass)
        val bw = PropertyAccessorFactory.forBeanPropertyAccess(job)
        val pvs = MutablePropertyValues()
        pvs.addPropertyValues(bundle.jobDetail.jobDataMap)
        pvs.addPropertyValues(bundle.trigger.jobDataMap)
        if (this.schedulerContext != null) {
            pvs.addPropertyValues(this.schedulerContext)
        }
        bw.setPropertyValues(pvs, true)
        return job
    }

    override fun setSchedulerContext(schedulerContext: SchedulerContext) {
        this.schedulerContext = schedulerContext
        super.setSchedulerContext(schedulerContext)
    }
}