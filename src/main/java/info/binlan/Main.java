package info.binlan;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import info.binlan.LeetcodeRunner.Questions;
import info.binlan.LeetcodeRunner.StatusCodes;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.fasterxml.jackson.databind.JsonNode;

public class Main {
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new BasicParser();
        Options options = buildOptions();
        CommandLine line = parser.parse(options, args);
        if (line.hasOption("l")) {
            printQuestionList();
        } else if (line.hasOption("f")
                   && line.hasOption("u")
                   && line.hasOption("p")
                   && (line.hasOption("tag") || line.hasOption("id"))){
            JsonNode result = runTest(line);
            String statusCodeString = result.path(LeetcodeRunner.ResponseFields.STATUS_CODE.code).asText();
            StatusCodes statusCode = StatusCodes.getStatusCode(statusCodeString);
            if (statusCode.equals(StatusCodes.ACCEPTED)) {
                System.out.println("Your answer is accepted!");
            } else {
                System.out.println(result.toString());
            }

        } else {
            printHelpMessage(options);
        }
    }

    private static JsonNode runTest(CommandLine line) throws Exception {
        LeetcodeRunner runner = null;
        try {
            Questions question = null;
            if (line.hasOption("id")) {
                question = LeetcodeRunner.Questions.getQuestion(Integer.parseInt(line.getOptionValue("id")));
            } else if (line.hasOption("tag")) {
                question = LeetcodeRunner.Questions.getQuestion(line.getOptionValue("tag"));
            }
            if (question == null) {
                printQuestionList();
            }

            runner = new LeetcodeRunner(line.getOptionValue("u"), line.getOptionValue("p"));

            if (line.hasOption("i")) {
                runner.setCheckInterval(Long.parseLong(line.getOptionValue("i")));
            }

            String code = getCode(line.getOptionValue("f"));

            if (code == null) {
                throw new Exception("Cannot parse the source code, please check your file");
            }

            return runner.run(question, code);
        } finally {
            if (runner != null) runner.close();
        }
    }

    private static String getCode(String path) throws Exception {
        byte[] buf = Files.readAllBytes(Paths.get(path));
        return Charset.defaultCharset().decode(ByteBuffer.wrap(buf)).toString();
    }

    private static void printQuestionList() {
        for (Questions question : LeetcodeRunner.Questions.values()) {
            System.out.println(String.format("%d: %s", question.id, question.tag));
        }
    }

    private static void printHelpMessage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("leetcode-runner", options);
    }

    @SuppressWarnings("static-access")
    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("help")
                                       .withDescription("Print this help message.")
                                       .create("h"));
        options.addOption(OptionBuilder.withLongOpt("file")
                                       .withDescription("path to your source file.")
                                       .hasArg()
                                       .withType(String.class)
                                       .create("f"));
        options.addOption(OptionBuilder.withLongOpt("intervl")
                                       .withDescription("interval to wait when checking results.")
                                       .hasArg()
                                       .withType(Long.class)
                                       .create("i"));
        options.addOption(OptionBuilder.withDescription("the name of the questions, use -l/-list to see the list of questions.")
                                       .hasArg()
                                       .withType(String.class)
                                       .create("tag"));
        options.addOption(OptionBuilder.withDescription("the id of the questions, use -l/-list to see the list of questions.")
                                       .hasArg()
                                       .withType(Integer.class)
                                       .create("id"));
        options.addOption(OptionBuilder.withLongOpt("list")
                                       .withDescription("list all the questions.")
                                       .create("l"));
        options.addOption(OptionBuilder.withLongOpt("username")
                                       .withDescription("leetcode username")
                                       .hasArg()
                                       .withType(String.class)
                                       .create("u"));
        options.addOption(OptionBuilder.withLongOpt("password")
                                       .withDescription("leetcode password")
                                       .hasArg()
                                       .withType(String.class)
                                       .create("p"));
        return options;
    }
}
