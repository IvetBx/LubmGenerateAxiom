Najjednoduchsie je si to otvorit cez nejake prostredie (IntelliJ a podobne), pretoze v tejto verzii je to spravene iba tak, ze sa nenacitavaju vstupy od pouzivatela cez konzolu, ale su tam v premennych priradene, cize to bude najlahsie, ak chceme zmenit vstupy (plus, cez konzolu aspon u mna su nejake problemy so spustenim, lebo to tam nevie najst cesty k niecomu, ale tak to som zatial nepokladala za potrebne riesit, ak sa to len jednorazovo pouzije, ak by neskor bolo potrebne, tak skusim pozriet, kde je chyba)

Subor obsahuje 3 triedy:
ClassWithSubclasses
- spracuvava subor lubm-classes-with-subclasses.txt, ktory obsahuje iba URI (na kazdom riadku jedno) tried, ktore su v LUBM a maju nejaku podtriedu; je to rucne vytvoreny subor, kedze zatial sme sa dohodli, ze to takto staci
- v podstate urobi kombinacie vsetkych tried dlzky n do pola, to nasledne zamiesa a vyberie prvych y kombinacii (n aj y pouzivatel zada na vstupe) 
 
Main (tato trieda sa spusta)
- v nej je presne popisane ktora cast kodu co robi, cize tu iba k vstupom a vystupom napisem komentar
- je potrebne do premennych zadat nejake hodnoty, ktore sa potom poslu na vstup pri spusteni programu:
fileWithOntology - nazov suboru, v ktorom mame ulozenu ontologiu, ktoru ideme upravovat (momentalne je tam lubm-125.owl, tento subor je potrebne mat v tom najvrchnejsom adresari, teda v LubmGenerateAxiom, pripade zadat tam absolutnu cestu k priecinku)
POZN. pri tejto ontologii som musela na druhom riadku 
<rdf:RDF xmlns="owlapi:ontology#ont24525195307693" odstranit # na konci, ktory tam bol, pretoze to nechcelo parsovat inak ontologiu, v protege to aj bez toho islo riadne otvarat, cize by to nemal byt problem, avsak ak by ste tam davali iny subor LUBM a vyhodilo by to chybu s nespravnym tvarom URI, tak toto zrejme treba odstranit

numberOfIndividuals - toto je tam iba kvoli vytvaraniu nazvu modifikovanej ontologie

fileWithClasses - to je subor s tymi triedami, ktory som vyssie popisovala (momentalne je tam lubm-classes-with-subclasses.txt, co sa pravdepodobne ani nebude menit, pokial budeme pracovat s LUBM)

newFolder - tu je potrebne zadat nazov priecinka, do ktoreho sa nam po zbehnuti programu ulozia vsetky modifikovane ontologie aj s input subormi, ktore im prisluchaju (je potrebne na konci nazvu dat aj znak: /); po jednom behu programu sa tento priecinok vytvori, ale ked budete spustat opakovane program, tak tento nazov treba vzdy zmenit, to aby sa nam neprepisal obsah, ked uz raz si vygenerujeme ontologie po jednom behu (ked ho odstranime, tak samozrejme sa to da spustat s rovnakym nazvom priecinka)

uriNewClass - toto asi defaultne bude rovnake ako momentalne: "http://swat.cse.lehigh.edu/onto/univ-bench.owl#DummyClass" (je to iba URI novej triedy, do ktorej budeme individual zaradzovat)

n - dlzka n-tic, teda v axiome DummyClass \ekvivalent C1 \and C2 ... \and Cn je to pocet tried C1 az Cn je to pocet tried ktore davame do prieniku

y - pocet takychto n-tic, co v podstate bude zodpovedat aj poctu vygenerovanych modifikovanych ontologii

InputFileForMHS_MXP
- trieda ktora pre jednu konkretnu upravenu ontologiu vytvori input file pre jeden beh MHS_MXP algoritmu (takisto bude ulozeny v priecinku, kde sa ukladaju aj upravene ontologie, s rovnakym nazvom, aky ma prisluchajuca ontologia, iba s s priponou .txt namiesto .owl a na konci nazvu je este _input)

Na spustenie u seba je potrebna aj kninica jena, a kedze ona nie je priamo v projekte, tak asi ju bude treba stiahnut, ale to vlastne iba z tejto stranky:
https://jena.apache.org/download/index.cgi
stiahnut apache-jena-3.17.0.zip no a nasledne ju priadat do projektu
(v IntelliJ to islo cez File/Project Structure/Global Libraries a tam cez + si staci najst kde je ten rozzipovany priecinok s Jena, v nom najst lib a nasledne dat Apply a potvrdit)

