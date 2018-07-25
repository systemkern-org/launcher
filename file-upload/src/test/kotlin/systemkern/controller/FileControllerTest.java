package systemkern.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import systemkern.service.FileStorageService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {FileController.class})
@ContextConfiguration
@WebAppConfiguration
public class FileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FileStorageService fileStorageServiceMock;

    @Test
    public void testCreateClientSuccessfully() throws Exception {

        final String noSuffixInFile = null;

        File file = File.createTempFile("prueba", noSuffixInFile, new File(System.getProperty("java.io.tmpdir")));
        file.deleteOnExit();

        BufferedWriter bw = null;
        FileWriter fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        bw.write("fileContent");

        FileInputStream fi1 = new FileInputStream(file);

        final MockMultipartFile avatar = new MockMultipartFile("file", "test.dat", "application/octet-stream", fi1);

        given(fileStorageServiceMock.storeFile(avatar)).willReturn("Foo");


        mockMvc.perform(MockMvcRequestBuilders.multipart("/uploadFile")
                .file(avatar)
                .characterEncoding("UTF-8").with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", is("Foo")))
                .andReturn();
    }
}
