package fit.hutech.spring.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UploadController {
    @org.springframework.beans.factory.annotation.Value("${app.upload.dir:uploads}")
    private String uploadDir;
    @GetMapping("/uploads/{folder}/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String folder, @PathVariable String filename){
        java.io.File f = new java.io.File(uploadDir + java.io.File.separator + folder + java.io.File.separator + filename);
        if(!f.exists()){
            return ResponseEntity.notFound().build();
        }
        Resource r = new FileSystemResource(f);
        MediaType type = MediaType.IMAGE_JPEG;
        try{
            String ct = java.nio.file.Files.probeContentType(f.toPath());
            if(ct != null){
                type = MediaType.parseMediaType(ct);
            }
        }catch(Exception ignored){}
        return ResponseEntity.ok().contentType(type).body(r);
    }
}
