package com.org.exportdatadb2csv.config;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.org.exportdatadb2csv.entity.Student;
import com.org.exportdatadb2csv.entity.StudentRowMapper;
import com.org.exportdatadb2csv.processor.RecordProcessor;

import lombok.RequiredArgsConstructor;

@Configuration // Informs Spring that this class contains configurations
@EnableBatchProcessing // Enables batch processing for the application
@RequiredArgsConstructor
public class BatchConfiguration implements IBatchConfig, Steps {

	String format1 = new SimpleDateFormat("yyyy-MM-dd'-'HH-mm-ss-SSS", Locale.forLanguageTag("tr-TR")).format(new Date());
	private Resource outputResource = new FileSystemResource("output/customers_" + format1 + ".csv");
	private static final String QUERY_FIND_STUDENTS = "SELECT s.id AS student_id, s.name AS student_name, s.age, GROUP_CONCAT(DISTINCT a.address ORDER BY a.id SEPARATOR ', ') AS addresses FROM student s LEFT JOIN address a ON s.id = a.student_id GROUP BY s.id";

	private final String[] headers = new String[] { "id", "name", "age", "address" };

	@Autowired
	DataSource dataSource;

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Override
	@Bean
	public ItemReader<Student> reader() {

		JdbcCursorItemReader<Student> itemReader = new JdbcCursorItemReader<Student>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql(QUERY_FIND_STUDENTS);
		itemReader.setRowMapper(new StudentRowMapper());
		return itemReader;

	}

	@Override
	@Bean
	public FlatFileItemWriter<Student> writer() {

		FlatFileItemWriter<Student> writer = new FlatFileItemWriter<>();

		writer.setResource(outputResource);
		writer.setAppendAllowed(true);

		writer.setLineAggregator(new DelimitedLineAggregator<Student>() {
			{
				setDelimiter(",");
				setFieldExtractor(new BeanWrapperFieldExtractor<Student>() {
					{
						setNames(headers);
					}
				});
			}
		});

		writer.setHeaderCallback(new FlatFileHeaderCallback() {
			@Override
			public void writeHeader(Writer writer) throws IOException {
				for (int i = 0; i < headers.length; i++) {
					if (i != headers.length - 1)
						writer.append(headers[i] + ",");
					else
						writer.append(headers[i]);
				}
			}
		});

		return writer;
	}

	@Override
	@Bean
	public RecordProcessor processor() {
		return new RecordProcessor();
	}

	@Override
	@Bean
	public Job jobRunner() {
		return jobBuilderFactory.get("exportdatadb2csv")
				.flow(step1())
				.end()
				.build();
	}

	@Override
	@Bean
	public Step step1() {
        return stepBuilderFactory.get("csv-step").<Student,Student>chunk(50)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

}
