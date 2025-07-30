package com.controllers;

import com.beans.Application;
import com.dao.ApplicationDao;
import com.beans.FileUploadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Controller
public class ApplicationController {

    private static final String UPLOAD_DIR =  "D:\\Servelt\\Final_JN\\webcontent\\resources\\uploads\\";  //check
    @Autowired
    private ApplicationDao applicationDao;

    @RequestMapping(value = "jobdetail/application/{jobId}", method = RequestMethod.GET)
    public String applyForJob(@PathVariable("jobId") Integer jobId, Model model, HttpSession session) {
        Application application = new Application();

        System.out.println("-------application part-----");
        Integer jobSeekerId = (Integer) session.getAttribute("jobSeekerId");
        System.out.println(jobSeekerId);
        session.setAttribute("jobId", jobId);
        
        if (jobSeekerId == null) {
            return "error"; //no job seeker ID in session
        }

        application.setJobSeekerId(jobSeekerId);
        application.setJobId(jobId);

        // Check resume uploaded
        if (!applicationDao.hasResume(jobSeekerId)) {
        	System.out.print("no resume with hasResume");
            return "no_resume"; 
        }
//        if (applicationDao.isResumeFileNull(jobSeekerId)) {
//            
//            return "no_resume"; 
//        }

        application.setApplicationDate(LocalDateTime.now());
        application.setStatus("Pending");

        // Save the application to the database
        applicationDao.saveApplication(application);

        // Add the application to the model (optional)
        model.addAttribute("application", application);

        // Redirect to a confirmation page
        return "application_confirm";
    }

    //Checking for Resume
    @RequestMapping(value = "jobdetail/application/uploadResumeForm", method = RequestMethod.GET)
    public String showUploadResumeForm(Model model) {
        model.addAttribute("fileUploadForm", new FileUploadForm());
        return "no_resume"; // The JSP page that displays the form
    }

    
    @RequestMapping(value = "jobdetail/application/uploadResume", method = RequestMethod.POST)
    public String uploadResume(@ModelAttribute("fileUploadForm") FileUploadForm form, HttpSession session, Model model) {
        MultipartFile file = form.getResumeFile();
        Integer jobSeekerId = (Integer) session.getAttribute("jobSeekerId");
        Integer jobId = (Integer) session.getAttribute("jobId");
        
        Application application = new Application();        
        
        if (jobSeekerId == null) {
            return "error"; //no job seeker ID in session
        }
        application.setJobSeekerId(jobSeekerId);
        application.setJobId(jobId);

        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            System.out.print("no file select");
            return "no_resume"; 
        }       

        try {
        	
        	Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
          
            System.out.println("Upload Directory: " + UPLOAD_DIR);
            
            String fileName = jobSeekerId + "_" + file.getOriginalFilename();
            System.out.println("File received: " + file.getOriginalFilename());

            Path filePath = uploadPath.resolve(fileName);
            System.out.println("File: " + fileName);

            // Save the uploaded file to the target location
            Files.copy(file.getInputStream(), filePath);


            // Update resume path in the database
            String resumePath = "uploads/" + file.getOriginalFilename();
            applicationDao.updateResumePath(jobSeekerId, resumePath);
            
            // Get the file data as a byte array
           // byte[] fileData = file.getBytes();

            // Save the file data to the database using the DAO
            //applicationDao.updateResumeFile(jobSeekerId, fileData);

            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("error");
            System.out.println("Error saving file: " + e.getMessage());
            return "no_resume"; 
        }
        // After successful upload, 
        // Save the application to the database
        application.setApplicationDate(LocalDateTime.now());
        application.setStatus("Pending");
        applicationDao.saveApplication(application);

        // Add the application to the model
        model.addAttribute("application", application);
        
        return "application_confirm"; 
    }

    

}
