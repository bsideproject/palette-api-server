package com.palette.diary.service;

import com.palette.diary.domain.History;
import com.palette.infra.scheduler.SchedulerConfig;
import com.palette.infra.scheduler.job.HistoryFinished;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class DiaryService {
    @Autowired
    private SchedulerConfig schedulerConfig;

    public void registerHistoryFinishedJob(History history) {
        try {
            // Creating JobDetail instance
            String jobDetailId = "History" + history.getId();
            JobDetail jobDetail = JobBuilder.newJob(HistoryFinished.class).withIdentity(jobDetailId).build();

            // Adding JobDataMap to JobDetail
            jobDetail.getJobDataMap().put("historyId", history.getId().toString());

            // Scheduling time to run job
            Date triggerJobAt = java.sql.Timestamp.valueOf(history.getEndDate());

            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobDetailId)
                    .startAt(triggerJobAt)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .build();

            // Getting scheduler instance
            Scheduler scheduler = schedulerConfig.schedulerFactoryBean().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
            System.out.println("Registered! -> " + history.getId());
        } catch (IOException | SchedulerException e) {
            e.printStackTrace();
        }

    }

}
