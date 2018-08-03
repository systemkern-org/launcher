package systemkern.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import systemkern.configuration.FileStorageProperties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(FileStorageProperties.class)
@SpringBootTest
@SpringBootConfiguration
public class FileStorageServiceTest {

    @Autowired
    private FileStorageProperties fileStorageProperties;

    private FileStorageService fileStorageService;

    @Before
    public void setUp() {
        fileStorageService = new FileStorageService(fileStorageProperties);
    }

    @Test
    public void upload() throws Exception {

        final String noSuffixInFile = null;

        //File f = new File(System.getProperty("java.io.tmpdir").concat("/").concat("Penguins.jpg"));

        File file = File.createTempFile("prueba", noSuffixInFile, new File(System.getProperty("java.io.tmpdir")));
        file.deleteOnExit();

        BufferedWriter bw = null;
        FileWriter fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        bw.write("file content");

        FileInputStream fi1 = new FileInputStream(file);

        final MockMultipartFile avatar = new MockMultipartFile("file", "test.dat", "application/octet-stream", fi1);

        fileStorageService.storeFile(avatar);

        File fileToCheck = new File(fileStorageProperties.getUploadDir()+"/"+avatar.getOriginalFilename());
        Assert.assertTrue(fileToCheck.exists());

        deleteFile(fileToCheck);
    }

    private void deleteFile(File fileToDelete){
        if (!fileToDelete.delete()){
            System.out.println("file has not been deleted");
        }
    }
}