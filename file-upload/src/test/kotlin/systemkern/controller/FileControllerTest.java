package systemkern.controller;

import org.junit.Assert;
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
import systemkern.configuration.FileStorageProperties;
import systemkern.service.FileStorageService;

import java.io.*;
import java.util.Objects;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FileController.class)
@ContextConfiguration(classes = {FileController.class, FileStorageService.class, FileStorageProperties.class})
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    private final static String noSuffixInFile = ".tmp";

    @Test
    public void uploadFile() throws Exception {
        File tempFile = File.createTempFile("tempfile", noSuffixInFile);
        tempFile.deleteOnExit();

        writeInFile(tempFile, "This is temp file content.");

        FileInputStream fi1 = new FileInputStream(tempFile);

        final MockMultipartFile avatar = new MockMultipartFile("file", "test.dat", "application/octet-stream", fi1);

        ResultActions resultActions = mockMvc.perform(multipart("/uploadFile")
                .file(avatar)
                .with(httpBasic("user", "password")).with(csrf())
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", is(avatar.getOriginalFilename())));

        Assert.assertNotNull(resultActions.toString());

        File convFile = new File(fileUploadDir.concat("/").concat(Objects.requireNonNull(avatar.getOriginalFilename())));

        deleteFile(convFile);
    }

    @Test
    public void connect() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/connect"))
                .andExpect(status().isOk());

        Assert.assertNotNull(resultActions.toString());
    }

    @Test
    public void downloadFile() throws Exception {
        File tempFile = File.createTempFile(fileUploadDir.concat("/downloadFile.txt"), noSuffixInFile);
        tempFile.deleteOnExit();

        File file = new File(fileUploadDir.concat("/downloadFile.txt"));

        writeInFile(file, "This is download file content.");

        ResultActions result = mockMvc.perform(get("/downloadFile/"+file.getName()))
                .andExpect(status().isOk()).andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"downloadFile.txt\""));

        Assert.assertNotNull(result.toString());

        deleteFile(file);
    }

    private void writeInFile(File file, String content){
        BufferedWriter bw;
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

}