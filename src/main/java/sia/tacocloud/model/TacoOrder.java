package sia.tacocloud.model;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Table
public class TacoOrder implements Persistable<Long> {
    public static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private Date placedAt;

    @NotBlank(message = "Delivery Name is required")
    private String deliveryName;
    @NotBlank(message = "Delivery Street is required")
    private String deliveryStreet;
    @NotBlank(message = "Delivery City is required")
    private String deliveryCity;
    @NotBlank(message = "Delivery State is required")
    private String deliveryState;
    @NotBlank(message = "Delivery Zip is required")
    private String deliveryZip;
    @CreditCardNumber(message = "Not a valid credit card number")
    private String ccNumber;
    @Pattern(regexp = "^(0[1-9]|1[0-2])([\\/])([2-9][0-9])$", message = "must be formatted MM/YY")
    private String ccExpiration;
    @Digits(integer = 3, fraction = 0, message = "Invalid CCV")
    private String ccCVV;

    private List<Taco> tacos = new ArrayList<>();

    public void addTaco(Taco taco) {
        this.tacos.add(taco);
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
