package com.kkenterprise.controller;


import com.google.gson.*;
import com.kkenterprise.domain.beans.PhoneAndVcodebean;
import com.kkenterprise.domain.beans.PhoneNumberbean;
import com.kkenterprise.domain.beans.TAResultbean;
import com.kkenterprise.domain.beans.Userbean;
import com.kkenterprise.service.account.impl.UserAccountService;
import com.kkenterprise.service.redis.impl.VerificationServiceImpl;
import com.kkenterprise.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Locale;

/**
 * 定义了用户行为
 *
 */
@Controller
@RequestMapping(path = "/useraction")
public class UserActionController {
    @Autowired
    private VerificationServiceImpl verificationService;
    @Autowired
    private UserAccountService userAccountService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserActionController.class);
    private Gson gson = new Gson();


    /**
     * 测试变量
     */
    private String vcode ;

    /**
     * 获取登陆验证码
     * 返回code
     * -1 表示推送短信失败
     * 1 表示成功
     *
     */

    @RequestMapping(path = "/userGetCode")
    public void userGetCode(@RequestBody String phoneNum,HttpServletResponse response) throws Exception
    {

            //从前端获取手机号
            String phonestring  = gson.fromJson(RSAUtils.decryptByPrivate(phoneNum, RSAUtils.getPrivateKey()), PhoneNumberbean.class).getPhoneNum();
//            String phonestring  = gson.fromJson(phoneNum, PhoneNumberbean.class).getPhoneNum();
            System.out.println("手机号："+phonestring);
            //todo 验证手机号是否正确

            //生成验证码
            String verificationCode = VerificationCodeUtils.generateVcode();

            vcode = verificationCode;
            //进行短信推送
            if(SmsUtils.SendSmsResponse(phonestring,"{\"code\":\""+ verificationCode + "\"}")) {

                //短信推送成功后将号码加入redis  调用服务层
                verificationService.addVcodeToRedis(phonestring,verificationCode);
                response.getOutputStream().write("{\"code\":\"1\",\"data\":\"ok\"}".getBytes("utf-8"));
                return;
            }
            response.getOutputStream().write("{\"code\":\"-1\",\"data\":\"service deny\"}".getBytes("utf-8"));
    }



    /**
     * 用户手机号登陆  并且上传ASE密钥、验证码、手机号以及向量
     * 返回code
     * 1 表示成功注册或者已经注册后成功登陆，会返回数据
     * -1 表示验证码过期
     * 0 表示验证码错误
     *
     *
     */
    @RequestMapping(path = "/userLoginByPhoneNum")
    public void userLoginByPhoneNum(@RequestBody String p_c_iv_aeskey,HttpServletResponse response) {

        try {
            String results = "authentication ok";
            Gson gson = new Gson();
            //解析出手机号和验证码
            RSAUtils.setFileName(RSAUtils.DATAKEY);
            String AESkey = gson.fromJson(RSAUtils.decryptByPrivate(p_c_iv_aeskey, RSAUtils.getPrivateKey()), PhoneAndVcodebean.class).getAESkey();
            String vcode = gson.fromJson(RSAUtils.decryptByPrivate(p_c_iv_aeskey, RSAUtils.getPrivateKey()), PhoneAndVcodebean.class).getVcode();
            String phonenum = gson.fromJson(RSAUtils.decryptByPrivate(p_c_iv_aeskey, RSAUtils.getPrivateKey()), PhoneAndVcodebean.class).getPhoneNum();
            String iv = gson.fromJson(RSAUtils.decryptByPrivate(p_c_iv_aeskey, RSAUtils.getPrivateKey()), PhoneAndVcodebean.class).getIv();

            //验证验证码是否过期  以及是否正确
            if(verificationService.hasKey(phonenum))
            {
                if (verificationService.getKeyValue(phonenum).equals(vcode)) {
                    //验证成功 查询数据库 查询手机号用户   存在则更改AES密匙 向量   不存在则创建
                    if(userAccountService.findUserByPhone(phonenum) == null)
                    {//数据库没有对应用户
                        userAccountService.saveUserInfo(new Userbean(phonenum,"U"+phonenum));
                        results = "authentication no";
                        System.out.println("数据库没有对应用户");
                    }else{
                        if(!userAccountService.findUserByPhone(phonenum).isAuthentication())
                        {
                            results = "authentication no";
                            System.out.println("数据库有对应用户，但未认证");
                        }
                    }
                    //获取用户id
                    int id = userAccountService.findUserByPhone(phonenum).getUserId();
                    //生成token
                    String token = TokenUtils.getToken(phonenum);
                    //将token等参数放入redis 设置3天过期
                    verificationService.hashSet(id+"",token,AESkey,iv,phonenum);
                    //切换钥匙对
                    RSAUtils.setFileName(RSAUtils.TOKENKEY);
                    //加签后封装为对象以Json数据格式返回
                    TAResultbean taResultbean = new TAResultbean();

                    if("authentication no".equals(results))
                    {
                        taResultbean.setAuthentication(false);
                    }
                    else if("authentication ok".equals(results))
                    {
                        taResultbean.setAuthentication(true);
                    }
                    //设置用户ID
                    taResultbean.setId(id);
                    //设置令牌
                    taResultbean.setToken(RSAUtils.sign(token,RSAUtils.getPrivateKey()));

//                    System.out.println("返回的加密Token字符串："+gson.toJson(tokenandAuthenticationbean,TokenandAuthenticationbean.class));
//                    //用AES加密后返回给用户
//                    System.out.println("加密Token字符串："+AesUtils.encrypt(gson.toJson(tokenandAuthenticationbean,TokenandAuthenticationbean.class),Base64.getDecoder().decode(AESkey),Base64.getDecoder().decode(iv)));
//                    System.out.println("解密Token字符串："+AesUtils.decrypt(AesUtils.encrypt(gson.toJson(tokenandAuthenticationbean,TokenandAuthenticationbean.class),Base64.getDecoder().decode(AESkey),Base64.getDecoder().decode(iv)),Base64.getDecoder().decode(AESkey),Base64.getDecoder().decode(iv)));
                   //返回数据
                    response.getOutputStream().write(("{\"code\":\"1\",\"data\":\""+AesUtils.encrypt(gson.toJson(taResultbean,TAResultbean.class),Base64.getDecoder().decode(AESkey),Base64.getDecoder().decode(iv))+"\"}").getBytes());
                return;
                }
                //验证码错误
                response.getOutputStream().write("{\"code\":\"0\",\"data\":\"\"}".getBytes("utf-8"));
                return;
            }
            //验证码过期
            response.getOutputStream().write("{\"code\":\"-1\",\"data\":\"\"}".getBytes("utf-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 返回
     * checkresult ok 表示通过
     * checkresult no 表示未通过
     *
     * @param auth_info
     * @param response
     * @throws IOException
     */
    @RequestMapping(path = "/userAuthentication")
    public void userAuthentication(@RequestBody String auth_info,HttpServletResponse response) throws IOException {

        JsonObject jsonObject = jsonParse(auth_info);
        String id = jsonObject.get("id").getAsString();
        String data = jsonObject.get("data").getAsString();
        //获取密匙和iv向量
        String aeskey = verificationService.hashGet(id,"AESkey");
        String iv = verificationService.hashGet(id,"Iv");
        //解密数据
        data = AesUtils.decrypt(data,Base64.getDecoder().decode(aeskey),Base64.getDecoder().decode(iv));
        //Token验证
//TODO:
        //获取姓名身份证号
        JsonObject obj = jsonParse(data);

        //验证实名信息
        if(HttpUtils.checkResult(HttpUtils.postForm(HttpUtils.appCode,HttpUtils.url,obj.get("idcard").getAsString(),obj.get("name").getAsString())))
        {
           response.getOutputStream().write("{\"checkresult\":\"ok\"}".getBytes("utf-8"));

        }
        else {
            response.getOutputStream().write("{\"checkresult\":\"no\"}".getBytes("utf-8"));

        }

    }


    /**
     * 用户账号密码登陆
     */
    @RequestMapping(path = "/userLoginBynamepw")
    public void userLoginBynamepw(){

    }


    /**
     * 用户账号密码注册
     */
    @RequestMapping(path = "/userRegisterBynamepw")
    public void userRegisterBynamepw() throws Exception{


    }


    @RequestMapping(path = "/test")
    public void usermethodTest() {

        try {
            //System.out.println("结果："+userAccountService.findUserByPhone("17854222103"));
//            Userbean user = new Userbean();
//            user.setUsername("杨洋");
//            user.setPhone("13989556214");
//            userAccountService.saveUserInfo(user);
            String AESkey = "asds";
            String iv = "asdf";

            //短信验证码
            this.userGetCode("{\"phoneNum\":\"17854222103\"}", new HttpServletResponse() {
                @Override
                public void addCookie(Cookie cookie) {

                }

                @Override
                public boolean containsHeader(String s) {
                    return false;
                }

                @Override
                public String encodeURL(String s) {
                    return null;
                }

                @Override
                public String encodeRedirectURL(String s) {
                    return null;
                }

                @Override
                public String encodeUrl(String s) {
                    return null;
                }

                @Override
                public String encodeRedirectUrl(String s) {
                    return null;
                }

                @Override
                public void sendError(int i, String s) throws IOException {

                }

                @Override
                public void sendError(int i) throws IOException {

                }

                @Override
                public void sendRedirect(String s) throws IOException {

                }

                @Override
                public void setDateHeader(String s, long l) {

                }

                @Override
                public void addDateHeader(String s, long l) {

                }

                @Override
                public void setHeader(String s, String s1) {

                }

                @Override
                public void addHeader(String s, String s1) {

                }

                @Override
                public void setIntHeader(String s, int i) {

                }

                @Override
                public void addIntHeader(String s, int i) {

                }

                @Override
                public void setStatus(int i) {

                }

                @Override
                public void setStatus(int i, String s) {

                }

                @Override
                public String getCharacterEncoding() {
                    return null;
                }

                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                public ServletOutputStream getOutputStream() throws IOException {
                    return null;
                }

                @Override
                public PrintWriter getWriter() throws IOException {
                    return null;
                }

                @Override
                public void setCharacterEncoding(String s) {

                }

                @Override
                public void setContentLength(int i) {

                }

                @Override
                public void setContentType(String s) {

                }

                @Override
                public void setBufferSize(int i) {

                }

                @Override
                public int getBufferSize() {
                    return 0;
                }

                @Override
                public void flushBuffer() throws IOException {

                }

                @Override
                public void resetBuffer() {

                }

                @Override
                public boolean isCommitted() {
                    return false;
                }

                @Override
                public void reset() {

                }

                @Override
                public void setLocale(Locale locale) {

                }

                @Override
                public Locale getLocale() {
                    return null;
                }
            });
            PhoneAndVcodebean phoneAndVcodebean = new PhoneAndVcodebean();
            phoneAndVcodebean.setAESkey(AESkey);
            phoneAndVcodebean.setIv(iv);
            phoneAndVcodebean.setPhoneNum("17854222103");
            phoneAndVcodebean.setVcode(vcode);
            RSAUtils.setFileName(RSAUtils.DATAKEY);
            Gson gson = new Gson();
            System.out.println("phoneAndVcodebean:"+gson.toJson(phoneAndVcodebean,PhoneAndVcodebean.class));

            this.userLoginByPhoneNum(RSAUtils.encryptByPublic(gson.toJson(phoneAndVcodebean, PhoneAndVcodebean.class).getBytes(), RSAUtils.getPublicKey()), new HttpServletResponse() {
                @Override
                public void addCookie(Cookie cookie) {

                }

                @Override
                public boolean containsHeader(String s) {
                    return false;
                }

                @Override
                public String encodeURL(String s) {
                    return null;
                }

                @Override
                public String encodeRedirectURL(String s) {
                    return null;
                }

                @Override
                public String encodeUrl(String s) {
                    return null;
                }

                @Override
                public String encodeRedirectUrl(String s) {
                    return null;
                }

                @Override
                public void sendError(int i, String s) throws IOException {

                }

                @Override
                public void sendError(int i) throws IOException {

                }

                @Override
                public void sendRedirect(String s) throws IOException {

                }

                @Override
                public void setDateHeader(String s, long l) {

                }

                @Override
                public void addDateHeader(String s, long l) {

                }

                @Override
                public void setHeader(String s, String s1) {

                }

                @Override
                public void addHeader(String s, String s1) {

                }

                @Override
                public void setIntHeader(String s, int i) {

                }

                @Override
                public void addIntHeader(String s, int i) {

                }

                @Override
                public void setStatus(int i) {

                }

                @Override
                public void setStatus(int i, String s) {

                }

                @Override
                public String getCharacterEncoding() {
                    return null;
                }

                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                public ServletOutputStream getOutputStream() throws IOException {
                    return null;
                }

                @Override
                public PrintWriter getWriter() throws IOException {
                    return null;
                }

                @Override
                public void setCharacterEncoding(String s) {

                }

                @Override
                public void setContentLength(int i) {

                }

                @Override
                public void setContentType(String s) {

                }

                @Override
                public void setBufferSize(int i) {

                }

                @Override
                public int getBufferSize() {
                    return 0;
                }

                @Override
                public void flushBuffer() throws IOException {

                }

                @Override
                public void resetBuffer() {

                }

                @Override
                public boolean isCommitted() {
                    return false;
                }

                @Override
                public void reset() {

                }

                @Override
                public void setLocale(Locale locale) {

                }

                @Override
                public Locale getLocale() {
                    return null;
                }
            });
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequestMapping(path = "/authencatitiontest")
    public void usermethodauthencatitiontest()
    {


    }

    public static JsonObject jsonParse(String data)
    {
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(data);
        return element.getAsJsonObject();

    }

}
