package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Service
@Transactional
public class ProductService {

    private ProductRepository productRepository;
    private PaymentServiceClients paymentServiceClients;

    @Autowired
    public ProductService(ProductRepository productRepository, PaymentServiceClients paymentServiceClients) {
        this.productRepository = productRepository;
        this.paymentServiceClients=paymentServiceClients;
    }

    public void productAdd(Product product) {
        productRepository.save(product);
    }

    public void productRemove(Long id) {
       productRepository.findById(id).ifPresent(pro->productRepository.delete(pro));
    }

    public void productEdit(Product product, Long id) {
       productRepository.findById(id).ifPresent(pro->{
            pro.setName(product.getName());
            pro.setDescription(product.getDescription());
            pro.setStock(product.getStock());
            pro.setPrice(product.getPrice());
            productRepository.save(pro);
        });
    }

    public List<Product> productList() {
        return productRepository.findAll();
    }

    public Product findByName(String name){
       return productRepository.findProductByName(name);
    }

    public synchronized List<Product> buyProduct(List<String> productName){

        Map<Product,Long> sellingProductMapInfo = new HashMap<>();
        List<Product> runOutOfProductInfo = new ArrayList<>();

        try{
            BigDecimal  resultCal =calculateProduct(productName, sellingProductMapInfo, runOutOfProductInfo);
            stockEdit(resultCal, sellingProductMapInfo);
        }catch (Exception ex){
            throw ex;
        }


        return runOutOfProductInfo; //stockta olmadigi icin satisi olmayan urunler
    }

    private void stockEdit(BigDecimal totalSum, Map<Product, Long> sellingProductMapInfo) {
        if(totalSum.longValue()>0){
            paymentServiceClients.call(totalSum);

            sellingProductMapInfo.entrySet().stream().forEach(product->{
                Long resultStock= product.getKey().getStock()-product.getValue();
                product.getKey().setStock(resultStock<0L?0L:resultStock);
                productEdit(product.getKey(),product.getKey().getId());
            });

            CompletableFuture.completedFuture("success");
        }
    }

    private BigDecimal calculateProduct(List<String> productName, Map<Product, Long> sellingProductMapInfo, List<Product> runOutOfProductInfo) {
        productName.stream().forEach(name->{
            Product product =findByName(name);
            if(sellingProductMapInfo.get(product)==null){

                sellingProductMapInfo.put(product,1L);

            }else if(sellingProductMapInfo.get(product)!=null){

                sellingProductMapInfo.put(product, sellingProductMapInfo.get(product)+1);
            }
        });

        List<BigDecimal> totalList = new ArrayList<>();

        sellingProductMapInfo.entrySet().stream().forEach(product->{
            if(product.getValue()<product.getKey().getStock()){
                BigDecimal  sum =  product.getKey().getPrice().multiply(BigDecimal.valueOf(product.getValue()));
                totalList.add(sum);
            }else{
                runOutOfProductInfo.add(product.getKey());
            }

        });

        BigDecimal totalSum = totalList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalSum;
    }


}
