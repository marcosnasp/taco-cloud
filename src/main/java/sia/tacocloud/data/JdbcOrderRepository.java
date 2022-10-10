package sia.tacocloud.data;

import org.springframework.asm.Type;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import sia.tacocloud.model.IngredientRef;
import sia.tacocloud.model.Taco;
import sia.tacocloud.model.TacoOrder;

import java.sql.Types;
import java.util.*;

public class JdbcOrderRepository implements OrderRepository {

    private final JdbcOperations jdbcOperations;

    public JdbcOrderRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    @Transactional
    public TacoOrder save(TacoOrder tacoOrder) {
        String sql = "insert into Taco_Order " +
                "(delivery_name, delivery_street, delivery_city, " +
                "delivery_state, delivery_zip, cc_number, " +
                "cc_explanation, cc_cvv, placed_at) " +
                "values (?,?,?,?,?,?,?,?,?)";

        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sql,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP);
        pscf.setReturnGeneratedKeys(true);

        tacoOrder.setPlacedAt(new Date());
        PreparedStatementCreator psc =
                pscf.newPreparedStatementCreator(
                        Arrays.asList(
                                tacoOrder.getDeliveryName(),
                                tacoOrder.getDeliveryStreet(),
                                tacoOrder.getDeliveryCity(),
                                tacoOrder.getDeliveryState(),
                                tacoOrder.getDeliveryZip(),
                                tacoOrder.getCcNumber(),
                                tacoOrder.getCcExpiration(),
                                tacoOrder.getCcCVV(),
                                tacoOrder.getPlacedAt()
                        ));
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);
        long orderId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        tacoOrder.setId(orderId);

        List<Taco> tacos = tacoOrder.getTacos();
        int i = 0;
        for (Taco taco : tacos) {
            saveTaco(orderId, i++, taco);
        }
        return tacoOrder;
    }

    private long saveTaco(Long orderId, int orderKey, Taco taco) {
        taco.setCreatedAt(new Date());
        PreparedStatementCreatorFactory pscf =
                new PreparedStatementCreatorFactory(
                        "insert into Taco "
                                + "(name, created_at, taco_order, taco_order_key) "
                                + "values (?, ?, ?, ?)",
                        Types.VARCHAR, Types.TIMESTAMP, Type.LONG, Type.LONG
                );
        pscf.setReturnGeneratedKeys(true);

        PreparedStatementCreator psc =
                pscf.newPreparedStatementCreator(
                        Arrays.asList(
                                taco.getName(),
                                taco.getCreatedAt(),
                                orderId,
                                orderKey));

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);
        long tacoId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        taco.setId(tacoId);

        saveIngredientRefs(tacoId, taco.getIngredients());

        return tacoId;
    }

    private void saveIngredientRefs(
            long tacoId, List<IngredientRef> ingredientRefs) {
        int key = 0;
        for (IngredientRef ingredientRef : ingredientRefs) {
            jdbcOperations.update(
                    "insert into Ingredient_Ref (ingredient, taco, taco_key) "
                            + "values (?, ?, ?)",
                    ingredientRef.getIngredient(), tacoId, key++);
        }
    }

    @Override
    public Optional<TacoOrder> findById(Long id) {
        try {
            TacoOrder order = jdbcOperations.queryForObject(
                    "select id, delivery_name, delivery_street, delivery_city, "
                            + "delivery_state, delivery_zip, cc_number, cc_expiration, "
                            + "cc_cvv, placed_at from Taco_Order where id=?",
                    (row, rowNum) -> {
                        TacoOrder tacoOrder = new TacoOrder();
                        tacoOrder.setId(row.getLong("id"));
                        tacoOrder.setDeliveryName(row.getString("delivery_name"));
                        tacoOrder.setDeliveryStreet(row.getString("delivery_street"));
                        tacoOrder.setDeliveryCity(row.getString("delivery_city"));
                        tacoOrder.setDeliveryState(row.getString("delivery_state"));
                        tacoOrder.setDeliveryZip(row.getString("delivery_zip"));
                        tacoOrder.setCcNumber(row.getString("cc_number"));
                        tacoOrder.setCcExpiration(row.getString("cc_expiration"));
                        tacoOrder.setCcCVV(row.getString("cc_cvv"));
                        tacoOrder.setPlacedAt(new Date(row.getTimestamp("placed_at").getTime()));
                        tacoOrder.setTacos(findTacosByOrderId(row.getLong("id")));
                        return tacoOrder;
                    }, id);
            return Optional.of(order);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    private List<Taco> findTacosByOrderId(long orderId) {
        return jdbcOperations.query(
                "select id, name, created_at from Taco "
                        + "where taco_order=? order by taco_order_key",
                (row, rowNum) -> {
                    Taco taco = new Taco();
                    taco.setId(row.getLong("id"));
                    taco.setName(row.getString("name"));
                    taco.setCreatedAt(new Date(row.getTimestamp("created_at").getTime()));
                    taco.setIngredients(findIngredientsByTacoId(row.getLong("id")));
                    return taco;
                },
                orderId);
    }

    private List<IngredientRef> findIngredientsByTacoId(long tacoId) {
        return jdbcOperations.query(
                "select ingredient from Ingredient_Ref "
                        + "where taco = ? order by taco_key",
                (row, rowNum) -> {
                    return new IngredientRef(row.getString("ingredient"));
                },
                tacoId);
    }
}
