package pojo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class IngredientsResponsePOJO {
    private boolean success;
    private List<IngredientPOJO> data;
}
