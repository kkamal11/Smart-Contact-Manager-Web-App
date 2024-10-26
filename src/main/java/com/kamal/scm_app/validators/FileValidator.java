package com.kamal.scm_app.validators;

import com.kamal.scm_app.utils.AppConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<ValidUploadedFile, MultipartFile> {

    private static final Long MAX_FILE_SIZE = AppConstants.MAX_FILE_SIZE;

    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if(file == null || file.isEmpty()){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File cannot be empty").addConstraintViolation();
            return false;
        }
        if(file.getSize() > MAX_FILE_SIZE){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size should be less that " + MAX_FILE_SIZE + " KB").addConstraintViolation();
            return false;
        }
        return true;
    }
}
