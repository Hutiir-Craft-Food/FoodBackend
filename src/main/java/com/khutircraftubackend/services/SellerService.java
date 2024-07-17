package com.khutircraftubackend.services;

import com.khutircraftubackend.dto.SellerDTO;
import com.khutircraftubackend.mapper.SellerMapper;
import com.khutircraftubackend.models.SellerEntity;
import com.khutircraftubackend.repositories.SellerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${spring.mail.name")
    private String mailSellerName;

    @Transactional
    public void registerSeller(SellerDTO sellerDTO) {
        String confirmationCode = UUID.randomUUID().toString();
        SellerEntity seller = SellerMapper.INSTANCE.toSellerEntity(sellerDTO);

        seller.setPassword(passwordEncoder.encode(sellerDTO.password()));
        seller.setEnabled(false);
        seller.setConfirmationCode(confirmationCode);

        sellerRepository.save(seller);

        sendConfirmationEmail(seller.getEmail(), confirmationCode);
    }
    @Async
    public void sendConfirmationEmail(String email, String confirmationCode) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setFrom(mailSellerName);
        mailMessage.setSubject("Підтвердження облікового запису");
        mailMessage.setText("Для підтвердження вашого облікового запису перейдіть за посиланням: "
                + "http://ocalhost:8080/v1/seller/register/confirm?code=" + confirmationCode);

        mailSender.send(mailMessage);

    }

    @Transactional
    public boolean confirmSeller(String confirmationCode) {
        SellerEntity seller = sellerRepository.findByConfirmationCode(confirmationCode);
        if(seller == null) {
            return false; //Код підтвердження не знайдено
        }
        seller.setConfirmationCode(null);
        seller.setEnabled(true);
        sellerRepository.save(seller);
        return true;
    }

    @Transactional
    public SellerDTO getSellerConfirmation(String email) {
        SellerEntity seller = sellerRepository.findByEmail(email);
        return SellerMapper.INSTANCE.toSellerDTO(seller);
    }
}
