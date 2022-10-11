package sia.tacocloud.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "Ingredient_Ref")
public class IngredientRef {
    @Id
    private final String ingredient;

}