package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.repository.PaymentRepository;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
//@Transactional
public class IyzicoPaymentService {

    private Logger logger = LoggerFactory.getLogger(IyzicoPaymentService.class);

    private BankService bankService;
    private PaymentRepository paymentRepository;

    public IyzicoPaymentService(BankService bankService, PaymentRepository paymentRepository) {
        this.bankService = bankService;
        this.paymentRepository = paymentRepository;
    }

    public synchronized void pay(BigDecimal price) {
        //pay with bank
        try {
            BankPaymentRequest request = new BankPaymentRequest();
            request.setPrice(price);
            Optional<BankPaymentResponse> response = Optional.ofNullable(bankService.pay(request));
            //insert records
            response.ifPresent(a->{
                if(a.getResultCode().equals("200")){
                    paymentInfoSave(price);
                }

            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void paymentInfoSave(BigDecimal price){

        Payment payment = new Payment();
        payment.setBankResponse("100");
        payment.setPrice(price);
        try{
            paymentRepository.saveAndFlush(payment);
            logger.info("Payment saved successfully!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}
