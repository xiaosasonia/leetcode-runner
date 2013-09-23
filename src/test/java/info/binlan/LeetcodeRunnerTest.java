package info.binlan;

import static org.junit.Assert.*;
import info.binlan.LeetcodeRunner.JudgeTypes;
import info.binlan.LeetcodeRunner.Langs;
import info.binlan.LeetcodeRunner.Questions;
import info.binlan.LeetcodeRunner.ResponseFields;
import info.binlan.LeetcodeRunner.States;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class LeetcodeRunnerTest {

    private LeetcodeRunner runner;
    private String code = "public class Solution {"
                        +     "public int[] twoSum(int[] numbers, int target) {"
                        +         "return new int[0];"
                        +     "}"
                        + "}";

    @Before
    public void setUpBeforeClass() throws Exception {
        runner = new LeetcodeRunner("leetcoderunner", "abc123");
    }

    @After
    public void tearDownAfterClass() throws Exception {
        if (runner != null) runner.close();
    }

    @Test
    public void testLogin() throws Exception {
        runner.login();
        assertTrue(runner.isLoggedIn());
    }

    @Test
    public void testLogout() throws Exception {
        testLogin();
        runner.logout();
        assertFalse(runner.isLoggedIn());
    }

    @Test
    public void testPostCode() throws Exception {
        Integer id = runner.postCode(Questions.TWO_SUM, JudgeTypes.LARGE, Langs.JAVA, code);
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    public void testCheckSubmission() throws Exception {
        Integer id = runner.postCode(Questions.TWO_SUM, JudgeTypes.LARGE, Langs.JAVA, code);
        String url = LeetcodeRunner.Utils.getCheckSubmissionUrl(id);
        JsonNode node = runner.checkSubmission(url);
        assertNotNull(node);
        assertEquals(States.PENDING.state, node.path("state").textValue());
    }

    @Test
    public void testGetResult() throws Exception {
        Integer id = runner.postCode(Questions.MEDIAN_OF_TWO_SORTED_ARRAYS, JudgeTypes.LARGE, Langs.JAVA, code);
        JsonNode node = runner.getResult(id);
        assertNotNull(node);
        assertEquals(States.SUCCESS.state, node.path(ResponseFields.STATE.code).asText());
    }
}
