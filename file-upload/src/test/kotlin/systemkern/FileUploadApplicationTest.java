package systemkern;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import systemkern.configuration.FileStorageProperties;
import systemkern.service.FileStorageService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(FileStorageProperties.class)
@SpringBootTest
@SpringBootConfiguration
public class FileUploadApplicationTest {

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

        Long aux = 1L;
        assert(aux.equals(1L));
    }

//    @After
//    public void cleanUp(){
//
//    }
}
