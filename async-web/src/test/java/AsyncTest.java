import com.lvchuan.WebMain;
import com.lvchuan.common.aysnc.AsyncProxyUtil;
import com.lvchuan.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @description: 异步测试
 * @author: lvchuan
 * @createTime: 2024-03-06 15:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebMain.class)
public class AsyncTest {
    @Autowired
    private AsyncProxyUtil asyncProxyUtil;
    @Autowired
    private TestService testService;

    @Test
    public void testAsync() {
        asyncProxyUtil.proxy(testService).async(122L);
    }
}
