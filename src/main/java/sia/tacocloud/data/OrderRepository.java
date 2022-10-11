package sia.tacocloud.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import sia.tacocloud.model.TacoOrder;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends CrudRepository<TacoOrder, Long> {
    List<TacoOrder> findByDeliveryZip(String deliveryZip);
    List<TacoOrder> readOrdersByDeliveryZipAndPlacedAtBetween(
            String deliveryZip, Date startDate, Date endDate);
    List<TacoOrder> findByDeliveryStateAndDeliveryCity(
            String deliveryState, String deliveryCity);
    List<TacoOrder> findByDeliveryCityOrderByDeliveryCity(String city);
    @Query("SELECT t FROM TacoOrder t WHERE t.deliveryCity='Seattle'")
    List<TacoOrder> readOrdersDeliveredInSeattle();
}
