package info.binlan;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LeetcodeRunner implements Closeable {
    public static final String LOGIN_URL = "http://oj.leetcode.com/accounts/login/";
    public static final String LOGOUT_URL = "http://oj.leetcode.com/accounts/logout/";
    public static final String PROBLEMS_URL = "http://oj.leetcode.com/problems/";
    public static final String SUBMISSION_BASE_URL = "http://oj.leetcode.com/submissions/detail/";
    public static final String CHECK_URL_PATTERN = "http://oj.leetcode.com/submissions/detail/%s/check/";
    public static final String SUBMISSION_URL_PATTERN = "http://oj.leetcode.com/problems/%s/submit/";

    public static final String COOKIE_CSRFTOKEN = "csrftoken";
    public static final String COOKIE_SESSIONID = "sessionid";

    public static final String LOGIN_TOKEN = "csrfmiddlewaretoken";
    public static final String LOGIN_USERNAME = "login";
    public static final String LOGIN_PASSWORD = "password";

    public static final String CODE_SUBMISSION_DATA_INPUT = "data_input";
    public static final String CODE_SUBMISSION_JUDGE_TYPE = "judge_type";
    public static final String CODE_SUBMISSION_LANG = "lang";
    public static final String CODE_SUBMISSION_QUESTION_ID = "question_id";
    public static final String CODE_SUBMISSION_TYPED_CODE = "typed_code";

    public static final long DEFAULT_CHECK_INTERVAL = 300L;

    public static enum Questions {
        TWO_SUM("two-sum", 1),
        ADD_TWO_NUMBERS("add-two-numbers", 2),
        LONGEST_SUBSTRING_WITHOUT_REPEATING_CHARACTERS("longest-substring-without-repeating-characters", 3),
        MEDIAN_OF_TWO_SORTED_ARRAYS("median-of-two-sorted-arrays", 4),
        LONGEST_PALINDROMIC_SUBSTRING("longest-palindromic-substring", 5),
        ZIGZAG_CONVERSION("zigzag-conversion", 6),
        REVERSE_INTEGER("reverse-integer", 7),
        STRING_TO_INTEGER_ATOI("string-to-integer-atoi", 8),
        PALINDROME_NUMBER("palindrome-number", 9),
        REGULAR_EXPRESSION_MATCHING("regular-expression-matching", 10),
        CONTAINER_WITH_MOST_WATER("container-with-most-water", 11),
        INTEGER_TO_ROMAN("integer-to-roman", 12),
        ROMAN_TO_INTEGER("roman-to-integer", 13),
        LONGEST_COMMON_PREFIX("longest-common-prefix", 14),
        THREE_SUM("3sum", 15),
        THREE_SUM_CLOSEST("3sum-closest", 16),
        FOUR_SUM("4sum", 17),
        LETTER_COMBINATIONS_OF_A_PHONE_NUMBER("letter-combinations-of-a-phone-number", 18),
        REMOVE_NTH_NODE_FROM_END_OF_LIST("remove-nth-node-from-end-of-list", 19),
        VALID_PARENTHESES("valid-parentheses", 20),
        MERGE_TWO_SORTED_LISTS("merge-two-sorted-lists", 21),
        GENERATE_PARENTHESES("generate-parentheses", 22),
        MERGE_K_SORTED_LISTS("merge-k-sorted-lists", 23),
        SWAP_NODES_IN_PAIRS("swap-nodes-in-pairs", 24),
        REVERSE_NODES_IN_K_GROUP("reverse-nodes-in-k-group", 25),
        REMOVE_DUPLICATES_FROM_SORTED_ARRAY("remove-duplicates-from-sorted-array", 26),
        REMOVE_ELEMENT("remove-element", 27),
        IMPLEMENT_STRSTR("implement-strstr", 28),
        DIVIDE_TWO_INTEGERS("divide-two-integers", 29),
        SUBSTRING_WITH_CONCATENATION_OF_ALL_WORDS("substring-with-concatenation-of-all-words", 30),
        NEXT_PERMUTATION("next-permutation", 31),
        LONGEST_VALID_PARENTHESES("longest-valid-parentheses", 32),
        SEARCH_IN_ROTATED_SORTED_ARRAY("search-in-rotated-sorted-array", 33),
        SEARCH_FOR_A_RANGE("search-for-a-range", 34),
        SEARCH_INSERT_POSITION("search-insert-position", 35),
        VALID_SUDOKU("valid-sudoku", 36),
        SUDOKU_SOLVER("sudoku-solver", 37),
        COUNT_AND_SAY("count-and-say", 38),
        COMBINATION_SUM("combination-sum", 39),
        COMBINATION_SUM_II("combination-sum-ii", 40),
        FIRST_MISSING_POSITIVE("first-missing-positive", 41),
        TRAPPING_RAIN_WATER("trapping-rain-water", 42),
        MULTIPLY_STRINGS("multiply-strings", 43),
        WILDCARD_MATCHING("wildcard-matching", 44),
        JUMP_GAME_II("jump-game-ii", 45),
        PERMUTATIONS("permutations", 46),
        PERMUTATIONS_II("permutations-ii", 47),
        ROTATE_IMAGE("rotate-image", 48),
        ANAGRAMS("anagrams", 49),
        POWX_N("powx-n", 50),
        N_QUEENS("n-queens", 51),
        N_QUEENS_II("n-queens-ii", 52),
        MAXIMUM_SUBARRAY("maximum-subarray", 53),
        SPIRAL_MATRIX("spiral-matrix", 54),
        JUMP_GAME("jump-game", 55),
        MERGE_INTERVALS("merge-intervals", 56),
        INSERT_INTERVAL("insert-interval", 57),
        LENGTH_OF_LAST_WORD("length-of-last-word", 58),
        SPIRAL_MATRIX_II("spiral-matrix-ii", 59),
        PERMUTATION_SEQUENCE("permutation-sequence", 60),
        ROTATE_LIST("rotate-list", 61),
        UNIQUE_PATHS("unique-paths", 62),
        UNIQUE_PATHS_II("unique-paths-ii", 63),
        MINIMUM_PATH_SUM("minimum-path-sum", 64),
        VALID_NUMBER("valid-number", 65),
        PLUS_ONE("plus-one", 66),
        ADD_BINARY("add-binary", 67),
        TEXT_JUSTIFICATION("text-justification", 68),
        SQRTX("sqrtx", 69),
        CLIMBING_STAIRS("climbing-stairs", 70),
        SIMPLIFY_PATH("simplify-path", 71),
        EDIT_DISTANCE("edit-distance", 72),
        SET_MATRIX_ZEROS("set-matrix-zeroes", 73),
        SEARCH_A_2D_MATRIX("search-a-2d-matrix", 74),
        SORT_COLORS("sort-colors", 75),
        MINIMUM_WINDOW_SUBSTRING("minimum-window-substring", 76),
        COMBINATIONS("combinations", 77),
        SUBSETS("subsets", 78),
        WORD_SEARCH("word-search", 79),
        REMOVE_DUPLICATES_FROM_SORTED_ARRAY_II("remove-duplicates-from-sorted-array-ii", 80),
        SEARCH_IN_ROTATED_SORTED_ARRAY_II("search-in-rotated-sorted-array-ii", 81),
        REMOVE_DUPLICATES_FROM_SORTED_LIST_II("remove-duplicates-from-sorted-list-ii", 82),
        REMOVE_DUPLICATES_FROM_SORTED_LIST("remove-duplicates-from-sorted-list", 83),
        LARGEST_RECTANGLE_IN_HISTOGRAM("largest-rectangle-in-histogram", 84),
        MAXIMAL_RECTANGLE("maximal-rectangle", 85),
        PARTITION_LIST("partition-list", 86),
        SCRAMBLE_STRING("scramble-string", 87),
        MERGE_SORTED_ARRAY("merge-sorted-array", 88),
        GRAY_CODE("gray-code", 89),
        SUBSETS_II("subsets-ii", 90),
        DECODE_WAYS("decode-ways", 91),
        REVERSE_LINKED_LIST_II("reverse-linked-list-ii", 92),
        RESTORE_IP_ADDRESSES("restore-ip-addresses", 93),
        BINARY_TREE_INORDER_TRAVERSAL("binary-tree-inorder-traversal", 94),
        UNIQUE_BINARY_SEARCH_TREE_II("unique-binary-search-trees-ii", 95),
        UNIQUE_BINARY_SEARCH_TREE("unique-binary-search-trees", 96),
        INTERLEAVING_STRING("interleaving-string", 97),
        VALIDATE_BINARY_SEARCH_TREE("validate-binary-search-tree", 98),
        RECOVER_BINARY_SEARCH_TREE("recover-binary-search-tree", 99),
        SAME_TREE("same-tree", 100),
        SYMMETRIC_TREE("symmetric-tree", 101),
        BINARY_TREE_LEVEL_ORDER_TRAVERSAL("binary-tree-level-order-traversal", 102),
        BINARY_TREE_ZIGZAG_LEVEL_ORDER_TRAVERSAL("binary-tree-zigzag-level-order-traversal", 103),
        MAXIMUM_DEPTH_OF_BINARY_TREE("maximum-depth-of-binary-tree", 104),
        CONSTRUCT_BINARY_TREE_FROM_PREORDER_AND_INORDER_TRAVERSAL("construct-binary-tree-from-preorder-and-inorder-traversal", 105),
        CONSTRUCT_BINARY_TREE_FROM_INORDER_AND_POSTORDER_TRAVERSAL("construct-binary-tree-from-inorder-and-postorder-traversal", 106),
        BINARY_TREE__LEVEL_ORDER_TRAVERSAL("binary-tree-level-order-traversal-ii", 107),
        CONVERT_SORTED_ARRAY_TO_BINARY_SEARCH_TREE("convert-sorted-array-to-binary-search-tree", 108),
        CONVERT_SORTED_LIST_TO_BINARY_SEARCH_TREE("convert-sorted-list-to-binary-search-tree", 109),
        BALANCED_BINARY_TREE("balanced-binary-tree", 110),
        MINIMUM_DEPTH_OF_BINARY_TREE("minimum-depth-of-binary-tree", 111),
        PATH_SUM("path-sum", 112),
        PATH_SUM_II("path-sum-ii", 113),
        FLATTEN_BINARY_TREE_TO_LINKED_LIST("flatten-binary-tree-to-linked-list", 114),
        DISTINCT_SUBSEQUENCES("distinct-subsequences", 115),
        POPULATING_NEXT_RIGHT_POINTERS_IN_EACH_NODE("populating-next-right-pointers-in-each-node", 116),
        POPULATING_NEXT_RIGHT_POINTERS_IN_EACH_NODE_II("populating-next-right-pointers-in-each-node-ii", 117),
        PASCALS_TRIANGLE("pascals-triangle", 118),
        PASCALS_TRIANGLE_II("pascals-triangle-ii", 119),
        TRIANGLE("triangle", 120),
        BEST_TIME_TO_BUY_AND_SELL_STOCK("best-time-to-buy-and-sell-stock", 121),
        BEST_TIME_TO_BUY_AND_SELL_STOCK_II("best-time-to-buy-and-sell-stock-ii", 122),
        BEST_TIME_TO_BUY_AND_SELL_STOCK_III("best-time-to-buy-and-sell-stock-iii", 123),
        BINARY_TREE_MAXIMUM_PATH_SUM("binary-tree-maximum-path-sum", 124),
        VALID_PALINDROME("valid-palindrome", 125),
        WORD_LADDER_II("word-ladder-ii", 126),
        WORD_LADDER("word-ladder", 127),
        LONGEST_CONSECUTIVE_SEQUENCE("longest-consecutive-sequence", 128),
        SUM_ROOT_TO_LEAF_NUMBERS("sum-root-to-leaf-numbers", 129),
        SURROUNDED_REGIONS("surrounded-regions", 130),
        PALINDROME_PARTITIONING("palindrome-partitioning", 131),
        PALINDROME_PARTITIONING_II("palindrome-partitioning", 132);

        private static final HashMap<String, Questions> questionTagMap = new HashMap<String, Questions>();
        private static final HashMap<Integer, Questions> questionIdMap = new HashMap<Integer, Questions>();

        static {
            for (Questions q : Questions.values()) {
                questionTagMap.put(q.tag, q);
                questionIdMap.put(q.id, q);
            }
        }

        public final String tag;
        public final int id;

        private Questions(String _tag, int _id) {
            tag = _tag;
            id = _id;
        }

        public String getUrl() {
            return String.format(SUBMISSION_URL_PATTERN, tag);
        }

        public static Questions getQuestion(String tag) {
            return questionTagMap.get(tag);
        }

        public static Questions getQuestion(int id) {
            return questionIdMap.get(id);
        }
    }

    public static enum JudgeTypes {
        LARGE("large"),
        SMALL("small");

        public final String type;

        private JudgeTypes(String _type) {
            type = _type;
        }
    }

    public static enum Langs {
        JAVA("java"),
        CPP("cpp");

        public final String lang;

        private Langs(String _lang) {
            lang = _lang;
        }
    }

    public static enum States {
        SUCCESS("SUCCESS"),
        PENDING("PENDING"),
        FAILURE("FAILURE");

        public final String state;

        private States(String _state) {
            state = _state;
        }
    }

    public static enum StatusCodes {
        ACCEPTED(10, "AC", "Accepted"),
        WRONG_ANSWER(11, "WA", "Wrong Answer"),
        MEMORY_LIMIT_EXCEEDED(12, "MLE", "Memory Limit Exceeded"),
        OUTPUT_LIMIT_EXCEEDED(13, "OLE", "Output Limit Exceeded"),
        TIME_LIMIT_EXCEEDED(14, "TLE", "Time Limit Exceeded"),
        RUNTIME_ERROR(15, "RE", "Runtime Error"),
        INTERNAL_ERROR(16, "IE", "Internal Error"),
        COMPILE_ERROR(20, "CE", "Compile Error"),
        UNKNOWN_ERROR(21, "UE", "Unknown Error");

        private static final HashMap<String, StatusCodes> STATUS_CODES = new HashMap<String, StatusCodes>();
        static {
            for (StatusCodes statusCode : StatusCodes.values()) {
                STATUS_CODES.put(Integer.toString(statusCode.code), statusCode);
            }
        }

        public final String classText;
        public final String statusText;
        public final int code;

        private StatusCodes(int _code, String _classText, String _statusText) {
            code = _code;
            classText = _classText;
            statusText = _statusText;
        }

        public static StatusCodes getStatusCode(String code) {
            return STATUS_CODES.get(code);
        }

        public static StatusCodes getStatusCode(int code) {
            return STATUS_CODES.get(Integer.toString(code));
        }
    }

    public static enum ResponseFields {
        STATUS_CODE("status_code"),
        STATE("state"),
        TOTAL_TESTCASES("total_testcases"),
        TOTAL_CORRECT("total_correct"),
        CODE_OUTPUT("code_output"),
        SUCCESS("success"),
        COMPARE_RESULT("compare_result"),
        STATUS_RUNTIME("status_runtime"),
        INPUT_FORMATTED("input_formatted"),
        EXPECTED_OUTOUR("expected_output"),
        SUBMISSION_ID("submission_id");

        public final String code;

        private ResponseFields(String _code) {
            code = _code;
        }
    }

    public static class Utils {
        public static String getCheckSubmissionUrl(Integer id) {
            return String.format(CHECK_URL_PATTERN, id.toString());
        }
    }

    private final CloseableHttpClient client;
    private final HttpClientContext context;
    private final String username;
    private final String password;
    private long checkInterval;

    public LeetcodeRunner(String username, String password) {
        this.client = HttpClients.createDefault();
        this.context = HttpClientContext.create();
        this.context.setCookieStore(new BasicCookieStore());
        this.username = username;
        this.password = password;
        this.checkInterval = DEFAULT_CHECK_INTERVAL;
    }

    public boolean login() throws Exception {
        if (!isLoggedIn()) {
            getLoginPage();
            HttpPost loginPost = new HttpPost(LOGIN_URL);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair(LOGIN_USERNAME, username));
            nvps.add(new BasicNameValuePair(LOGIN_PASSWORD, password));
            nvps.add(new BasicNameValuePair(LOGIN_TOKEN, getTokenCookie()));

            CloseableHttpResponse response = null;
            try {
                loginPost.setEntity(new UrlEncodedFormEntity(nvps));
                response = client.execute(loginPost, context);
                return PROBLEMS_URL.equals(response.getHeaders("Location")[0].getValue());
            } finally {
                if (response != null) response.close();
            }
        }

        return true;
    }

    public void logout() throws Exception {
        if (isLoggedIn()) {
            HttpGet logoutGet = new HttpGet(LOGOUT_URL);
            CloseableHttpResponse response = null;
            try {
                response = client.execute(logoutGet, context);
            } finally {
                if (response != null) response.close();
            }
        }
    }

    boolean isLoggedIn() {
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            if (COOKIE_SESSIONID.equals(cookie.getName()) && cookie.getExpiryDate() == null) {
                return true;
            }
        }
        return false;
    }

    public int postCode(Questions question, JudgeTypes judgeType, Langs lang, String code) throws Exception {
        ensureLoggedIn();
        HttpPost codePost = new HttpPost(question.getUrl());
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(LOGIN_TOKEN, getTokenCookie()));
        nvps.add(new BasicNameValuePair(CODE_SUBMISSION_DATA_INPUT, ""));
        nvps.add(new BasicNameValuePair(CODE_SUBMISSION_JUDGE_TYPE, judgeType.type));
        nvps.add(new BasicNameValuePair(CODE_SUBMISSION_LANG, lang.lang));
        nvps.add(new BasicNameValuePair(CODE_SUBMISSION_QUESTION_ID, Integer.toString(question.id)));
        nvps.add(new BasicNameValuePair(CODE_SUBMISSION_TYPED_CODE, code));
        CloseableHttpResponse response = null;
        InputStream is = null;
        try {
            codePost.setEntity(new UrlEncodedFormEntity(nvps));
            response = client.execute(codePost, context);
            is = response.getEntity().getContent();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(is);
            return Integer.parseInt(node.path(ResponseFields.SUBMISSION_ID.code).asText());
        } finally {
            if (is != null) is.close();
            if (response != null) response.close();
        }
    }

    public JsonNode checkSubmission(String url) throws Exception {
        ensureLoggedIn();
        HttpGet checkGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        InputStream is = null;
        try {
            response = client.execute(checkGet, context);
            is = response.getEntity().getContent();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(is);
        } finally {
            if (is != null) is.close();
            if (response != null) response.close();
        }
    }

    public JsonNode getResult(Integer id) throws Exception {
        String url = Utils.getCheckSubmissionUrl(id);
        ensureLoggedIn();
        JsonNode result = null;
        String state = null;
        do {
            result = checkSubmission(url);
            state = result.path(ResponseFields.STATE.code).asText();
            Thread.sleep(checkInterval);
        } while (state != null && States.PENDING.state.equals(state));

        return result;
    }

    public JsonNode run(Questions question, String code) throws Exception {
        return run(question, JudgeTypes.LARGE, Langs.JAVA, code);
    }

    public JsonNode run(Questions question, JudgeTypes judgeType, Langs lang, String code) throws Exception {
        Integer id = postCode(question, judgeType, lang, code);
        return getResult(id);
    }

    @Override
    public void close() throws IOException {
        if (this.client != null) {
            try { logout(); } catch (Exception e) {}
            client.close();
        }
    }

    private String getTokenCookie() {
        for (Cookie cookie : context.getCookieStore().getCookies()) {
            if (cookie.getName().equals(COOKIE_CSRFTOKEN)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void getLoginPage() throws Exception {
        if (!isLoggedIn()) {
            HttpGet loginGet = new HttpGet(LOGIN_URL);
            CloseableHttpResponse response = null;
            try {
                response = client.execute(loginGet, context);
            } finally {
                if (response != null) response.close();
            }
        }
    }

    private void ensureLoggedIn() throws Exception {
        if (!isLoggedIn()) {
            login();
        }
    }

    public long getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }
}
