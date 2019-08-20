package com.mihao.ancient_empire.email;

public class EmailHelper {


    public static String getRegisterTemp(String url) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n")
                .append("<body>\n")
                .append("   <h3>请激活您的登录邮箱</h3>\n")
                .append("   <span>　亲爱的用户，您好：</span><br>\n")
                .append("   <span>　请点击下方的链接激活您的登录邮箱：</span><br>\n")
                .append("   <span>　</span><a href = "+ url +">" + url + "</a><br>\n")
                .append("   <span>　（如链接点击无效，请手动复制链接并粘贴到浏览器地址栏中，然后按“回车”打开页面即可）</span><br>\n")
                .append("   <span>　如果这不是您本人操作的，请不要点击激活链接。</span><br>\n")
                .append("   <span>　这是一封系统自动发送的邮件，请不要直接回复，如您有任何疑问，请点击下面的按钮联系我们。</span><br>\n")
                .append("   <h2>　ancient-empire</h2><br>\n")
                .append("</body>\n")
                .append("</html>");
        return sb.toString();
    }
}
