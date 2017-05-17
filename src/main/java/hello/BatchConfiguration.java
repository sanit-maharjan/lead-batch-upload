package hello;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
        reader.setResource(new ClassPathResource("MOCK_DATA.csv"));
        reader.setLineMapper(new DefaultLineMapper<PersonDto>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstName", "lastName", "gender", "company" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<PersonDto>() {{
                setTargetType(PersonDto.class);
            }});
        }});
        return reader;
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

   @Bean
    public MyWriter writer1() {
        return new MyWriter();
    }
    
/*    @Bean
    public JdbcBatchItemWriter<Person> writer2() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
        writer.setSql("INSERT INTO people2 (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }
    
    @Bean
    public CompositeItemWriter<Person> getCompositeWriter() {
    	CompositeItemWriter<Person> compositeWriter = new CompositeItemWriter<>();
    	compositeWriter.setDelegates(Arrays.asList(writer2(), writer1()));
    	return compositeWriter;
    }*/
    

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
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<PersonDto, PersonDto> chunk(1000)
                .reader(reader())
                .processor(processor())
                .writer(writer1())
                .build();
    }
    // end::jobstep[]
}
