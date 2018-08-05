package systemkern.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import systemkern.configuration.FileStorageProperties;
import systemkern.service.FileStorageService;

import java.io.*;
import java.util.Objects;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@AutoConfigureMockMvc(secure = false)
@RunWith(SpringRunner.class)
@WebMvcTest(FileController.class)
@ContextConfiguration(classes = {FileController.class, FileStorageService.class, FileStorageProperties.class})
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    @Test
    public void uploadFile() throws Exception {

        final String noSuffixInFile = ".tmp";

        File tempFile = File.createTempFile("tempfile", ".tmp");
        tempFile.deleteOnExit();

        writeInFile(tempFile, "This is temp file content.");

        FileInputStream fi1 = new FileInputStream(tempFile);

        final MockMultipartFile avatar = new MockMultipartFile("file", "test.dat", "application/octet-stream", fi1);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/uploadFile")
                .file(avatar)
                .with(httpBasic("user", "password")).with(csrf())
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", is(avatar.getOriginalFilename())));

        File convFile = new File(fileUploadDir.concat("/").concat(Objects.requireNonNull(avatar.getOriginalFilename())));

        deleteFile(convFile);
    }

    @Test
    public void connect() throws Exception {
        mockMvc.perform(get("/connect"))
                .andExpect(status().isOk());
    }

    @Test
    public void downloadFile() throws Exception {

        File file = new File(fileUploadDir.concat("/downloadFile.txt"));

        writeInFile(file, "This is download file content.");

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/downloadFile/"+file.getName()))
                .andExpect(status().isOk()).andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"downloadFile.txt\""));

        deleteFile(file);
    }

    private void writeInFile(File file, String content){
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(File fileToDelete){
        if (!fileToDelete.delete()){
            System.out.println("file has not been deleted");
        }
    }

//                .characterEncoding("UTF-8").with(SecurityMockMvcRequestPostProcessors.csrf()).header(HttpHeaders.AUTHORIZATION,
//                        "Basic 4a7dce31-b0c4-4887-8ddf-08d405f3836c" ))
//                .andExpect(jsonPath("$.fileName", is("Foo")))
}