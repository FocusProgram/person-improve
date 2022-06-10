package com.example.quartz.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * @Auther: Mr.Kong
 * @Date: 2020/4/20 13:55
 * @Description: 定时邮件发送
 */
@RestController
@RequestMapping("sendEmail")
public class EmailController {

    private static String emailMsg = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>温馨小提示</title>\n" +
            "    　　\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
            "</head>\n" +
            "\n" +
            "<body style=\"margin: 0; padding: 0;\">\n" +
            "\n" +
            "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse: collapse;\">\n" +
            "\n" +
            "    <tr>\n" +
            "        <td>\n" +
            "            <div style=\"border: #36649d 1px dashed;margin: 30px;padding: 20px\">\n" +
            "                <label style=\"font-size: 22px;color: #36649d;font-weight: bold\">亲爱的宝宝：</label>\n" +
            "                <p style=\"font-size: 16px\"><label style=\"font-weight: bold\">ReminderContent</p>\n" +
            "            </div>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    \n" +
            "\t\n" +
            "    <tr>\n" +
            "        <td>\n" +
            "            <div align=\"right\" style=\"margin: 40px;border-top: solid 1px gray\" id=\"bottomTime\">\n" +
            "                <p style=\"margin-right: 20px\">来自爸爸的温馨小提示</p>\n" +
            "                <label style=\"margin-right: 20px\">sendData</label>\n" +
            "            </div>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "</table>\n" +
            "</body>\n" +
            "</html>\n";

    @RequestMapping("sendTwoDay")
    public boolean sendTwoDay() {
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
        String currentData = format.format(new Date());
        emailMsg = emailMsg.replace("sendData", currentData);
        emailMsg = emailMsg.replace("ReminderContent", "今天是宝宝敷普通面膜的日子，不要忘记了哦，洗完澡后记得敷哦，爱你ლ(°◕‵ƹ′◕ლ)");
        return send(emailMsg);
    }

    @RequestMapping("sendTenDay")
    public boolean sendTenDay() {
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
        String currentData = format.format(new Date());
        emailMsg = emailMsg.replace("sendData", currentData);
        emailMsg = emailMsg.replace("ReminderContent", "今天是宝宝敷清洁面膜的日子，不要忘记了哦，洗完澡后记得敷哦，爱你ლ(°◕‵ƹ′◕ლ)");
        return send(emailMsg);
    }

    @RequestMapping("sendMouth")
    public boolean sendMouth() {
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
        String currentData = format.format(new Date());
        emailMsg = emailMsg.replace("sendData", currentData);
        emailMsg = emailMsg.replace("ReminderContent", "宝宝大姨妈快来了，不要吃辣的、冷的、生的、鲜的，要注意休息，不要生气，生气对身体不好(*^▽^*)");
        return send(emailMsg);
    }

    @RequestMapping("sendPassword")
    public boolean sendPassword() {
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
        String currentData = format.format(new Date());
        emailMsg = emailMsg.replace("sendData", currentData);
        emailMsg = emailMsg.replace("ReminderContent", "记得给家长老板发密码哦，千万别忘了");
        return send(emailMsg);
    }

    @RequestMapping("sendSummary")
    public boolean sendSummary() {
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
        String currentData = format.format(new Date());
        emailMsg = emailMsg.replace("sendData", currentData);
        emailMsg = emailMsg.replace("ReminderContent", "记得给家长作业和总结，千万别忘了");
        return send(emailMsg);
    }

    private boolean send(String emailMsg) {

        // 谁发送(发送方)
        String from = "1031652818@qq.com";

        // 发给谁
        String to = "654787651@qq.com";

        // 发送者的用户名和密码(邮箱登录用)
        final String username = "1031652818@qq.com"; // 此处填写发送的邮箱名
        final String password = "jonsrvcewjlybdhh"; // 此处填写登录的邮箱密码

        // 定义properties对象，设置环境信息
        Properties properties = new Properties();

        /*
         * mail.smtp.host ：指定连接的邮件服务器的主机名。如：163邮箱就填写smtp.163.com
         * 若在本地测试的话，需要在本地安装smtp服务器
         */
        properties.setProperty("mail.smtp.host", "smtp.qq.com");

        // mail.smtp.auth：指定客户端是否要向邮件服务器提交验证
        properties.setProperty("mail.smtp.auth", "true");

        /*
         * mail.transport.protocol：指定邮件发送协议：smtp。smtp：发邮件；pop3：收邮件
         * mail.store.protocol:指定邮件接收协议
         */
        properties.setProperty("mail.transport.protocol", "smtp");

        // 获取session对象
        Session session = Session.getInstance(properties);

        // 当设置为true，JavaMail AP就会将其运行过程和邮件服务器的交互命令信息输出到console中，用于JavaMail的调试
        session.setDebug(true);
        try {

            // 创建邮件对象
            MimeMessage message = new MimeMessage(session);

            // 设置邮件发送方
            message.setFrom(new InternetAddress(from));

            // 设置邮件发送的主题<邮件标题>
            message.setSubject("温馨小提示");

            // 设置邮件发送的内容
            message.setContent(emailMsg, "text/html;charset=utf-8");
            Transport transport = session.getTransport();

            // 连接邮件服务器，“”中填写邮件服务器主机名
            transport.connect("smtp.qq.com", 587, username, password);
            transport.sendMessage(message, new Address[]{new InternetAddress(to)});
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }


}
