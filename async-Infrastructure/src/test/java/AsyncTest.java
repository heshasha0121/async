import com.lvchuan.common.aysnc.AsyncProxyUtil;
import com.lvchuan.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @description: 异步测试
 * @author: lvchuan
 * @createTime: 2024-03-06 15:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class AsyncTest {
    @Resource
    private AsyncProxyUtil asyncProxyUtil;
    @Resource
    private TestService testService;

    @Test
    public void testAsync() {
        asyncProxyUtil.proxy(testService).async(122L);
    }
}
