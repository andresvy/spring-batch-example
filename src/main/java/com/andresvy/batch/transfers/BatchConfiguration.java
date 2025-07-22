package com.andresvy.batch.transfers;

import com.andresvy.batch.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    @Bean
    public FlatFileItemReader<Transfer> reader() {
        return new FlatFileItemReaderBuilder<Transfer>()
                .name("transferItemReader")
                .resource(new ClassPathResource("data/transfers.csv"))
                .delimited()
                .delimiter(",")
                .names("sourceBankCode", "sourceAccountNumber", "destinationBankCode", "destinationAccountNumber", "amount")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Transfer>() {{
                    setTargetType(Transfer.class);
                }})
                .build();
    }

    @Bean
    public TransferProcessor processor() {
        return new TransferProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Transfer> writer(DataSource dataSource) {
        // Hay que definir el SQL INSERT
        String sql = "INSERT INTO transfer (id, source_bank_code, source_account_number, " +
                "destination_bank_code, destination_account_number, amount) " +
                "VALUES (:id, :sourceBankCode, :sourceAccountNumber, " +
                ":destinationBankCode, :destinationAccountNumber, :amount)";

        // Entrega los parámetros al insert
        return new JdbcBatchItemWriterBuilder<Transfer>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(sql)
                .dataSource(dataSource)
                .build();
    }

    // --- Step (Define un paso del Job: Reader -> Processor -> Writer) ---
    @Bean
    public Step importTransferStep(JobRepository jobRepository,
                                   DataSourceTransactionManager transactionManager,
                                   FlatFileItemReader<Transfer> reader,
                                   TransferProcessor processor,
                                   JdbcBatchItemWriter<Transfer> writer) {
        return new StepBuilder("importTransferStep", jobRepository)
                .<Transfer, Transfer>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importTransferJob(JobRepository jobRepository,
                                 JobCompletionNotificationListener listener,
                                 Step importTransferStep) {
        return new JobBuilder("importTransferJob", jobRepository)
//                .incrementer(new RunIdIncrementer()) // Asegura que cada ejecución del Job tenga un ID único
                .listener(listener) // El listener es opcional
                .flow(importTransferStep) // Define el flujo del Job
                .end()
                .build();
    }
}
