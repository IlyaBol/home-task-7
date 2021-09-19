package qaguru;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import org.junit.jupiter.api.Test;
import net.lingala.zip4j.core.ZipFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipException;

import java.io.IOException;
import java.io.InputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFileTest {
    @Test
    void uploadFileTest() {
        open("https://the-internet.herokuapp.com/upload");
        $("input[type='file']").uploadFromClasspath("example.txt");
        $("#file-submit").click();
        $("#uploaded-files")
                .shouldHave(text("example.txt"));
    }

    @Test
    void txtFileTest() throws Exception {
        String result;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("example.txt")) {
            result = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertThat(result).contains("Hello");
    }

    @Test
    void parsePdfFile() throws Exception {
        PDF parsed = new PDF(getClass().getClassLoader().getResourceAsStream("pdf-test.pdf"));
        assertThat(parsed.text).contains("PDF Test File");
        assertThat(parsed.author).contains("Yukon Department of Education");
        assertThat(parsed.title).contains("PDF Test Page");
    }

    @Test
    void parseXlsFile() throws Exception {
        XLS parsed = new XLS(getClass().getClassLoader().getResourceAsStream("tests-example.xls"));
        assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue())
                .isEqualTo("What is 2+2?");
        assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(2).getStringCellValue())
                .isEqualTo("four");
        assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(3).getStringCellValue())
                .isEqualTo("correct");
    }

    @Test
    void readDocFile() throws Exception {
        try (InputStream fis = getClass().getClassLoader().getResourceAsStream("TestWord.docx")) {
            XWPFDocument document = new XWPFDocument(fis);

            List<XWPFParagraph> paragraphs = document.getParagraphs();

            for (XWPFParagraph para : paragraphs) {
                assertThat(para.getText()).contains("Hello WORLD!!!!!!!!!!!");
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void parseZipFile() throws IOException, ZipException {

        String source = "src/main/resources/Desktop.zip";
        String destination = "src/main/";
        String password = "1234";
        try {
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destination);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



