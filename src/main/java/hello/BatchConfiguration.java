package hello;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.BackToBackPatternClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;
    
    @Autowired
    private PersonRepository personRepository;

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<PersonDto> reader() {
        FlatFileItemReader<PersonDto> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("MOCK_DATA_2.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<PersonDto>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstName", "lastName", "email"
                		,"gender", "company", "position", "phone", "address"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<PersonDto>() {{
                setTargetType(PersonDto.class);
            }});
        }});
        return reader;
    }

    @Bean   
    @StepScope
    public PersonItemProcessor processor(
    		@Value("#{jobParameters[outputType]}") String outputType) {
        return new PersonItemProcessor(outputType);
    }

    @Bean
    public MyWriter mysqlWriter() {
        return new MyWriter();
    }
    
    
    @Bean
    public JdbcBatchItemWriter<Person> writer2() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
        writer.setSql("INSERT INTO people2 (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }
  
   @Bean
   @StepScope
   public FlatFileItemWriter<PersonDto> cvsFileItemWriter(
		   @Value("#{jobParameters[outputFilename]}") String outputFilename,
		   @Value("#{jobParameters[headers]}") String headers) {
		FlatFileItemWriter<PersonDto> writer = new FlatFileItemWriter<>();
		writer.setHeaderCallback(new FlatFileHeaderCallback() {

			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write(headers);
			}
		});
		outputFilename = outputFilename.concat(new Date().getTime() + ".csv");
		Resource resource = new FileSystemResource(outputFilename);
		writer.setResource(resource);
		writer.setShouldDeleteIfExists(true);
		DelimitedLineAggregator<PersonDto> aggregaor = new DelimitedLineAggregator<>();
		aggregaor.setDelimiter(",");
		BeanWrapperFieldExtractor<PersonDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(headers.split(","));
		aggregaor.setFieldExtractor(fieldExtractor);
	   writer.setLineAggregator(aggregaor);
	   ExecutionContext executionContext = new ExecutionContext();
	   writer.open(executionContext);
	   return writer;
   }
   
   @Bean
   @StepScope
   public ExcelWriter excelWriter(@Value("#{jobParameters[outputFilename]}") String outputFilename,
		   @Value("#{jobParameters[headers]}") String headers) {
	   ExcelWriter writer = new ExcelWriter(outputFilename, headers);
	   writer.open(new ExecutionContext());
	   return writer;
   }
 
/*   @Bean
   public Classifier<PersonDto, ItemWriter<PersonDto>> writerClassifier() {
	   return new WriterClassifier();
   }*/
   
    @Bean
    @Qualifier("classifierCompositeWriter")
    @StepScope
    public ClassifierCompositeItemWriter<PersonDto> getCompositeWriter(    		
    		FlatFileItemWriter<PersonDto> cvsFileItemWriter, ExcelWriter excelWriter) {
    	
    	BackToBackPatternClassifier classifier = new BackToBackPatternClassifier();
		classifier.setRouterDelegate(new WriterClassifier());
		Map<String, ItemWriter<PersonDto>> matcherMap = new HashMap<>();
		matcherMap.put("csv", cvsFileItemWriter);
		matcherMap.put("excel", excelWriter);
		classifier.setMatcherMap(matcherMap);		
    	
		ClassifierCompositeItemWriter<PersonDto> writer = new ClassifierCompositeItemWriter<>();
    	writer.setClassifier(classifier);
    	
    	return writer;
    }
   
   @Bean
   @Qualifier("compositeWriter")
   @StepScope
   public CompositeItemWriter<PersonDto> compositeWriter(
		   @Value("#{jobParameters[outputFilename]}") String outputFilename,
		   @Value("#{jobParameters[headers]}") String headers, 
		   FlatFileItemWriter<PersonDto> cvsFileItemWriter,
		   ClassifierCompositeItemWriter<PersonDto> classifierWriter) {
	   CompositeItemWriter<PersonDto> writer = new CompositeItemWriter<>();
	   writer.setDelegates(Arrays.asList(mysqlWriter(), classifierWriter));
	   return writer;
   }
   
    

    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step step1(
    		ExcelWriter excelWriter,
    		FlatFileItemWriter<PersonDto> cvsFileItemWriter,
    		PersonItemProcessor processor, 
    		@Qualifier("classifierCompositeWriter")ItemWriter<PersonDto> classifierCompositeWriter,
    		@Qualifier("compositeWriter")CompositeItemWriter<PersonDto> compositeWriter) {
        return stepBuilderFactory.get("step1").allowStartIfComplete(true)
                .<PersonDto, PersonDto> chunk(1000)
                .reader(reader())
                .processor(processor)
                .writer(compositeWriter)
                .build();
    }
    // end::jobstep[]
    
    //=================================== Batch Job Configuration ==============================
    @Bean
	public JobRepository jobRepository(DataSource dataSource) throws Exception {

		JobRepositoryFactoryBean jobRepo = new JobRepositoryFactoryBean();
		jobRepo.setDataSource(dataSource);
		jobRepo.setTransactionManager(transactionManager());
		return jobRepo.getObject();

	}

	@Bean
	public JpaTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}

	@Bean
	public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(taskExecutor());
		return jobLauncher;
	}
	
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(5);
		return asyncTaskExecutor;
	}

	@Bean
	public JobRegistry jobRegistry() {
		return new MapJobRegistry();
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry());
		return jobRegistryBeanPostProcessor;
	}
}
