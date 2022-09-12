package com.palette.diary.service;

import com.palette.diary.domain.History;
import com.palette.infra.scheduler.SchedulerConfig;
import com.palette.infra.scheduler.job.HistoryFinished;
import com.palette.infra.scheduler.job.HistoryRemindOne;
import com.palette.infra.scheduler.job.HistoryRemindTwo;
import io.sentry.Sentry;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryService {

    @Autowired
    private SchedulerConfig schedulerConfig;

    public void registerHistoryFinishedJob(History history) {
        try {
            // Creating JobDetail instance
            String jobDetailId = "History" + history.getId();
            JobDetail jobDetail = JobBuilder.newJob(HistoryFinished.class).withIdentity(jobDetailId)
                .build();

            // Adding JobDataMap to JobDetail
            jobDetail.getJobDataMap().put("historyId", history.getId().toString());

            // Scheduling time to run job
            Date triggerJobAt = java.sql.Timestamp.valueOf(history.getEndDate());

            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobDetailId)
                .startAt(triggerJobAt)
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

            // Getting scheduler instance
            Scheduler scheduler = schedulerConfig.schedulerFactoryBean().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (IOException | SchedulerException e) {
            Sentry.captureException(e);
        }

    }

    //1차 알림(12 * 선택일수)
    public void registerHistoryRemindOneJob(History history) {
        try {
            // Creating JobDetail instance
            String jobDetailId = "HistoryRemindOne" + history.getId();
            JobDetail jobDetail = JobBuilder.newJob(HistoryRemindOne.class)
                .withIdentity(jobDetailId)
                .build();

            // Adding JobDataMap to JobDetail
            jobDetail.getJobDataMap().put("historyId", history.getId().toString());

            LocalDateTime startDate = history.getStartDate();
            LocalDateTime endDate = history.getEndDate();

            int period = (int) ChronoUnit.DAYS.between(startDate, endDate);

            // Scheduling time to run job
            Date triggerJobAt = java.sql.Timestamp.valueOf(
                history.getEndDate().minusHours(12L * period));

            //범위
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobDetailId)
                .startAt(triggerJobAt)
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

            // Getting scheduler instance
            Scheduler scheduler = schedulerConfig.schedulerFactoryBean().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (IOException | SchedulerException e) {
            Sentry.captureException(e);
        }

    }

    //2차 알림(종료 전 24시간)
    public void registerHistoryRemindTwoJob(History history) {
        try {
            // Creating JobDetail instance
            String jobDetailId = "HistoryRemindTwo" + history.getId();
            JobDetail jobDetail = JobBuilder.newJob(HistoryRemindTwo.class)
                .withIdentity(jobDetailId)
                .build();

            // Adding JobDataMap to JobDetail
            jobDetail.getJobDataMap().put("historyId", history.getId().toString());

            // Scheduling time to run job
            Date triggerJobAt = java.sql.Timestamp.valueOf(
                history.getEndDate().minusHours(24L));

            //범위
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobDetailId)
                .startAt(triggerJobAt)
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

            // Getting scheduler instance
            Scheduler scheduler = schedulerConfig.schedulerFactoryBean().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (IOException | SchedulerException e) {
            Sentry.captureException(e);
        }

    }


}
