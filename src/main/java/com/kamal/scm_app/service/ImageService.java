package com.kamal.scm_app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.kamal.scm_app.utils.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {


    private final Cloudinary cloudinary;
    public ImageService(Cloudinary cloudinary){ //same as @Autowired
        this.cloudinary = cloudinary;
    }

    public String uploadImageToCloud(MultipartFile contactPicture, String fileName) {

        try {
            //get the image data
            byte[] imageDataArray = new byte[contactPicture.getInputStream().available()];
            contactPicture.getInputStream().read(imageDataArray); //get the image data in the array

            //Upload the image
            Map params1 = ObjectUtils.asMap(
                    "public_id",fileName,
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true
            );
            cloudinary.uploader().upload(imageDataArray, params1);
            return this.getUrlFromPublicId(fileName);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUrlFromPublicId(String publicId){
        Integer width = AppConstants.CONTACT_IMAGE_WIDTH;
        Integer height = AppConstants.CONTACT_IMAGE_HEIGHT;
        String crop = AppConstants.CONTACT_IMAGE_CROP;

        return cloudinary
                .url()
                .transformation(
                        new Transformation<>()
                                .width(width)
                                .height(height)
                                .crop(crop)
                )
                .generate(publicId);
    }
}
