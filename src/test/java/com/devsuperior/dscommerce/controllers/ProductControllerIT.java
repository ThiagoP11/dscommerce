package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //Sobe o contexto completo da aplicação Carrega:@Controller, @Service, @Repository, Configurações etc...
@AutoConfigureMockMvc// Essa anotação configura automaticamente o MockMvc para você usar nos testes.Ela é usada junto com @SpringBootTest.
@Transactional//Cada teste roda dentro de uma transação e no final é feito rollback automático.
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String clientUsername;
    private String clientPassword;
    private String adminUsername;
    private String adminPassword;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Product productName;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 2L;
        nonExistingId = 100L;
        dependentId = 3L;

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";


        productName = ProductFactory.createProduct("MacBook");

    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/products?name={productName}", productName.getName())
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(3L));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].price").value(1250.0));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].imgUrl")
                .value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));

    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsEmpty() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/products")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(1L));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("The Lord of the Rings"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].price").value(90.5));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].imgUrl")
                .value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));

    }

    @Test
    public void insertShouldReturnProductDTOCreatedWhenAdminLogged() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        ProductDTO productDTO = ProductFactory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.
                post("/products")
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)//corpo da requisição
                .contentType(MediaType.APPLICATION_JSON)//informa que o body é JSON
                .accept(MediaType.APPLICATION_JSON))//diz que quer a resposta em JSON
                .andDo(MockMvcResultHandlers.print());


        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").value("Console Playstation 5"));
        result.andExpect(jsonPath("$.description").value("Muito legal, pois funciona GTA 6"));
        result.andExpect(jsonPath("$.price").value(3999.0));
        result.andExpect(jsonPath("$.imgUrl").value("http://image.com"));
        result.andExpect(jsonPath("$.categories[0].id").value(1L));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedInvalidName() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        ProductDTO productDTO = ProductFactory.createProductDTOComCategoria("me", "muito legal esse gta 6", 600.0);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.
                post("/products")
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.errors[0].fieldName").value("Nome precisa ter de 3 a 80 caracteres"));
        result.andExpect(jsonPath("$.errors[0].message").value("name"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedInvalidDescription() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        ProductDTO productDTO = ProductFactory.createProductDTOComCategoria("GTA 6", "legal", 600.0);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.
                post("/products")
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.errors[0].fieldName").value("Descrição precisa ter no mínimo 10 caracteres"));
        result.andExpect(jsonPath("$.errors[0].message").value("description"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsNegative() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        ProductDTO productDTO = ProductFactory.createProductDTOComCategoria("GTA 6", "Muito legal esse jogo", -100.0);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.
                post("/products")
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.errors[0].fieldName").value("O preço deve ser positivo"));
        result.andExpect(jsonPath("$.errors[0].message").value("price"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsZero() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        ProductDTO productDTO = ProductFactory.createProductDTOComCategoria("GTA 6", "Muito legal esse jogo", 0.0);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.
                post("/products")
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.errors[0].fieldName").value("O preço deve ser positivo"));
        result.andExpect(jsonPath("$.errors[0].message").value("price"));

    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndProductHasNullCategory() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        ProductDTO productDTO = ProductFactory.createProductDTO("GTA 6", "Muito legal esse jogo", 100.0);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.
                post("/products")
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.errors[0].fieldName").value("Deve ter pelos menos uma categoria"));
        result.andExpect(jsonPath("$.errors[0].message").value("categories"));
    }

    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);

        ProductDTO productDTO = ProductFactory.createProductDTOComCategoria("GTA 6", "Muito legal esse jogo", 100.0);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.
                post("/products")
                .header("Authorization", "Bearer " + accessToken)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

        ProductDTO productDTO = ProductFactory.createProductDTOComCategoria("GTA 6",
                "Muito legal esse jogo", 100.0);

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.
                post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdExistsAndAdminLogged() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isNotFound());
    }

    @Test
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteShouldReturnBadRequestWhenIdExistsAndAdminLogged() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", dependentId)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isBadRequest());
    }

    @Test
    public void deleteShouldReturnForbiddenWhenIdExistsAndClientLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);


        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken));

        result.andExpect(status().isForbidden());

    }

    @Test
    public void deleteShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws Exception {

        Long id = 5L;

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", id));

        result.andExpect(status().isUnauthorized());
    }

}
