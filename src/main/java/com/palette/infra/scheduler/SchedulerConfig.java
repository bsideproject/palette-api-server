package com.palette.infra.scheduler;

import java.io.IOException;
import java.util.Properties;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public JobFactory jobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setQuartzProperties(quartzProperties());
        schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactory.setAutoStartup(true);
        schedulerFactory.setJobFactory(jobFactory());
        return schedulerFactory;
    }

    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        Properties properties = new Properties();
        properties.put("org.quartz.jobStore.dataSource", "mySql");
        properties.put("org.quartz.dataSource.mySql.provider", "hikaricp");
        properties.put("org.quartz.dataSource.mySql.driver", "com.mysql.cj.jdbc.Driver");
        properties.put("org.quartz.dataSource.mySql.maxConnections", 10);
        properties.put("org.quartz.dataSource.mySql.URL", datasourceUrl);
        properties.put("org.quartz.dataSource.mySql.user", datasourceUsername);
        properties.put("org.quartz.dataSource.mySql.password", datasourcePassword);
        propertiesFactoryBean.setProperties(properties);
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
