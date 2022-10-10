package com.example.demo.service.impl;

import com.example.demo.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class MailServiceImpl extends QuartzJobBean implements MailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    public MailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendMail(Map<String, Object> props, String email, String template, String subject) throws MessagingException {
        Context context = new Context();
        context.setVariables(props);
        String html = templateEngine.process(template, context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);
    }

    @Override
    public void sendProductLaunchEmail(String email, String username, String car) throws MessagingException {
        Map<String, Object> props = new HashMap<>();
        props.put("name", username);
        props.put("car", car);

        Context context = new Context();
        context.setVariables(props);
        String html = templateEngine.process("launchDate", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        helper.setTo(email);
        helper.setSubject("Ngày ra mắt xe");
        helper.setText(html, true);

        mailSender.send(message);
    }

//   * QuartzJobBean
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Executing Job with key ", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String email = jobDataMap.getString("email");
        String username = jobDataMap.getString("username");
        String car = jobDataMap.getString("car");

        try {
            sendProductLaunchEmail(email, username, car);
        } catch (MessagingException e) {
            log.error("Failed to send email to", email);
        }
    }

    public JobDetail buildJobDetail(String email, String username, String car) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", email);
        jobDataMap.put("username", username);
        jobDataMap.put("car", car);

        return JobBuilder.newJob(MailServiceImpl.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
