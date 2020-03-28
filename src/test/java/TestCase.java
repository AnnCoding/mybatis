import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

/**
 * @author chenjiena
 * @version 1.0
 * @created 2020/3/28.
 */
public class TestCase {
    private SqlSession session;
    @Before
    public void init() throws IOException {
        SqlSessionFactoryBuilder builder=new SqlSessionFactoryBuilder();
        InputStream is= Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory factory=builder.build(is);
        session=factory.openSession();
    }
    @Test
    public void test1() {
        Connection conn =session.getConnection();
        System.out.println(conn);
        //		User u=new User();
        //		u.setUser_name("zwk");
        //		u.setPwd("123");
        //		u.setPhone("1234567899");
        //		u.setEmail("145551556@151.com");
        //		u.setTime(new Timestamp(System.currentTimeMillis()));
        //		session.insert("test.save",u);
        /**
         * 手动提交事务
         */
        //		session.commit();
        //		session.close();
    }
}
