import com.google.gson.Gson;
import com.kkenterprise.dao.account.UserAccountDao;
import com.kkenterprise.dao.account.WorkerAccountDao;
import com.kkenterprise.domain.beans.Userbean;
import com.kkenterprise.domain.beans.Workerbean;
import com.kkenterprise.utils.HttpUtils;
import com.kkenterprise.utils.RSAUtils;
import com.kkenterprise.domain.beans.TandPublicKeyBean;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import static com.kkenterprise.utils.RSAUtils.getPrivateKey;
import static com.kkenterprise.utils.RSAUtils.getPublicKey;

public class FunctionTest {
    /**
     * 完成功能测试
     */
    @Test
    public void rsaTest() throws Exception {

        String s = "teasdfasfdadfsasdgfasdfadsf5415748102sdfhhAFGABSDY71Y4A534Y1T5HS4J4TGUR5Y5A37T4V51SYR54AE51EYWE5rbasgqqbdrbvassbvgdhst";
        //RSAUtils.generateKeyPair();
//        Gson gson = new Gson();
//        TandPublicKeyBean tandPublicKeyBean = new TandPublicKeyBean(RSAUtils.bytesToHexString(getPublicKey().getEncoded()),String.valueOf(System.currentTimeMillis()));
//        System.out.println("json字符串："+gson.toJson(tandPublicKeyBean));
//        System.out.println("解析后的对象值："+gson.fromJson(gson.toJson(tandPublicKeyBean),TandPublicKeyBean.class));
//        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(RSAUtils.hexStringToBytes(gson.fromJson(gson.toJson(tandPublicKeyBean),TandPublicKeyBean.class).getPublickey()));
//        String c1 = RSAUtils.encryptByPublic(s.getBytes(),(RSAPublicKey)RSAUtils.getKeyFactory().generatePublic(x509EncodedKeySpec));
//        String m1 = RSAUtils.decryptByPrivate(c1,null);

        System.out.println("sign:"+RSAUtils.sign(s,getPrivateKey()));
        System.out.println("unsign:"+RSAUtils.verify(s,RSAUtils.getPublicKey(),RSAUtils.sign(s,getPrivateKey())));

//        System.out.println("密文："+c1);
//        System.out.println("明文："+m1);

    }
    @Test
    public void testUsersaveDb() throws Exception {
        Userbean userbean = new Userbean();
        userbean.setUsername("丽k");
        userbean.setPhone("17854222103");
        InputStream inputStream;
        SqlSession session;
        inputStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        session = factory.openSession();
        UserAccountDao userAccountDao = session.getMapper(UserAccountDao.class);
//        userAccountDao.saveUserInfo(userbean);
        Userbean user = userAccountDao.findUserByPhone("17854222104");
        System.out.println(user);
        //提交事务
        session.commit();
        session.close();
        inputStream.close();

    }
    @Test
    public void testWorkersaveDb() throws Exception {
        Workerbean workerbean = new Workerbean();
        workerbean.setWorkername("小明");
        workerbean.setPhone("17854222503");
        InputStream inputStream;
        SqlSession session;
        inputStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        session = factory.openSession();
        WorkerAccountDao workerAccountDao = session.getMapper(WorkerAccountDao.class);
        //workerAccountDao.saveWorkerInfo(workerbean);
       Workerbean worker = workerAccountDao.findWorkerByPhone("17854222503");
        System.out.println(worker);
        //提交事务
        session.commit();
        session.close();
        inputStream.close();

    }
    @Test
    public void testIDcard() throws IOException {
        System.out.println("结果："+ HttpUtils.checkResult(HttpUtils.postForm(HttpUtils.appCode,HttpUtils.url,"510184199510263171","任重阳")));



    }

    @Test
    public void test() throws IOException {
        String st = "test";
        System.out.println("{\"code\":\"0\",\"data\":\""+st+"\"}");


    }
}
