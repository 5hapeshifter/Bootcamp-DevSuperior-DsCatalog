package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Precisamos utilizar o @MockBean pq estamos na camada web
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page; // Objeto concreto ao inves do Page que é uma interface
    private Long categoryId;

    @BeforeEach
    void setUp() throws Exception{
        // Como o interesse aqui é o comportamento simulado, não importa os valores em si, basta eles serem diferentes, porque não estamos testando o banco de dados.
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        categoryId = 1L;

        productDTO = Factory.createProductDto();
        page = new PageImpl<>(List.of(productDTO));

        // Comportamentos esperados simulados
        Mockito.when(service.findAllPaged(categoryId, any())).thenReturn(page);

        Mockito.when(service.findById(existingId)).thenReturn(productDTO);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(service.update(ArgumentMatchers.eq(existingId), any())).thenReturn(productDTO);
        Mockito.when(service.update(ArgumentMatchers.eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(service).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        Mockito.doThrow(DataBaseException.class).when(service).delete(dependentId);

        //TODO Questionar o pq não aceita o dto, mas o any sim
        Mockito.when(service.insert(any())).thenReturn(productDTO);

    }

    @Test
    public void insertShouldReturnProductDto() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de dados da requisição
                        .accept(MediaType.APPLICATION_JSON)); // Tipo de dados da resposta
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));
        // Status esperado
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteShouldThrowDataBaseExceptionWhenDependentIdExist() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", dependentId)
                        .accept(MediaType.APPLICATION_JSON));
        // Status esperado
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
        // Para poder fazer o teste estamos convertando o tipo do objeto da requisição de Java para Json
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON) // Tipo de dados da requisição
                        .accept(MediaType.APPLICATION_JSON)); // Tipo de dados da resposta
        // O '$' acessa o atributo no Json, nesse caso estamos verificando se o id, name, description existem
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        // Para poder fazer o teste estamos convertando o tipo do objeto da resposta de Json para Java
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // Status esperado
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .accept(MediaType.APPLICATION_JSON));
        // Status esperado
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));
        // Status esperado
        result.andExpect(MockMvcResultMatchers.status().isOk());

        // O '$' acessa o atributo no Json, nesse caso estamos verificando se o id, name, description existem
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    /*
     O retorno deve ser o status do método da camada web, not found nesse caso
     */
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));
        // Status esperado
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
