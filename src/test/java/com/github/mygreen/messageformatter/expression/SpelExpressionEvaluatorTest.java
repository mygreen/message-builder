package com.github.mygreen.messageformatter.expression;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;


/**
 * {@link SpelExpressionEvaluator}のテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
class SpelExpressionEvaluatorTest {

    private SpelExpressionEvaluator expressionEvaluator;

    @BeforeEach
    void setUp() throws Exception {
        this.expressionEvaluator = new SpelExpressionEvaluator();
    }

    @DisplayName("数式を評価する")
    @Test
    void testEvaluate_num() {

        String expression = "1 + #add";
        Map<String, Object> variables = Map.of("add", 20);

        Object result = expressionEvaluator.evaluate(expression, variables);
        assertThat(result).isEqualTo(21);

    }

    @DisplayName("空判定を式評価する - 関数を使用する")
    @Test
    void testEvaluate_empty() {

        String expression = "#empty(#label) ? '空です' : #label";

        {
            Map<String, Object> variables = new HashMap<>();

            Object result = (String) expressionEvaluator.evaluate(expression, variables);
            assertThat(result).isEqualTo("空です");
        }

        {
            Map<String, Object> variables = new HashMap<>();

            variables.put("label", "Hello world.");
            Object result = (String) expressionEvaluator.evaluate(expression, variables);
            assertThat(result).isEqualTo("Hello world.");
        }

    }

    @DisplayName("式の評価に失敗した場合 - 式中の変数が存在しない")
    @Test
    void testEvaluation_fail() {

        String expression = "aaa ?  label";

        Map<String, Object> variables = new HashMap<>();

        assertThatThrownBy(() -> expressionEvaluator.evaluate(expression, variables))
            .isInstanceOf(SpelParseException.class);

    }

    @DisplayName("関数の呼び出し")
    @Test
    void testEvaluation_function() {

        String expression = "#join(#array, ', ')";

        Map<String, Object> variables = new HashMap<>();
        variables.put("array", new int[]{1,2,3});

        Object result = expressionEvaluator.evaluate(expression, variables);
        assertThat(result).isEqualTo("1, 2, 3");

    }

    @DisplayName("カスタムしたExpressionParserを指定する場合")
    @Test
    void testConstructor_customExpressionParser() {

        SpelParserConfiguration configuration = new SpelParserConfiguration(true, true);
        ExpressionParser expressionParser = new SpelExpressionParser(configuration);
        this.expressionEvaluator = new SpelExpressionEvaluator(expressionParser);

        String expression = "#obj.list[3]";

        Nested obj = new Nested();

        Map<String, Object> variables = new HashMap<>();
        variables.put("obj", obj);

        Object result = expressionEvaluator.evaluate(expression, variables);
        assertThat(result).isEqualTo("");

        // リストのサイズが自動的に増えていること
        assertThat(obj.list).hasSize(4);

    }

    static class Nested {

        public List<String> list;
    }
}
