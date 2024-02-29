package com.org.exportdatadb2csv.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;

import com.org.exportdatadb2csv.processor.RecordProcessor;

public interface IBatchConfig {

	public ItemReader<?> reader();

	public FlatFileItemWriter<?> writer();

	public RecordProcessor processor();

	public Job jobRunner();

}
