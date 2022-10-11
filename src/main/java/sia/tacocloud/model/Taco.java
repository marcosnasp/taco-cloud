package sia.tacocloud.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "Taco")
// Exclude createdAt from equals() method so that tests won't fail trying to
// compare java.util.Date with java.sql.Timestamp (even though they're essentially
// equal). Need to figure out a better way than this, but excluding this property
// for now.
@EqualsAndHashCode(exclude = "createdAt")
public class Taco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date createdAt = new Date();

    @NotNull
    @Size(min = 5, message = "Name must be at least 5 characteres long")
    private String name;

    @NotNull
    @Size(min = 1, message = "You must choose at least 1 Ingredient")
    @ManyToMany
    private List<IngredientRef> ingredients = new ArrayList<>();


    public void addIngredient(Ingredient taco) {
        this.ingredients.add(new IngredientRef(taco.getId()));
    }


}
