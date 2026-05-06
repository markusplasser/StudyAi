import dev.langchain4j.model.ollama.OllamaChatModel;
import org.example.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.*;
import org.mockito.Mock;


class UniTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    class AIOperations{
        @Mock
        OllamaChatModel mockChat;

        @BeforeAll
        static void init(){
            System.out.println("Testing started!!");
        }

        @Test
        void testAIInitialization() {
            AI_Operations ai = new AI_Operations(mockChat);
            assertNotNull(ai, "Class should have been created");
        }

        //Model won't be called bc the response is predefined
        @Test
        void testAmountQuestionSendsCorrectPrompt() {
            AI_Operations ai = new AI_Operations(mockChat);

            String inputText = "Java ist toll.";
            String preMadeAnswer = "Here are the questions";

            when(mockChat.generate(anyString())).thenReturn(preMadeAnswer);

            String result = ai.anz_Fragen(inputText, 2, 3);

            assertEquals(preMadeAnswer, result);
        }
    }
    @Nested
    class FragenAntwortenTest{
        @Test
        void testConstructorAndGetters() {
            String question = "What is  Java?";
            String[] content = {"Car", "Island", "Language"};
            boolean[] answer = {false, true, true};

            Fragen_Antworten fa = new Fragen_Antworten(question, content, answer);

            assertEquals(question, fa.getFrage());
            assertArrayEquals(content, fa.getContent());
            assertArrayEquals(answer, fa.getLoesung());
        }

        @Test
        void testConstructorException() {
            String[] content = {"A", "B"};
            boolean[] answers = {true};

            assertThrows(MyException.class, () -> new Fragen_Antworten("Test", content, answers));
        }

        @Test
        void testSetters() {
            Fragen_Antworten fa = new Fragen_Antworten();
            String newQuestion = "Neu?";
            String[] newContent = {"Ja"};
            boolean[] newAnswer = {true};

            fa.setFrage(newQuestion);
            fa.setContent(newContent);
            fa.setLoesung(newAnswer);

            assertEquals(newQuestion, fa.getFrage());
            assertArrayEquals(newContent, fa.getContent());
            assertArrayEquals(newAnswer, fa.getLoesung());
        }

        @Test
        void testSetFirstQuestion() {
            String[] content = {"Alt1", "Alt2"};
            Fragen_Antworten fa = new Fragen_Antworten("Test", content, new boolean[]{false, false});

            fa.setFirstQuestion("Neu1");

            assertEquals("Neu1", fa.getContent()[0]);
            assertEquals("Alt2", fa.getContent()[1]);
        }

        @Test
        void testToStringFormat() {
            String[] content = {"Wrong", "Right"};
            boolean[] answers = {false, true};
            Fragen_Antworten fa = new Fragen_Antworten("Q", content, answers);

            String result = fa.toString();

            assertTrue(result.contains("Question: Q"));
            assertTrue(result.contains("[ ] Wrong"));
            assertTrue(result.contains("[X] Right"));
        }

        @Test
        void testToStringNullSafety() {
            Fragen_Antworten fa = new Fragen_Antworten();
            String result = fa.toString();
            assertTrue(result.contains("(No answer defined)"));
        }
    }
}
