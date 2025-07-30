package com.dao;

import com.beans.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import java.sql.PreparedStatement;
import org.springframework.jdbc.core.PreparedStatementSetter;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ApplicationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveApplication(Application application) {
        String sql = "INSERT INTO Applications (job_id, job_seeker_id, application_date, status) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, 
                                application.getJobId(), 
                                application.getJobSeekerId(), 
                                application.getApplicationDate(), 
                                application.getStatus());
            System.out.println("Application saved successfully for job seeker ID " + application.getJobSeekerId());
        } catch (Exception e) {           
            System.err.println("Error saving application for job seeker ID " + application.getJobSeekerId() + ": " + e.getMessage());
        }
    }

    public boolean hasResume(int jobSeekerId) {
        String sql = "SELECT cv FROM JobSeekers WHERE job_seeker_id = ?";
        // Handling potential null values and checking if resume exists
        String resumePath = null;
        try {
            resumePath = jdbcTemplate.queryForObject(sql, new Object[]{jobSeekerId}, String.class);
        } catch (Exception e) {
            // Log the exception and handle it if needed
            System.err.println("Error checking resume for job seeker ID " + jobSeekerId + ": " + e.getMessage());
        }
        return resumePath != null && !resumePath.trim().isEmpty();
    }
    
//    public boolean isResumeFileNull(Integer jobSeekerId) {
//    	 System.out.println("isNullcheck");
//        String sql = "SELECT cv IS NULL FROM jobseekers WHERE job_seeker_id = ?";       
//        return jdbcTemplate.queryForObject(sql, new Object[]{jobSeekerId}, Boolean.class);
//    }
    
//    public boolean isResumeFileNull(Integer jobSeekerId) {
//        String sql = "SELECT CASE WHEN cv IS NULL THEN TRUE ELSE FALSE END FROM jobseekers WHERE job_seeker_id = ?";
//        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, new Object[]{jobSeekerId}, Boolean.class));
//    }

    
    public void updateResumePath(int jobSeekerId, String resumePath) {
        String sql = "UPDATE JobSeekers SET cv = ? WHERE job_seeker_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, resumePath, jobSeekerId);
            if (rowsAffected > 0) {
                System.out.println("Resume path updated successfully for job seeker ID " + jobSeekerId);
                System.out.println("uploaded into database");
            } else {
                System.out.println("No job seeker found with ID " + jobSeekerId);
            }
            
        } catch (Exception e) {
            // Log the exception for debugging purposes
            System.err.println("Error updating resume path for job seeker ID " + jobSeekerId + ": " + e.getMessage());
        }
    }
    
}
