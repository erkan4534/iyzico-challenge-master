package com.iyzico.challenge.service;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.entity.Product;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;;import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class IyzicoProductTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void addProduct() throws Exception {
        ResultActions productAdd = mockMvc.perform(post("/api/product/add")
                .content("{ \"name\":\"banana\", \"description\":\"fruit\", \"stock\": 3 ,\"price\":24}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void deleteProduct() {
        try {
            ResultActions productDelete = mockMvc.perform(delete("/api/product/delete/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void editProduct() {
        try {
            ResultActions productUpdate = mockMvc.perform(put("/api/product/edit/1")
                    .content("{ \"name\":\"apple\", \"description\":\"fruit\", \"stock\": 3 ,\"price\":4}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listOfProduct() {
        try {
            ResultActions productUpdate = mockMvc.perform(get("/api/product/getAll")
                    .content("{ \"name\":\"banana\", \"description\":\"fruit\", \"stock\": 3 ,\"price\":24}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void buyProduct() throws Exception {

        List<String> productName = new ArrayList<>();
        productName.add("banana");

        String jsonData =mapper.writeValueAsString(productName);

        ResultActions buyProduct = mockMvc.perform(post("/api/product/buyProduct")
                .content(jsonData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

}
