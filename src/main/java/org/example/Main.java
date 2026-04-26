package org.example;

import GUI.Aplication;
import GUI.Oberflaeche;
import KI_Satzerkennung.FindAnswersAndQuestions;
import KI_Satzerkennung.TrainWithTrainSet;

import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        FindAnswersAndQuestions f = new FindAnswersAndQuestions();
        AI_Operations a = new AI_Operations();
//        String reintext = a.anz_Fragen(15,"Ein Computer (englisch; deutsche Aussprache [kɔmˈpjuːtɐ]) oder Rechner ist ein Gerät, das mittels programmierbarer Rechenvorschriften Daten verarbeitet. Dementsprechend werden vereinzelt auch die abstrahierenden beziehungsweise veralteten, synonym gebrauchten Begriffe Rechenanlage, Datenverarbeitungsanlage oder elektronische Datenverarbeitungsanlage sowie Elektronengehirn verwendet.\n" +
//                "\n" +
//                "Charles Babbage und Ada Lovelace (geborene Byron) gelten durch die von Babbage 1837 entworfene Rechenmaschine Analytical Engine als Vordenker des modernen universell programmierbaren Computers. Konrad Zuse (Z3, 1941 und Z4, 1945) in Berlin, John Presper Eckert und John William Mauchly (ENIAC, 1946) bauten die ersten funktionstüchtigen Geräte dieser Art. Bei der Klassifizierung eines Geräts als universell programmierbarer Computer spielt die Turing-Vollständigkeit eine wesentliche Rolle. Sie ist benannt nach dem englischen Mathematiker Alan Turing, der 1936 das logische Modell der Turingmaschine eingeführt hatte.[1][2]\n" +
//                "\n" +
//                "Die frühen Computer wurden auch (Groß-)Rechner genannt; ihre Ein- und Ausgabe der Daten war zunächst auf Zahlen beschränkt. Zwar verstehen sich moderne Computer auf den Umgang mit weiteren Daten, beispielsweise mit Buchstaben und Tönen. Diese Daten werden jedoch innerhalb des Computers in Zahlen umgewandelt und als solche verarbeitet, weshalb ein Computer auch heute eine Rechenmaschine ist.\n" +
//                "\n" +
//                "Mit zunehmender Leistungsfähigkeit eröffneten sich neue Einsatzbereiche. Computer sind heute in allen Bereichen des täglichen Lebens vorzufinden, meistens in spezialisierten Varianten, die auf einen vorliegenden Anwendungszweck zugeschnitten sind. So dienen integrierte Kleinstcomputer (eingebettetes System) zur Steuerung von Alltagsgeräten wie Waschmaschinen und Videorekordern oder zur Münzprüfung in Warenautomaten; in modernen Automobilen dienen sie beispielsweise zur Anzeige von Fahrdaten und steuern in „Fahrassistenten“ diverse Manöver selbst.\n" +
//                "\n" +
//                "Universelle Computer finden sich in Smartphones und Spielkonsolen. Personal Computer (engl. für persönliche Computer, als Gegensatz zu von vielen genutzten Großrechnern) dienen der Informationsverarbeitung in Wirtschaft und Behörden sowie bei Privatpersonen; Supercomputer werden eingesetzt, um komplexe Vorgänge zu simulieren, z. B. in der Klimaforschung oder für medizinische Berechnungen.");
        String tmp = "Hier sind 15 Fragen zu dem Infotext:\n" +
                "\n" +
                "1. Was bedeutet \"Computer\" im deutschen Sprachgebrauch?\n" +
                "a) Elektronisches Gehirn\n" +
                "b) Gerät zur Datenverarbeitung\n" +
                "c) Rechenmaschine\n" +
                "\n" +
                "Antwort: b) Gerät zur Datenverarbeitung\n" +
                "\n" +
                "2. Wer ist durch die Entwurf einer Rechenmaschine als Vordenker des modernen universell programmierbaren Computers bekannt?\n" +
                "a) Ada Lovelace\n" +
                "b) Charles Babbage\n" +
                "c) Alan Turing\n" +
                "\n" +
                "Antwort: b) Charles Babbage\n" +
                "\n" +
                "3. Was wurde 1946 von John Presper Eckert und John William Mauchly entwickelt?\n" +
                "a) Z1\n" +
                "b) ENIAC\n" +
                "c) Z4\n" +
                "\n" +
                "Antwort: b) ENIAC\n" +
                "\n" +
                "4. Welche Eigenschaft ist für die Klassifizierung eines Geräts als universell programmierbarer Computer wichtig?\n" +
                "a) Leistungsfähigkeit\n" +
                "b) Turing-Vollständigkeit\n" +
                "c) Größe\n" +
                "\n" +
                "Antwort: b) Turing-Vollständigkeit\n" +
                "\n" +
                "5. Wie wurden frühe Computer auch bekannt?\n" +
                "a) Groß-Rechner\n" +
                "b) Rechenmaschinen\n" +
                "c) Datenverarbeitungsgeräte\n" +
                "\n" +
                "Antwort: a) Groß-Rechner\n" +
                "\n" +
                "6. Welche Art von Daten konnten frühe Computer nur verarbeiten?\n" +
                "a) Buchstaben und Töne\n" +
                "b) Zahlen und Bilder\n" +
                "c) Wörter und Musik\n" +
                "\n" +
                "Antwort: c) Zahlen\n" +
                "\n" +
                "7. Welches sind die neuen Einsatzbereiche, die sich durch zunehmende Leistungsfähigkeit eröffnet haben?\n" +
                "a) Steuerung von Alltagsgeräten und Münzprüfung in Warenautomaten\n" +
                "b) Simulation komplexer Vorgänge in der Klimaforschung und medizinischen Berechnungen\n" +
                "c) Erstellung von Spielen für Spielkonsolen\n" +
                "\n" +
                "Antwort: a) Steuerung von Alltagsgeräten und Münzprüfung in Warenautomaten\n" +
                "\n" +
                "8. Welche Art von Computer findet sich in Smartphones und Spielkonsolen?\n" +
                "a) Universeller Computer\n" +
                "b) Personalcomputer\n" +
                "c) Supercomputer\n" +
                "\n" +
                "Antwort: a) Universeller Computer\n" +
                "\n" +
                "9. Was sind die Hauptanwendungsbereiche von universellen Computern?\n" +
                "a) Wirtschaft, Behörden und Privatpersonen\n" +
                "b) Klimaforschung, medizinische Berechnungen und Alltagsgeräte\n" +
                "c) Spielkonsolen, Smartphones und Erstellung von Spielen\n" +
                "\n" +
                "Antwort: a) Wirtschaft, Behörden und Privatpersonen\n" +
                "\n" +
                "10. Wer ist nach dem englischen Mathematiker benannt, der das logische Modell der Turingmaschine eingeführt hatte?\n" +
                "a) Alan Turing\n" +
                "b) Charles Babbage\n" +
                "c) Ada Lovelace\n" +
                "\n" +
                "Antwort: a) Alan Turing\n" +
                "\n" +
                "11. Was sind die Hauptmerkmale von modernen Computern?\n" +
                "a) Rechenmaschinen mit Einschränkung auf Zahlen\n" +
                "b) Geräte zur Datenverarbeitung, die auch Buchstaben und Töne verarbeiten können\n" +
                "c) Elektronische Gehirne\n" +
                "\n" +
                "Antwort: b) Geräte zur Datenverarbeitung, die auch Buchstaben und Töne verarbeiten können\n" +
                "\n" +
                "12. Wer entwickelte die Rechenmaschine Analytical Engine?\n" +
                "a) Charles Babbage und Ada Lovelace\n" +
                "b) John Presper Eckert und John William Mauchly\n" +
                "c) Konrad Zuse\n" +
                "\n" +
                "Antwort: a) Charles Babbage und Ada Lovelace\n" +
                "\n" +
                "13. Was war der Name des von Alan Turing 1936 eingeführten logischen Modells?\n" +
                "a) Turingmaschine\n" +
                "b) Rechenmaschine\n" +
                "c) Analytical Engine\n" +
                "\n" +
                "Antwort: a) Turingmaschine\n" +
                "\n" +
                "14. Welche Art von Computer dient zur Steuerung von Alltagsgeräten wie Waschmaschinen und Videorekordern?\n" +
                "a) Universeller Computer\n" +
                "b) Personalcomputer\n" +
                "c) integriertes Kleinstcomputer (eingebettetes System)\n" +
                "\n" +
                "Antwort: c) integriertes Kleinstcomputer (eingebettetes System)\n" +
                "\n" +
                "15. Was ist ein Supercomputer eingesetzt, um zu simulieren?\n" +
                "a) Klimaforschung und medizinische Berechnungen\n" +
                "b) Steuerung von Alltagsgeräten und Münzprüfung in Warenautomaten\n" +
                "c) Erstellung von Spielen für Spielkonsolen\n" +
                "Antwort: a) Klimaforschung und medizinische Berechnungen";

        System.out.println(tmp);
        Fragen_Antworten[] test = f.find(tmp,15,3);

        System.out.println(Arrays.toString(test));
    }
}