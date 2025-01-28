package com.example.batch_processing.config;

import com.example.batch_processing.entity.User;
import com.example.batch_processing.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final UserRepository userRepository;

    @Autowired
    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.userRepository = userRepository;
    }

    @Bean
    public FlatFileItemReader<User> reader() {
        return new FlatFileItemReaderBuilder<User>()
                .resource(new FileSystemResource("src/main/resources/users.csv"))
                .name("csvReader")
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("id", "firstname", "lastname", "email")
                .targetType(User.class)
//                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
//                    setTargetType(User.class);
//                }})
//                .lineMapper(lineMapper())
                .build();
    }

    @Bean
    public UserProcessor processor() {
        return new UserProcessor();
    }

    @Bean
    public RepositoryItemWriter<User> writer() {
        RepositoryItemWriter<User> writer = new RepositoryItemWriter<>();
        writer.setRepository(userRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step() {
        return new StepBuilder("csvImport", jobRepository)
                .<User, User>chunk(1, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
//                .taskExecutor(taskExecutor())
//                .faultTolerant()
//                .retry(NullPointerException.class)
//                .retryLimit(3)
//                .backOffPolicy(new FixedBackOffPolicy() {{
//                    setBackOffPeriod(2000);
//                }})
//                .noRetry(RuntimeException.class)
//                .startLimit(1)
                .build();
    }

//    @Bean
//    public SimpleFlow flow() {
//        return new FlowBuilder<SimpleFlow>("flow")
//                .start(step())
//                .next(step()).from(step()).on("FAILED").to(step())
//                .next(step())
//                .end();
//    }

    @Bean
    public Job job() {
        return new JobBuilder("importUsers", jobRepository)
                .start(step())
                .build();
    }

//    @Bean
//    public TaskExecutor taskExecutor() {
//        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
//        asyncTaskExecutor.setConcurrencyLimit(10);
//        return asyncTaskExecutor;
//    }

    private LineMapper<User> lineMapper() {
        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstname", "lastname", "email");

        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
}
