package me.dusan.sfr.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
public class FileSystemStorageService {

    public Resource loadAsResource(String filename) throws Exception {
        try {
            Resource resource = new ClassPathResource(filename);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new Exception("Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new Exception("Could not read file: " + filename, e);
        }
    }

}
