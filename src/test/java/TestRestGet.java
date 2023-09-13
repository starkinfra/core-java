import com.google.gson.Gson;
import com.starkcore.utils.GsonEvent;
import com.starkcore.utils.Page;
import com.starkcore.utils.Rest;
import com.starkcore.utils.StarkHost;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class TestRestGet {

    @Test
    public void testRestGet() throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("before", "2022-02-01");
        params.put("limit", 1);

        Page transactions = Rest.getPage(
            GsonEvent.getInstance(),
            "0.0.0",
            StarkHost.bank.toString(),
            "v2",
            utils.User.defaultProject(),
            Transaction.data,
            "pt-BR",
            15,
            params
        );
        Transaction transaction = (Transaction) transactions.entities.get(0);
        System.out.println(transaction);
    }
}
