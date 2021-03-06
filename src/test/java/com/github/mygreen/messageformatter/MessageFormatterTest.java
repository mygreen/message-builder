package com.github.mygreen.messageformatter;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.github.mygreen.messageformatter.expression.SpelExpressionEvaluator;

/**
 * {@link MessageFormatter}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
class MessageFormatterTest {

    private MessageFormatter messageFormatter;

    @BeforeEach
    void setUp() throws Exception {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("test_messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);

        MessageInterpolator messageInterpolator = new MessageInterpolator(new SpelExpressionEvaluator());

        this.messageFormatter = new MessageFormatter(messageSource, messageInterpolator);
    }

    @Test
    void testFormat() {

        String result = messageFormatter.create("test.message01")
                .param("validatedValue", 3.1)
                .param("min", 1)
                .param("max", 10)
                .format();

        assertThat(result).isEqualTo("メッセージ：3.10は、1～10の範囲で入力してください。");

    }

    @Test
    void testFormatRecursively() {

        String result = messageFormatter.create("test.recursive")
                .param("value", "{min}")
//                .var("min", 3)    // min はプロパティファイルに定義
                .formatRecursively();

        assertThat(result).isEqualTo("再帰的なメッセージ：{abc}=3");

    }

    @Test
    void testFormatRecursivelyMax() {

        String result = messageFormatter.create("test.recursiveMax")
                .param("value", "{max}")
//                .var("max", "{value}")    // max はプロパティファイルに定義
                .formatRecursively(5);

        assertThat(result).isEqualTo("再帰的なメッセージ：{abc}={max}");

    }

    @Test
    void testVarWithArray() {

        String result = messageFormatter.create("test.varWithArray")
                .param("arrays", 1, "abc", 3)
                .format();

        assertThat(result).isEqualTo("配列の指定：「1,abc,3」");

    }

    @Test
    void testVarWithAnno() {

        String result = messageFormatter.create("test.varWithAnno")
                .paramWithAnno("anno", Entity.class)
                .format();

        assertThat(result).isEqualTo("変数の初期フォーマット：アノテーション「@Entity」");

    }

    @Test
    void testVarWithEnum() {

        String result = messageFormatter.create("test.varWithEnum")
                .paramWithEnum("enum", Role.Admin)
                .format();

        assertThat(result).isEqualTo("変数の初期フォーマット：列挙型「Role#Admin」");

    }

    @Test
    void testVarWithClass() {
        String result = messageFormatter.create("test.varWithClass")
                .paramWithClass("class", MessageFormatter.class)
                .format();

        assertThat(result).isEqualTo("変数の初期フォーマット：クラス「com.github.mygreen.messageformatter.MessageFormatter」");
    }

    @Test
    void testVarWithClasses() {
        String result = messageFormatter.create("test.varWithClasses")
                .paramWithClass("classes", MessageFormatter.class, SpelExpressionEvaluator.class)
                .format();

        assertThat(result).isEqualTo("変数の初期フォーマット：クラス「com.github.mygreen.messageformatter.MessageFormatter, com.github.mygreen.messageformatter.expression.SpelExpressionEvaluator」");
    }

    /**
     * テスト用アノテーション
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @Documented
    @interface Entity {

        String name() default "";

    }

    /**
     * テスト用列挙型
     *
     */
    enum Role {

        Normal,
        Admin
    }

}
