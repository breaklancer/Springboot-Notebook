import com.xiaofu.JasyptApplication;
import com.xiaofu.annotation.EncryptField;
import com.xiaofu.controller.Encryptor;
import com.xiaofu.model.UserVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JasyptApplication.class})
public class EncryptTest {
    @Autowired
    private Encryptor encryptor;

    @Test
    public void encryptorTest(){
        UserVo vo = UserVo.builder()
                        .age("10")
                                .mobile("15021421524")
                                        .userId(1L)
                                                .address("wea")
                                                        .build();
        encryptor.testEncrypt(vo, "haha");
    }
}
