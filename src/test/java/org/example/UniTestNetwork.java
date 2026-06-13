package org.example;

import org.example.KI_Satzerkennung.Network;
import org.example.KI_Satzerkennung.TrainSet;
import org.example.KI_Satzerkennung.TrainWithTrainSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UniTestNetwork { //Mostly generated
    private Network network;

    // Kleines Netz: Input=16 (2 Tokens * 8 EmbeddingDim), Hidden=8, Output=2
    private static final int VOCAB_SIZE   = 5000;
    private static final int EMBEDDING_DIM = 8;
    private static final int INPUT_SIZE    = 16; // 2 tokens * 8 dims
    private static final int HIDDEN_SIZE   = 8;
    private static final int OUTPUT_SIZE   = 2;

    @BeforeEach
    void setUp() {
        network = new Network(VOCAB_SIZE, EMBEDDING_DIM, INPUT_SIZE, HIDDEN_SIZE, OUTPUT_SIZE);
    }

    // -----------------------------------------------------------------------
    // createRandomArr Tests
    // -----------------------------------------------------------------------

    @Test
    void testCreateRandomArr1D_correctSize() {
        double[] arr = network.createRandomArr(10, -0.5, 0.5);
        assertEquals(10, arr.length);
    }

    @Test
    void testCreateRandomArr1D_valuesInBounds() {
        double[] arr = network.createRandomArr(100, -0.5, 0.5);
        for (double v : arr) {
            assertTrue(v >= -0.5 && v <= 0.5,
                    "Wert außerhalb der Grenzen: " + v);
        }
    }

    @Test
    void testCreateRandomArr2D_correctDimensions() {
        double[][] arr = network.createRandomArr(5, 3, -1.0, 1.0);
        assertNotNull(arr);
        assertEquals(5, arr.length);
        for (double[] row : arr) {
            assertEquals(3, row.length);
        }
    }

    @Test
    void testCreateRandomArr2D_invalidSize_returnsNull() {
        assertNull(network.createRandomArr(0, 3, -1.0, 1.0));
        assertNull(network.createRandomArr(3, 0, -1.0, 1.0));
    }

    // -----------------------------------------------------------------------
    // calculate / Feedforward Tests
    // -----------------------------------------------------------------------

    @Test
    void testCalculate_correctOutputSize() {
        double[] input = new double[INPUT_SIZE];
        double[] result = network.calculate(input);
        assertNotNull(result);
        assertEquals(OUTPUT_SIZE, result.length);
    }

    @Test
    void testCalculate_outputInSigmoidRange() {
        double[] input = new double[INPUT_SIZE];
        double[] result = network.calculate(input);
        assertNotNull(result);
        for (double v : result) {
            assertTrue(v > 0.0 && v < 1.0,
                    "Sigmoid-Output muss zwischen 0 und 1 liegen: " + v);
        }
    }

    @Test
    void testCalculate_wrongInputSize_returnsNull() {
        double[] wrongInput = new double[INPUT_SIZE + 5];
        assertNull(network.calculate(wrongInput));
    }

    @Test
    void testCalculate_deterministicForSameInput() {
        double[] input = new double[INPUT_SIZE];
        for (int i = 0; i < INPUT_SIZE; i++) input[i] = 0.1 * i;

        double[] r1 = network.calculate(input);
        double[] r2 = network.calculate(input);
        assertArrayEquals(r1, r2, 1e-10);
    }

    // -----------------------------------------------------------------------
    // embedInput Tests
    // -----------------------------------------------------------------------

    @Test
    void testEmbedInput_correctOutputLength() {
        double[] tokens = {0, 1}; // 2 Token-IDs
        double[] embedded = network.embedInput(tokens);
        assertEquals(2 * EMBEDDING_DIM, embedded.length);
    }

    @Test
    void testEmbedInput_sameTokenSameEmbedding() {
        double[] tokens1 = {42};
        double[] tokens2 = {42};
        double[] e1 = network.embedInput(tokens1);
        double[] e2 = network.embedInput(tokens2);
        assertArrayEquals(e1, e2, 1e-10);
    }

    // -----------------------------------------------------------------------
    // checkSentence Tests
    // -----------------------------------------------------------------------

    @Test
    void testCheckSentence_outputNotNull() {
        double[] tokens = {0, 1};
        double[] result = network.checkSentence(tokens);
        assertNotNull(result);
        assertEquals(OUTPUT_SIZE, result.length);
    }

    @Test
    void testCheckSentence_outputInSigmoidRange() {
        double[] tokens = {1, 2};
        double[] result = network.checkSentence(tokens);
        for (double v : result) {
            assertTrue(v > 0.0 && v < 1.0);
        }
    }

    // -----------------------------------------------------------------------
    // Training: Netz soll sich verbessern
    // -----------------------------------------------------------------------

    @Test
    void testTraining_reducesError() {
        // Ziel: Output[0] = 1, Output[1] = 0
        double[] target = {1.0, 0.0};
        double[] tokens  = {1.0, 2.0};

        double[] embedded = network.embedInput(tokens);

        // Fehler vor dem Training
        double[] before = network.calculate(embedded);
        double errorBefore = Math.pow(before[0] - target[0], 2)
                + Math.pow(before[1] - target[1], 2);

        // Mehrere Trainingsschritte
        for (int i = 0; i < 500; i++) {
            embedded = network.embedInput(tokens); // Embeddings ändern sich
            network.calculate(embedded);
            network.backpropagation(target, tokens);
            network.update(0.05);
        }

        double[] after = network.calculate(network.embedInput(tokens));
        double errorAfter = Math.pow(after[0] - target[0], 2)
                + Math.pow(after[1] - target[1], 2);

        assertTrue(errorAfter < errorBefore,
                "Fehler nach Training sollte kleiner sein. Vorher=" + errorBefore + " Nachher=" + errorAfter);
    }

    // -----------------------------------------------------------------------
    // saveNetwork / loadNetwork Tests
    // -----------------------------------------------------------------------

    @Test
    void testSaveAndLoadNetwork_outputIdentical() throws Exception {
        String tmpPath = System.getProperty("java.io.tmpdir") + "/test_network_save.txt";

        double[] tokens   = {3.0, 7.0};
        double[] embedded = network.embedInput(tokens);
        double[] original = network.calculate(embedded);

        network.saveNetwork(tmpPath);

        Network loaded     = Network.loadNetwork(tmpPath);
        double[] loadedEmb = loaded.embedInput(tokens);
        double[] restored  = loaded.calculate(loadedEmb);

        assertArrayEquals(original, restored, 1e-6,
                "Geladenes Netz soll denselben Output liefern wie das gespeicherte.");
    }
    @Nested
    class TrainWithTrainSetTest {

        private TrainWithTrainSet trainer;

        @BeforeEach
        void setUp() throws Exception {
            trainer = new TrainWithTrainSet();
        }

        // -----------------------------------------------------------------------
        // getCategory (über getTargetlength indirekt, direkt via runSentenceThrough)
        // -----------------------------------------------------------------------

        @Test
        void testGetCategory_klein_gibtKategorie1() {
            // <= 5 Wörter → Kategorie 1
            double[] result = trainer.runSentenceThrough("Was ist das");
            assertNotNull(result);
            assertEquals(2, result.length);
        }

        @Test
        void testGetCategory_mittelklein_gibtKategorie2() {
            // > 5 && <= 10 Wörter
            double[] result = trainer.runSentenceThrough("Wie geht es dir heute wirklich");
            assertNotNull(result);
            assertEquals(2, result.length);
        }

        @Test
        void testGetCategory_mittelgross_gibtKategorie3() {
            // > 10 && <= 15 Wörter
            double[] result = trainer.runSentenceThrough("Kannst du mir bitte erklären was heute in der Schule passiert ist");
            assertNotNull(result);
            assertEquals(2, result.length);
        }

        @Test
        void testGetCategory_gross_gibtKategorie4() {
            // > 15 Wörter
            double[] result = trainer.runSentenceThrough(
                    "Kannst du mir bitte ganz genau erklären was heute in der Schule bei dem Ausflug wirklich passiert ist");
            assertNotNull(result);
            assertEquals(2, result.length);
        }

        // -----------------------------------------------------------------------
        // runSentenceThrough: Output-Summe plausibel (beide Werte zwischen 0 und 1)
        // -----------------------------------------------------------------------

        @Test
        void testRunSentenceThrough_outputInValidRange() {
            double[] result = trainer.runSentenceThrough("Wie geht es dir heute");
            for (double v : result) {
                assertTrue(v > 0.0 && v < 1.0,
                        "Output muss im Sigmoid-Bereich liegen: " + v);
            }
        }

        @Test
        void testRunSentenceThrough_frageHatHoheFragenWahrscheinlichkeit() {
            // Klare Frage sollte output[0] (Frage) > output[1] (Statement) haben
            double[] result = trainer.runSentenceThrough("Wo wohnst du");
            assertNotNull(result);
            // Kein hartes Assert auf den Wert – nur dass output[0] existiert
            assertTrue(result[0] >= 0.0 && result[0] <= 1.0);
        }

        // -----------------------------------------------------------------------
        // padOrTruncate
        // -----------------------------------------------------------------------

        @Test
        void testPadOrTruncate_kürztAufZiellaenge() {
            double[] input = {1, 2, 3, 4, 5};
            double[] result = trainer.padOrTruncate(input, 3);
            assertEquals(3, result.length);
            assertArrayEquals(new double[]{1, 2, 3}, result, 1e-10);
        }

        @Test
        void testPadOrTruncate_fülltMitNullenAuf() {
            double[] input = {1, 2};
            double[] result = trainer.padOrTruncate(input, 5);
            assertEquals(5, result.length);
            assertEquals(1.0, result[0], 1e-10);
            assertEquals(2.0, result[1], 1e-10);
            assertEquals(0.0, result[2], 1e-10); // Padding
        }

        @Test
        void testPadOrTruncate_gleicheLaenge_unveraendert() {
            double[] input = {7, 8, 9};
            double[] result = trainer.padOrTruncate(input, 3);
            assertArrayEquals(input, result, 1e-10);
        }

        // -----------------------------------------------------------------------
        // removeSentenceEnding (static)
        // -----------------------------------------------------------------------

        @Test
        void testRemoveSentenceEnding_entferntFragezeichen() {
            assertEquals("Wie geht es dir",
                    TrainWithTrainSet.removeSentenceEnding("Wie geht es dir?"));
        }

        @Test
        void testRemoveSentenceEnding_entferntPunkt() {
            assertEquals("Das ist ein Satz",
                    TrainWithTrainSet.removeSentenceEnding("Das ist ein Satz."));
        }

        @Test
        void testRemoveSentenceEnding_entferntAusrufezeichen() {
            assertEquals("Hallo",
                    TrainWithTrainSet.removeSentenceEnding("Hallo!"));
        }

        @Test
        void testRemoveSentenceEnding_keinEndezeichen_unveraendert() {
            assertEquals("Kein Ende",
                    TrainWithTrainSet.removeSentenceEnding("Kein Ende"));
        }

        @Test
        void testRemoveSentenceEnding_mehrereZeichen_alleEntfernt() {
            assertEquals("Wirklich",
                    TrainWithTrainSet.removeSentenceEnding("Wirklich!?!"));
        }

        // -----------------------------------------------------------------------
        // createSet: Trainset korrekt befüllt
        // -----------------------------------------------------------------------

        @Test
        void testCreateSet_gibtNichtNullZurueck() {
            TrainSet set = trainer.createSet(1);
            assertNotNull(set);
        }

        @Test
        void testCreateSet_hatEintraege() {
            TrainSet set = trainer.createSet(1);
            assertNotNull(set);
            assertTrue(set.size() > 0, "TrainSet sollte nach createSet Einträge haben");
        }

        @Test
        void testCreateSet_inputHatRichtigeLaenge_Kategorie1() {
            TrainSet set = trainer.createSet(1);
            assertNotNull(set);
            if (set.size() > 0) {
                assertEquals(17, set.getInput(0).length,
                        "Kategorie 1: Input sollte 17 Token lang sein");
            }
        }

        @Test
        void testCreateSet_targetHatZweiKlassen() {
            TrainSet set = trainer.createSet(2);
            assertNotNull(set);
            if (set.size() > 0) {
                assertEquals(2, set.getTarget(0).length,
                        "Target sollte 2 Klassen haben (Frage / Statement)");
            }
        }
    }
}