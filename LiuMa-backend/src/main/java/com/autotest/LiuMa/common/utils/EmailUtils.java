package com.autotest.LiuMa.common.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * @author bene
 * QQ 邮箱发送
 */
public class EmailUtils {
    /**
     * Receiver 收件人地址，title邮件主题，content邮件正文
     */
    private static final String SENDER = "leon.jack@qq.com";
    private static final String AUTH_CODE = "hgqaskhkxihsbhae";
    /**
     *   1、创建连接对象，连接到邮箱服务器
     */
    public static void sendMail(String receiver, String title, String content, String accessKey, String accessSecret,
                                String runnerSenderAddress, String runnerSenderName) {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName(runnerSenderAddress);
            request.setFromAlias(runnerSenderName);
            request.setAddressType(1);
            request.setReplyToAddress(false);
            request.setToAddress(receiver);
            request.setSubject(title);
            request.setHtmlBody(content);
            request.setMethod(MethodType.POST);

            client.getAcsResponse(request);
        } catch (ServerException e) {
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        }
    }

}