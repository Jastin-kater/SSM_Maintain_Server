package com.kkenterprise.service.products.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.kkenterprise.dao.products.ProductsDao;
import com.kkenterprise.domain.beans.Ordersbean;
import com.kkenterprise.domain.beans.Productsbean;
import com.kkenterprise.service.products.IUserProductsActionService;
import com.kkenterprise.service.redis.impl.VerificationServiceImpl;
import com.kkenterprise.utils.AliDevPayConfig;
import com.kkenterprise.utils.MoneyCountUtil;
import com.kkenterprise.utils.OrderIdCreateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class UserProductsActionService implements IUserProductsActionService {

    @Autowired
    ProductsDao productsDao;
    @Autowired
    VerificationServiceImpl verificationService;
    private Logger log = LoggerFactory.getLogger(UserProductsActionService.class);
    @Override
    public String aliNotify(Map<String, String> conversionParams) throws AlipayApiException, ParseException {
        //签名验证(对支付宝返回的数据验证，确定是支付宝返回的)
        boolean signVerified = false;
        try {
            //调用SDK验证签名
            String alipayPublicKey = AliDevPayConfig.ALIPAY_PUBLIC_KEY;
            String charset = AliDevPayConfig.CHARSET;
            String signType = AliDevPayConfig.SIGNTYPE;

            signVerified = AlipaySignature.rsaCheckV1(conversionParams, alipayPublicKey, charset, signType);
            //对验签进行处理.
            if (signVerified) {
                System.out.println("+支付宝回调签名认证成功+");
                // 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure 支付宝官方建议校验的值（out_trade_no、total_amount、sellerId、app_id）
                //this.check(conversionParams);
                //验签通过 获取交易信息
                String appId=conversionParams.get("app_id");//支付宝分配给开发者的应用Id
                String notifyTime=conversionParams.get("notify_time");//通知时间:yyyy-MM-dd HH:mm:ss
                String gmtCreate=conversionParams.get("gmt_create");//交易创建时间:yyyy-MM-dd HH:mm:ss
                String gmtPayment=conversionParams.get("gmt_payment");//交易付款时间
                String gmtRefund=conversionParams.get("gmt_refund");//交易退款时间
                String gmtClose=conversionParams.get("gmt_close");//交易结束时间
                String tradeNo=conversionParams.get("trade_no");//支付宝的交易号
                String outTradeNo = conversionParams.get("out_trade_no");//获取商户之前传给支付宝的订单号（商户系统的唯一订单号）
                String outBizNo=conversionParams.get("out_biz_no");//商户业务号(商户业务ID，主要是退款通知中返回退款申请的流水号)
                String buyerLogonId=conversionParams.get("buyer_logon_id");//买家支付宝账号
                String sellerId=conversionParams.get("seller_id");//卖家支付宝用户号
                String sellerEmail=conversionParams.get("seller_email");//卖家支付宝账号
                String totalAmount=conversionParams.get("total_amount");//订单金额:本次交易支付的订单金额，单位为人民币（元）
                String receiptAmount=conversionParams.get("receipt_amount");//实收金额:商家在交易中实际收到的款项，单位为元
                String invoiceAmount=conversionParams.get("invoice_amount");//开票金额:用户在交易中支付的可开发票的金额
                String buyerPayAmount=conversionParams.get("buyer_pay_amount");//付款金额:用户在交易中支付的金额
                String tradeStatus = conversionParams.get("trade_status");// 获取交易状态


                Ordersbean order = productsDao.findOrderByoutTradeNo(outTradeNo);
                //只处理支付成功的订单: 修改交易表状态,支付成功
                //只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
                if (order!=null&&totalAmount.equals(order.getOrder_amount().toString())&&AliDevPayConfig.APPID.equals(appId)) {

                    /**修改订单信息*/
                    order.setOrder_num(outTradeNo);
                    order.setOrder_amount(totalAmount);
                    order.setPaid_amount(buyerPayAmount);
                    order.setCreate_time(timeExchange(gmtCreate));
                    order.setPaid_time(timeExchange(gmtPayment));
                    order.setNotifyTime(timeExchange(notifyTime));
                    order.setGmtRefund(timeExchange(gmtRefund));
                    order.setGmtClose(timeExchange(gmtClose));
                    order.setTradeNo(tradeNo);
                    order.setOutBizNo(outBizNo);
                    order.setBuyerLogonId(buyerLogonId);
                    order.setSellerEmail(sellerEmail);
                    order.setSellerId(sellerId);
                    order.setInvoiceAmount(invoiceAmount);
                    switch (tradeStatus) // 判断交易结果
                    {
                        case "TRADE_FINISHED": // 交易结束并不可退款
                            order.setOrder_status("3");
                            break;
                        case "TRADE_SUCCESS": // 交易支付成功
                            order.setOrder_status("2");
                            break;
                        case "TRADE_CLOSED": // 未付款交易超时关闭或支付完成后全额退款
                            order.setOrder_status("1");
                            break;
                        case "WAIT_BUYER_PAY": // 交易创建并等待买家付款
                            order.setOrder_status("0");
                            break;
                        default:
                            break;
                    }
                    productsDao.updateOrder(order);

                }
                return "success";
            } else {
                System.out.println("++验签不通过 ！++");
                return "fail";

            }

    } catch (AlipayApiException e) {
        System.out.println("++验签失败 ！++");
        e.printStackTrace();
    }
        return "fail";
}




    /**
     *
     * @param productId 产品Id
     * @return
     */
    public Productsbean findPriceById(String productId) {


        return productsDao.findPriceById(productId);
    }

    @Override
    public String createOrder(Productsbean productsbean, String num,String userid,double x,double y, String address,String remarks,String picture) {

        String orderString = null;//这个字符串是用来返回给前端的
        String orderNo = OrderIdCreateUtils.getOrderIdByTime();//这行代码是生成一个商户的订单号..根据你们自己的业务需求去生成就可以了..


        try {
            AliDevPayConfig aliDevPayConfig= new  AliDevPayConfig(); //实例化上面的那个配置类..
            //实例化客户端（参数：网关地址、商户appid、商户私钥、格式、编码、支付宝公钥、加密类型），为了取得预付订单信息
            AlipayClient alipayClient = new DefaultAlipayClient(AliDevPayConfig.URL, AliDevPayConfig.APPID,
                    AliDevPayConfig.RSA_PRIVATE_KEY, AliDevPayConfig.FORMAT, AliDevPayConfig.CHARSET,
                    AliDevPayConfig.ALIPAY_PUBLIC_KEY, AliDevPayConfig.SIGNTYPE);
            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
            AlipayTradeAppPayRequest ali_request = new AlipayTradeAppPayRequest();
            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

            //业务参数传入,可以传很多，参考API
            //model.setPassbackParams(URLEncoder.encode(request.getBody().toString())); //公用参数（附加数据）
            //对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
            model.setBody(productsbean.getDescribeInfo());
            //商品名称
            model.setSubject(productsbean.getName());
            //商户订单号(根据业务需求自己生成)
            model.setOutTradeNo(orderNo);
            //交易超时时间 这里的30m就是30分钟
            model.setTimeoutExpress("30m");
            //支付金额 后面保留2位小数点..不能超过2位
            model.setTotalAmount(MoneyCountUtil.mul(productsbean.getPrice(),num,2).toString());
            //销售产品码（固定值） //这个不做多解释..看文档api接口参数解释
            model.setProductCode("QUICK_MSECURITY_PAY");
            ali_request.setBizModel(model);
            //异步回调地址（后台）//这里我在上面的aliPayConfig有讲..自己去看
            ali_request.setNotifyUrl(AliDevPayConfig.notify_url);

            //同步回调地址（APP）同上
            ali_request.setReturnUrl(AliDevPayConfig.return_url);


            // 这里和普通的接口调用不同，使用的是sdkExecute
            //返回支付宝订单信息(预处理)
            AlipayTradeAppPayResponse alipayTradeAppPayResponse = alipayClient.sdkExecute(ali_request);
            //就是orderString 可以直接给APP请求，无需再做处理。
            orderString = alipayTradeAppPayResponse.getBody();
            System.out.println("orderString:"+orderString);
            //创建订单
            Ordersbean ordersbean = new Ordersbean();
            ordersbean.setOrder_num(orderNo);
            ordersbean.setBuy_counts(Integer.parseInt(num));
            ordersbean.setOrder_amount(MoneyCountUtil.mul(productsbean.getPrice(),num,2).toString());
            ordersbean.setOrder_status("0");
            ordersbean.setProduct_id(productsbean.getId()+"");
            ordersbean.setCreate_time(new Date());
            ordersbean.setUserId(Integer.parseInt(userid));
            ordersbean.setLongitude(x);
            ordersbean.setLatitude(y);
            ordersbean.setAddress(address);
            ordersbean.setRemarks(remarks);
            ordersbean.setPicture(picture);


            productsDao.createOrder(ordersbean);
            //订单加入订单池
            if(verificationService.addmember(orderNo,x,y) != 0) {
                log.info("Create Order", orderNo + "订单创建成功");
            }
            else
            {   log.info("Create Order", orderNo + "订单创建失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.error("与支付宝交互出错，未能生成订单，请检查代码！");
        }
        return orderString;

    }


    /**
     * 向支付宝查询订单状态
     * @param outTradeNo 订单号
     * @return
     */
    public String checkAlipay(String outTradeNo) {

        try {
            //实例化客户端（参数：网关地址、商户appid、商户私钥、格式、编码、支付宝公钥、加密类型）
            AlipayClient alipayClient = new DefaultAlipayClient(AliDevPayConfig.URL, AliDevPayConfig.APPID,
                    AliDevPayConfig.RSA_PRIVATE_KEY, AliDevPayConfig.FORMAT, AliDevPayConfig.CHARSET,
                    AliDevPayConfig.ALIPAY_PUBLIC_KEY, AliDevPayConfig.SIGNTYPE);
            AlipayTradeQueryRequest alipayTradeQueryRequest = new AlipayTradeQueryRequest();
            alipayTradeQueryRequest.setBizContent("{" +
                    "\"out_trade_no\":\""+outTradeNo+"\"" +
                    "}");
            AlipayTradeQueryResponse alipayTradeQueryResponse = alipayClient.execute(alipayTradeQueryRequest);
            if(alipayTradeQueryResponse.isSuccess()){

                Ordersbean ordersbean = productsDao.findOrderByoutTradeNo(outTradeNo);
                //修改数据库支付宝订单表
                ordersbean.setTradeNo(alipayTradeQueryResponse.getTradeNo());
                ordersbean.setBuyerLogonId(alipayTradeQueryResponse.getBuyerLogonId());
                ordersbean.setOrder_amount(alipayTradeQueryResponse.getTotalAmount());
                ordersbean.setPaid_amount(alipayTradeQueryResponse.getBuyerPayAmount());
                ordersbean.setInvoiceAmount(alipayTradeQueryResponse.getInvoiceAmount());

                switch (alipayTradeQueryResponse.getTradeStatus()) // 判断交易结果
                {
                    case "TRADE_FINISHED": // 交易结束并不可退款
                        ordersbean.setOrder_status("3");
                        break;
                    case "TRADE_SUCCESS": // 交易支付成功
                        ordersbean.setOrder_status("2");
                        break;
                    case "TRADE_CLOSED": // 未付款交易超时关闭或支付完成后全额退款
                        ordersbean.setOrder_status("1");
                        break;
                    case "WAIT_BUYER_PAY": // 交易创建并等待买家付款
                        ordersbean.setOrder_status("0");
                        break;
                    default:
                        break;
                }
                productsDao.updateOrder(ordersbean); //更新表记录
                return ordersbean.getOrder_status();
            } else {
                System.out.println("==================调用支付宝查询接口失败！");
            }
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "0";
    }





    public Date timeExchange(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(time);


    }
}
