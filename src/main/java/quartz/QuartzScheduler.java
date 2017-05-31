package quartz;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuartzScheduler {

	private static Scheduler scheduler = null;
	
	@SuppressWarnings(value = { "unchecked", "rawtypes" })
	public static void main(String[] args) throws SchedulerException, ParseException {
		start();
		JobDetail job1 = newJob(MyJob.class)
				.withIdentity("job2", "group2").storeDurably(true)
				.build();
		scheduler.addJob(job1, false);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm");
		Date targetTime = df.parse("2017-05-29 17:47");
		Trigger trigger1 = newTrigger()
				.withIdentity("trigger1", "group2")
				.forJob("job2", "group2")
				.startAt(targetTime).build();
		scheduler.scheduleJob(trigger1);
	
		
		targetTime = df.parse("2017-05-29 17:48");
		Trigger trigger2 = newTrigger()
				.withIdentity("trigger2", "group2")
				.forJob("job2", "group2")
				.startAt(targetTime).build();
		scheduler.scheduleJob(trigger2);
		/*JobDetail job1 = newJob(MyJob.class)
				.withIdentity("job1", "group1").storeDurably(true)
				.build();
		try {
			scheduler.addJob(job1, false);
		} catch (SchedulerException e) {
			System.out.println("job already exists");
		}
		
		Trigger oldTrigger = null;	
		try {
			oldTrigger = scheduler.getTrigger(new TriggerKey("trigger1", "group1"));
			if (oldTrigger != null) {
				System.out.println("trigger already exists");
				TriggerBuilder tb = oldTrigger.getTriggerBuilder();
				Trigger newTrigger = tb.withSchedule(
						CronScheduleBuilder.cronSchedule("0 0/10 * * * ?")).build();
				scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
			} else {
				// Schedule the trigger
				System.out.println(oldTrigger);
				System.out.println("trigger doesnt  exists");
				Trigger trigger = newTrigger()
						.withIdentity("trigger1", "group1")
						.forJob("job1", "group1")
						.withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")).build();

				scheduler.scheduleJob(trigger);
			}

		} catch (SchedulerException e) {
			e.printStackTrace();
		}*/
	}
	
	@PostConstruct
	public static void start() {
	
		try {
			Properties prop = new Properties();
			InputStream input = null;
			input = QuartzScheduler.class.getClassLoader().getResourceAsStream(
					"quartz/quartz.local.properties");
			prop.load(input);
			SchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(prop);
			scheduler = stdSchedulerFactory.getScheduler();		
			scheduler.start(); // dont start in beta yet
	

		} catch (SchedulerException | IOException se) {
			se.printStackTrace();
		}
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

}
