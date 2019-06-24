package finalproject.emag.controller;

import finalproject.emag.model.dto.PromotionProductDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PromotionControllerTest extends AbstractTest{

    @Test
    public void addPromotionSuccess() throws Exception{
        //TODO its not working fix
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

        String date = "16/08/2016";

        //convert String to LocalDate
        LocalDate localDate = LocalDate.parse(date, formatter);

        String date1 = "16/09/2016";

        //convert String to LocalDate
        LocalDate localDate1 = LocalDate.parse(date1, formatter);

        PromotionProductDTO promotion = new PromotionProductDTO();
        promotion.setStartDate(localDate);
        promotion.setEndDate(localDate1);
        promotion.setNewPrice(500.0);
        String jsonPromotion = mapToJson(promotion);
        System.out.println(jsonPromotion);
        mvc.perform(post("/products/promotions/{id}",2)
                .sessionAttr("user",getUserForSessionById(2))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPromotion)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Promotion added"));
    }

}
