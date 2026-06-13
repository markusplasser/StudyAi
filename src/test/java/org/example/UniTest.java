package org.example;

import dev.langchain4j.model.ollama.OllamaChatModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;

import org.example.manage.AI_Operations;
import org.example.manage.Fragen_Antworten;
import org.example.manage.Handle_Save;
import org.example.manage.MyException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.*;
import org.mockito.Mock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


class UniTest { //Mostly generated

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

            assertTrue(result.contains("Frage: Q"));
            assertTrue(result.contains("[ ]"));
            assertTrue(result.contains("[X]"));
        }

        @Test
        void testToStringNullSafety() {
            Fragen_Antworten fa = new Fragen_Antworten();
            String result = fa.toString();
            assertTrue(result.contains("(Keine Antworten definiert)"));
        }
    }

    @Nested
    class HandleSave{ //AI Generated
        @TempDir
        Path tempDir;

        private Handle_Save handleSave;
        private static final String TEST_SUBFOLDER = "test_save";

        @BeforeEach
        void setUp() throws Exception {
            // Handle_Save nutzt System.getProperty("user.home") intern,
            // daher leiten wir savePath per Reflection auf das TempDir um
            handleSave = new Handle_Save(TEST_SUBFOLDER);
            // savePath auf das TempDir umbiegen, damit keine Dateien im Home-Verzeichnis landen
            Handle_Save.savePath = tempDir.toString();
        }

        // ─────────────────────────────────────────────
        // Konstruktor / Ordner-Erstellung
        // ─────────────────────────────────────────────

        @Test
        @DisplayName("Konstruktor erstellt savePath-Verzeichnis, falls nicht vorhanden")
        void constructor_createsSaveFolder() {
            // Zielordner existiert noch nicht
            Path newFolder = tempDir.resolve("neu");
            assertFalse(Files.exists(newFolder), "Vorbedingung: Ordner darf noch nicht existieren");

            // savePath auf tempDir lenken, damit der Konstruktor dort arbeitet
            // → dafür braucht man einen Konstruktor der den Basispfad akzeptiert:
            Handle_Save hs = new Handle_Save(tempDir.toString(), "neu");

            assertTrue(Files.isDirectory(newFolder), "Konstruktor hätte Ordner anlegen sollen");
        }

        @Test
        @DisplayName("isSaveFolder gibt true zurück für existierendes Verzeichnis")
        void isSaveFolder_existingDir_returnsTrue() {
            assertTrue(handleSave.isSaveFolderExisting(tempDir.toString()));
        }

        @Test
        @DisplayName("isSaveFolder gibt false zurück für nicht-existierendes Verzeichnis")
        void isSaveFolder_nonExistingDir_returnsFalse() {
            assertFalse(handleSave.isSaveFolderExisting(tempDir + "/does_not_exist"));
        }

        // ─────────────────────────────────────────────
        // check_file_exist
        // ─────────────────────────────────────────────

        @Test
        @DisplayName("check_file_exist gibt false zurück wenn Datei fehlt")
        void checkFileExist_missingFile_returnsFalse() {
            assertFalse(handleSave.check_file_exist("nicht_vorhanden"));
        }

        @Test
        @DisplayName("check_file_exist gibt true zurück nach dem Speichern")
        void checkFileExist_afterSave_returnsTrue() {
            Fragen_Antworten frage = buildFrage("Was ist 2+2?", new String[]{"3", "4"}, new boolean[]{false, true});
            handleSave.setArr(new Fragen_Antworten[]{frage});
            handleSave.setFilename("existenz_test");
            handleSave.save();

            assertTrue(handleSave.check_file_exist("existenz_test.bin"));
        }

        // ─────────────────────────────────────────────
        // save() + read() – Roundtrip
        // ─────────────────────────────────────────────

        @Test
        @DisplayName("Roundtrip: Eine Frage mit zwei Antworten")
        void saveAndRead_singleQuestion_roundtrip() {
            Fragen_Antworten frage = buildFrage(
                    "Hauptstadt von Österreich?",
                    new String[]{"Wien", "Berlin"},
                    new boolean[]{true, false}
            );
            handleSave.setArr(new Fragen_Antworten[]{frage});
            handleSave.setFilename("single");
            handleSave.save();

            Fragen_Antworten[] result = handleSave.read("single.bin");

            assertNotNull(result);
            assertEquals(1, result.length);
            assertEquals("Hauptstadt von Österreich?", result[0].getFrage());
            assertArrayEquals(new String[]{"Wien", "Berlin"}, result[0].getContent());
            assertArrayEquals(new boolean[]{true, false}, result[0].getLoesung());
        }

        @Test
        @DisplayName("Roundtrip: Mehrere Fragen mit vier Antwortmöglichkeiten")
        void saveAndRead_multipleQuestions_roundtrip() {
            Fragen_Antworten[] arr = {
                    buildFrage("Frage 1", new String[]{"A", "B", "C", "D"}, new boolean[]{true, false, false, false}),
                    buildFrage("Frage 2", new String[]{"W", "X", "Y", "Z"}, new boolean[]{false, false, true, false}),
                    buildFrage("Frage 3", new String[]{"1", "2", "3", "4"}, new boolean[]{false, true, false, false}),
            };
            handleSave.setArr(arr);
            handleSave.setFilename("multi");
            handleSave.save();

            Fragen_Antworten[] result = handleSave.read("multi.bin");

            assertNotNull(result);
            assertEquals(3, result.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(arr[i].getFrage(), result[i].getFrage(), "Frage " + i + " stimmt nicht überein");
                assertArrayEquals(arr[i].getContent(), result[i].getContent(), "Antworten zu Frage " + i + " stimmen nicht überein");
                assertArrayEquals(arr[i].getLoesung(), result[i].getLoesung(), "Lösungen zu Frage " + i + " stimmen nicht überein");
            }
        }

        @Test
        @DisplayName("Roundtrip: Alle Antworten sind korrekt (alle booleans true)")
        void saveAndRead_allCorrectAnswers_roundtrip() {
            Fragen_Antworten frage = buildFrage(
                    "Multiple-Select Frage",
                    new String[]{"A", "B", "C"},
                    new boolean[]{true, true, true}
            );
            handleSave.setArr(new Fragen_Antworten[]{frage});
            handleSave.setFilename("all_true");
            handleSave.save();

            Fragen_Antworten[] result = handleSave.read("all_true.bin");

            assertNotNull(result);
            assertArrayEquals(new boolean[]{true, true, true}, result[0].getLoesung());
        }

        @Test
        @DisplayName("Roundtrip: Sonderzeichen und Umlaute in Fragetext")
        void saveAndRead_specialCharacters_roundtrip() {
            String spezial = "Wörter mit Umlauten: ä, ö, ü, ß & <Sonderzeichen>!";
            Fragen_Antworten frage = buildFrage(spezial, new String[]{"Ja", "Nein"}, new boolean[]{true, false});
            handleSave.setArr(new Fragen_Antworten[]{frage});
            handleSave.setFilename("umlaute");
            handleSave.save();

            Fragen_Antworten[] result = handleSave.read("umlaute.bin");

            assertNotNull(result);
            assertEquals(spezial, result[0].getFrage());
        }

        // ─────────────────────────────────────────────
        // read() – Fehlerfälle
        // ─────────────────────────────────────────────

        @Test
        @DisplayName("read() gibt null zurück wenn Datei nicht existiert")
        void read_nonExistentFile_returnsNull() {
            Fragen_Antworten[] result = handleSave.read("gibt_es_nicht");
            assertNull(result);
        }

        @Test
        @DisplayName("read() gibt null zurück bei korrupter Datei")
        void read_corruptFile_returnsNull() throws IOException {
            File corrupt = new File(tempDir + File.separator + "corrupt.txt");
            try (FileOutputStream fos = new FileOutputStream(corrupt)) {
                fos.write(new byte[]{0x00, 0x01, 0x02}); // ungültige Bytes
            }
            Fragen_Antworten[] result = handleSave.read("corrupt");
            assertNull(result);
        }

        // ─────────────────────────────────────────────
        // Getter / Setter
        // ─────────────────────────────────────────────

        @Test
        @DisplayName("setArr und getArr funktionieren korrekt")
        void setAndGetArr() {
            Fragen_Antworten[] arr = {buildFrage("X?", new String[]{"a"}, new boolean[]{true})};
            handleSave.setArr(arr);
            assertArrayEquals(arr, handleSave.getArr());
        }

        @Test
        @DisplayName("setFilename und getFilename funktionieren korrekt")
        void setAndGetFilename() {
            handleSave.setFilename("mein_quiz");
            assertEquals("mein_quiz", handleSave.getFilename());
        }

        @Test
        @DisplayName("getPath gibt das Home-Verzeichnis zurück")
        void getPath_returnsHomeDirectory() {
            assertEquals(System.getProperty("user.home"), handleSave.getPath());
        }

        // ─────────────────────────────────────────────
        // Hilfsmethode
        // ─────────────────────────────────────────────

        private Fragen_Antworten buildFrage(String frage, String[] content, boolean[] loesung) {
            return new Fragen_Antworten(frage, content, loesung);
        }


    }
}
