package hello;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.batch.runtime.BatchStatus;

import org.springframework.batch.core.JobExecution;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/job")
@RestController
public class Controller {

	@Autowired
	JobLauncher jobLauncher;
	@Autowired
	JobRegistry jobRegistry;
	
	@RequestMapping(method = RequestMethod.POST)
	public String launchJob(@RequestBody List<String> headers, @RequestParam String outputType) throws IllegalArgumentException, IllegalAccessException {
		HeaderMapper headerMapper = new HeaderMapper();
		
		Class headerMapperClass = headerMapper.getClass();
		Field[] mapperField = headerMapperClass.getDeclaredFields();
		StringBuilder convertedHeaderBuffer = new StringBuilder();
		
		for (String header : headers) {
			for (Field field : mapperField) {
				if(header.equals(field.get(headerMapper))) {
					convertedHeaderBuffer.append(field.getName() + ",");
				}
						
			}
		}
		
		String convertedHeader = convertedHeaderBuffer.toString().substring(0, 
				convertedHeaderBuffer.toString().length() - 1);
		System.out.println("headers =" + convertedHeader );
		JobParameters parameter = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.addString("headers", convertedHeader)
			.addString("outputFilename", "/home/lt105/")
			.addString("outputType", outputType)
			.toJobParameters();
		
		try {
			JobExecution exe = jobLauncher.run(jobRegistry.getJob("importUserJob"), parameter);
			while(!exe.getStatus().getBatchStatus().equals(BatchStatus.COMPLETED)) {
				
			}
			return "complete";
		} catch (JobExecutionAlreadyRunningException | JobRestartException
				| JobInstanceAlreadyCompleteException
				| JobParametersInvalidException | NoSuchJobException e) {
			e.printStackTrace();
		}
		return "fail";
	}
}
	