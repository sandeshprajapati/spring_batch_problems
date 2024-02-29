package com.org.exportdatadb2csv.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StudentRowMapper implements RowMapper<Student> {

	@Override
	public Student mapRow(ResultSet rs, int rowNum) throws SQLException {

		Student s = new Student();
		s.setId(rs.getInt("student_id"));
		s.setName(rs.getNString("student_name"));
		s.setAge(rs.getInt("age"));
		s.setAddress(rs.getNString("addresses"));
		return s;
	}

}
