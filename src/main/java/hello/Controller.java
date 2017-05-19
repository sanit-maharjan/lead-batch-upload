package hello;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/job")
@RestController
public class Controller {

	@Autowired
	JobLauncher jobLauncher;
	@Autowired
	JobRegistry jobRegistry;
	
	@RequestMapping
	public void launchJob(@RequestParam String headers) {
		
	/*	jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.addString("pathToFile", dncFileLocation + headerMap.get("fileName")).addString("type", type)
				.addString("headers", header).addString("clientCompanyId", clientCompanyId)
				.addLong("total",
						(long) FileFolderUtils.getNumberOfLines(dncFileLocation + headerMap.get("fileName")) - 1)
				.toJobParameters();*/
		
		JobParameters parameter = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.addString("headers", headers)
			.toJobParameters();
		
		try {
			jobLauncher.run(jobRegistry.getJob("importUserJob"), parameter);
		} catch (JobExecutionAlreadyRunningException | JobRestartException
				| JobInstanceAlreadyCompleteException
				| JobParametersInvalidException | NoSuchJobException e) {
			e.printStackTrace();
		}
	}
}
	