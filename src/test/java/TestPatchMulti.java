import com.starkcore.Settings;
import com.starkcore.error.InputErrors;
import com.starkcore.utils.Resource;
import com.starkcore.utils.Rest;
import com.starkcore.utils.StarkHost;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// There is no seam to replace the outgoing HTTP call of the core (the OkHttp client is built inside Response), so, like
// TestRest, these tests talk to the sandbox and every case is a rejection by the API. Only the invalidHolder case tells PATCH
// from POST (the sandbox answers the same item with invalidCard to a POST); the other two prove that the items travel under
// the plural resource name and that Resource items serialize like Map items. Reading a successful response into objects is
// covered by the SDK tests that assign holders in the sandbox.
public class TestPatchMulti {

    public static final class Card extends Resource {
        static ClassData data = new ClassData(Card.class, "IssuingCard");

        public String holderName;
        public String holderTaxId;
        public String holderExternalId;
        public String[] tags;
        public String status;

        public Card(String id) {
            super(id);
        }
    }

    private static List<String> rejectedCodes(List<?> items) throws Exception {
        Settings.user = utils.User.defaultProject();
        try {
            Rest.patchMulti("0.0.0", StarkHost.infra.toString(), "v2", null, Card.data, items, "en-US", 15);
        } catch (InputErrors errors) {
            return errors.errors.stream().map(error -> error.code).collect(Collectors.toList());
        }
        Assert.fail("the API should have rejected the list");
        return new ArrayList<>();
    }

    @Test
    public void testItemWithAPartialHolderIsRejectedAsInvalidHolder() throws Exception {
        Map<String, Object> item = new HashMap<>();
        item.put("id", "0000000000000001");
        item.put("holderName", "Tony Stark");

        Assert.assertTrue(rejectedCodes(List.of(item)).contains("invalidHolder"));
    }

    @Test
    public void testItemWithAnUnknownCardIsRejectedAsInvalidCard() throws Exception {
        Map<String, Object> item = new HashMap<>();
        item.put("id", "0000000000000001");
        item.put("tags", new String[]{"sdk-test"});

        Assert.assertTrue(rejectedCodes(List.of(item)).contains("invalidCard"));
    }

    @Test
    public void testResourceItemsAreSerializedLikeMapItems() throws Exception {
        Card card = new Card("0000000000000001");
        card.tags = new String[]{"sdk-test"};

        Assert.assertTrue(rejectedCodes(List.of(card)).contains("invalidCard"));
    }
}
