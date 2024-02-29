package com.org.exportdatadb2csv.processor;

import org.springframework.batch.item.ItemProcessor;

import com.org.exportdatadb2csv.entity.Student;

public class RecordProcessor implements ItemProcessor<Student, Student> {

	@Override
	public Student process(Student item) throws Exception {
		return item;
	}

}
