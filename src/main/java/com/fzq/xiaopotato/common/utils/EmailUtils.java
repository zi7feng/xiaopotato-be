package com.fzq.xiaopotato.common.utils;


import com.fzq.xiaopotato.model.dto.email.EmailSendDTO;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;

public class EmailUtils {


    /**
     * default IP address
     */
    public final static String EMAIL_RESEND_API_KEY = "re_2xAW67Au_4qJSt6mpDwsDvZBsRxmBPxTm";

    /**
     * demo: https://resend.com/docs/send-with-java
     * @param emailSendDTO
     * @return
     */
    public static String sendEmail(EmailSendDTO emailSendDTO) {
        Resend resend = new Resend(EMAIL_RESEND_API_KEY);
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(emailSendDTO.getFromUser())
                .to(emailSendDTO.getToUser())
                .subject(emailSendDTO.getSubject())
                .html(emailSendDTO.getContent())
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            return data.getId();
        } catch (ResendException e) {
            e.printStackTrace();
        }
        return "";
    }

    @PostConstruct
    public void emailSend() {
        Resend resend = new Resend("re_2xAW67Au_4qJSt6mpDwsDvZBsRxmBPxTm");
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("official <support@xiaopotato.top>")
                .to("mengyaozhang888@gmail.com", "zfeng8678@conestogac.on.ca")
                //.to("ekafi.i1111@gmail.com", "lchen5274@conestogac.on.ca")
                .subject("Welcome to xiao potato art platform world and enjoy it!")
                .html("<strong>Hey， Potatoers：</strong><h2>XiaoPotato will makes your dream come true!</div>")
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
    }

}